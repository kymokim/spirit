package com.kymokim.spirit.post.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.Role;
import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.common.service.TransactionRetryUtil;
import com.kymokim.spirit.link.dto.LinkData;
import com.kymokim.spirit.link.dto.PathType;
import com.kymokim.spirit.link.service.LinkBuilder;
import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.notification.dto.post.PostCreatedNotificationEvent;
import com.kymokim.spirit.post.dto.RequestPost;
import com.kymokim.spirit.post.dto.ResponsePost;
import com.kymokim.spirit.post.entity.*;
import com.kymokim.spirit.post.exception.PostErrorCode;
import com.kymokim.spirit.post.repository.*;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@MainTransactional
public class PostService {
    private final StoreRepository storeRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final SavedPostRepository savedPostRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostShareRepository postShareRepository;
    private final S3Service s3Service;
    private final LinkBuilder linkBuilder;

    private Store resolveStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private Post resolvePost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));
    }

    private boolean isPostLiked(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    private boolean isPostSaved(Long postId, Long userId) {
        return savedPostRepository.existsByPostIdAndUserId(postId, userId);
    }


    private void validatePostCreationLimits(Long creatorId, Long storeId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        long postCountToday = postRepository.countByHistoryInfo_CreatorIdAndIsDeletedFalseAndHistoryInfo_CreatedAtBetween(creatorId, startOfDay, endOfDay);
        if (postCountToday > 20) {
            throw new CustomException(PostErrorCode.POST_DAILY_LIMIT_EXCEEDED);
        }

        if (storeId != null) {
            boolean alreadyPostedStoreToday = postRepository.existsByHistoryInfo_CreatorIdAndStoreIdAndIsDeletedFalseAndHistoryInfo_CreatedAtBetween(creatorId, storeId, startOfDay, endOfDay);
            if (alreadyPostedStoreToday) {
                throw new CustomException(PostErrorCode.POST_ALREADY_WRITTEN_TODAY);
            }
        }
    }

    private void validatePostWriterAccess(Post post, Auth user) {
        if (!Objects.equals(post.getHistoryInfo().getCreatorId(), user.getId()) && !user.getRoles().contains(Role.ADMIN)) {
            throw new CustomException(PostErrorCode.NOT_POST_WRITER);
        }
    }

    public void createPost(MultipartFile[] files, RequestPost.CreatePostDto createPostDto) {
        if (files == null || files.length == 0) {
            throw new CustomException(PostErrorCode.POST_IMG_FILE_EMPTY);
        }
        Store store = createPostDto.getStoreId() == null ? null : resolveStore(createPostDto.getStoreId());
        Long creatorId = AuthResolver.resolveUserId();
        validatePostCreationLimits(creatorId, createPostDto.getStoreId());
        Post post = createPostDto.toEntity(store, creatorId);
        if ((createPostDto.getStoreId() == null && createPostDto.getRate() == null) && !(createPostDto.getPlace() == null || createPostDto.getPlace().isEmpty())) {
            post.setPlace(createPostDto.getPlace());
        }
        postRepository.save(post);

        List<MultipartFile> fileList = Arrays.asList(files);
        List<String> imageUrls = s3Service.uploadMultiple(fileList, "post/" + String.valueOf(post.getId()));
        for (String url : imageUrls) {
            PostImage postImage = PostImage.builder().url(url).post(post).build();
            postImageRepository.save(postImage);
            post.addImageList(postImage);
        }
        postRepository.save(post);

        if (store != null && post.getRate() != null) {
            store.increaseTotalRate(post.getRate());
            store.increasePostCount();
            storeRepository.save(store);
            if (!store.getIsDeleted()) {
                NotificationEvent.raise(new PostCreatedNotificationEvent(store, post.getId()));
            }
        }
    }

    public void uploadImage(MultipartFile[] files, Long postId) {
        Post post = resolvePost(postId);
        Auth user = AuthResolver.resolveUser();
        validatePostWriterAccess(post, user);
        if (files != null && files.length > 0) {
            List<MultipartFile> fileList = Arrays.asList(files);
            List<String> imageUrls = s3Service.uploadMultiple(fileList, "post/" + String.valueOf(post.getId()));
            for (String url : imageUrls) {
                PostImage postImage = PostImage.builder().url(url).post(post).build();
                postImageRepository.save(postImage);
                post.addImageList(postImage);
            }
        } else {
            throw new CustomException(PostErrorCode.POST_IMG_FILE_EMPTY);
        }
        postRepository.save(post);
    }

    public void deleteImage(RequestPost.DeleteImageDto deleteImageDto, Long postId) {
        Post post = resolvePost(postId);
        Auth user = AuthResolver.resolveUser();
        validatePostWriterAccess(post, user);
        for (String imgUrl : deleteImageDto.getImgUrlList()) {
            PostImage postImage = postImageRepository.findByUrl(imgUrl)
                    .orElseThrow(() -> new CustomException(PostErrorCode.POST_ORIGIN_IMG_URL_EMPTY));
            s3Service.deleteFile(imgUrl);
            postImageRepository.delete(postImage);
            post.removeImageList(postImage);
        }
        postRepository.save(post);
    }

    public void updatePost(Long postId, RequestPost.UpdatePostDto updatePostDto) {
        Post originalPost = resolvePost(postId);
        Auth user = AuthResolver.resolveUser();
        validatePostWriterAccess(originalPost, user);
        Store store = originalPost.getStore();

        if (store != null && originalPost.getRate() != null && updatePostDto.getRate() != null) {
            Double totalRate = store.getTotalRate() - originalPost.getRate() + updatePostDto.getRate();
            store.setTotalRate(totalRate);
            storeRepository.save(store);
        }

        Post updatedPost = updatePostDto.toEntity(originalPost);
        updatedPost.getHistoryInfo().update(user.getId());
        postRepository.save(updatedPost);
    }

    public void deletePost(Long postId) {
        Auth user = AuthResolver.resolveUser();
        Post post = resolvePost(postId);
        validatePostWriterAccess(post, user);
        if (!Objects.equals(post.getImageList(), null) && !post.getImageList().isEmpty()) {
            List<PostImage> toDelete = new ArrayList<>(post.getImageList());
            for (PostImage postImage : toDelete) {
                s3Service.deleteFile(postImage.getUrl());
                postImageRepository.delete(postImage);
                post.removeImageList(postImage);
            }
        }
        savedPostRepository.deleteByPostId(postId);
        postLikeRepository.deleteByPostId(postId);
        postShareRepository.deleteByPostId(postId);
        Store store = post.getStore();
        if (store != null && post.getRate() != null) {
            store.decreaseTotalRate(post.getRate());
            store.decreasePostCount();
            storeRepository.save(store);
        }
        post.delete();
    }

    public void savePost(Long postId) {
        Post post = resolvePost(postId);
        Auth user = AuthResolver.resolveUser();
        SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(user.getId(), postId);
        if (savedPost == null) {
            savedPost = SavedPost.builder()
                    .post(post)
                    .userId(user.getId())
                    .build();
            savedPostRepository.save(savedPost);
        } else {
            savedPostRepository.delete(savedPost);
        }
        postRepository.save(post);
    }

    public void likePost(Long postId) {
        Post post = resolvePost(postId);
        Auth user = AuthResolver.resolveUser();
        PostLike postLike = postLikeRepository.findByPostIdAndUserId(postId, user.getId());
        if (postLike == null) {
            postLike = PostLike.builder()
                    .userId(user.getId())
                    .post(post)
                    .build();
            postLikeRepository.save(postLike);
            post.increaseLikeCount();
        } else {
            postLikeRepository.delete(postLike);
            post.decreaseLikeCount();
        }
    }

    public ResponsePost.SharePostDto sharePost(Long postId) {
        Post post = resolvePost(postId);
        Long userId = AuthResolver.resolveUserId();
        boolean isSharedBefore = postShareRepository.existsByPostIdAndUserId(postId, userId);
        if (!isSharedBefore) {
            PostShare postShare = PostShare.builder()
                    .userId(userId)
                    .post(post)
                    .build();
            postShareRepository.save(postShare);
            post.increaseShareCount();
        }
        LinkData.PathData pathData = LinkData.PathData.builder().type(PathType.POST).id(postId.toString()).build();
        return ResponsePost.SharePostDto.builder().shareLink(linkBuilder.serverLink(pathData)).build();
    }

    @MainTransactional(readOnly = true)
    public ResponsePost.GetPostDto getPost(Long postId) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Long userId = AuthResolver.resolveUserId();
            Post post = resolvePost(postId);
            return ResponsePost.GetPostDto.toDto(post, AuthResolver.resolveUser(post.getHistoryInfo().getCreatorId()), isPostLiked(postId, userId), isPostSaved(postId, userId));
        }, 3);
    }

    @MainTransactional(readOnly = true)
    public Page<ResponsePost.GetPostByStoreDto> getPostByStore(Long storeId, Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Long userId = AuthResolver.resolveUserId();
            Page<Post> postPage = postRepository.findAllByStoreIdAndIsDeletedFalseOrderByHistoryInfo_CreatedAtDesc(storeId, pageable);
            return postPage.map(post -> ResponsePost.GetPostByStoreDto.toDto(
                    post,
                    AuthResolver.resolveUser(post.getHistoryInfo().getCreatorId()),
                    userId
            ));
        }, 3);
    }

    @MainTransactional(readOnly = true)
    public Page<ResponsePost.GetMyPostDto> getMyPost(Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Long userId = AuthResolver.resolveUserId();
            Page<Post> postPage = postRepository.findAllByHistoryInfo_CreatorIdAndIsDeletedFalseOrderByHistoryInfo_CreatedAtDesc(AuthResolver.resolveUserId(), pageable);
            return postPage.map(post -> ResponsePost.GetMyPostDto.toDto(post, isPostLiked(post.getId(), userId), isPostSaved(post.getId(), userId)));
        }, 3);
    }

    @MainTransactional(readOnly = true)
    public Page<ResponsePost.GetRecentPostDto> getRecentPost(Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Long userId = AuthResolver.resolveUserId();
            Page<Post> postPage = postRepository.findAllByIsDeletedFalseOrderByHistoryInfo_CreatedAtDesc(pageable);
            return postPage.map(post -> ResponsePost.GetRecentPostDto.toDto(
                    post,
                    AuthResolver.resolveUser(post.getHistoryInfo().getCreatorId()),
                    userId,
                    isPostLiked(post.getId(), userId),
                    isPostSaved(post.getId(), userId)
            ));
        }, 3);
    }

    @MainTransactional(readOnly = true)
    public Page<ResponsePost.GetSavedPostDto> getSavedPost(Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Long userId = AuthResolver.resolveUserId();
            Page<Post> savedPostPage = savedPostRepository
                    .findAllByUserIdOrderByIdDesc(userId, pageable)
                    .map(SavedPost::getPost);
            return savedPostPage.map(post -> ResponsePost.GetSavedPostDto.toDto(
                    post,
                    AuthResolver.resolveUser(post.getHistoryInfo().getCreatorId()),
                    userId,
                    isPostLiked(post.getId(), userId),
                    isPostSaved(post.getId(), userId)
            ));
        }, 3);
    }
}
