package com.kymokim.spirit.main.review.service;

import com.kymokim.spirit.auth.auth.entity.Auth;
import com.kymokim.spirit.auth.auth.entity.Role;
import com.kymokim.spirit.auth.auth.service.AuthResolver;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.common.service.TransactionRetryUtil;
import com.kymokim.spirit.main.review.dto.RequestReview;
import com.kymokim.spirit.main.review.dto.ResponseReview;
import com.kymokim.spirit.main.review.entity.Review;
import com.kymokim.spirit.main.review.entity.ReviewImage;
import com.kymokim.spirit.main.review.exception.ReviewErrorCode;
import com.kymokim.spirit.main.review.repository.ReviewImageRepository;
import com.kymokim.spirit.main.review.repository.ReviewRepository;
import com.kymokim.spirit.main.store.entity.Store;
import com.kymokim.spirit.main.store.entity.StoreManager;
import com.kymokim.spirit.main.store.exception.StoreErrorCode;
import com.kymokim.spirit.main.store.repository.StoreManagerRepository;
import com.kymokim.spirit.main.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final StoreManagerRepository storeManagerRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final S3Service s3Service;

    private Store resolveStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private Review resolveReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    @Transactional
    public void createReview(MultipartFile[] files, RequestReview.CreateReviewDto createReviewDto) {
        Store store = resolveStore(createReviewDto.getStoreId());
        Review review = createReviewDto.toEntity(store, AuthResolver.resolveUserId());
        reviewRepository.save(review);

        if (files != null) {
            List<MultipartFile> fileList = Arrays.asList(files);
            List<String> imageUrls = s3Service.uploadMultiple(fileList, "review/" + String.valueOf(review.getId()));
            for (String url : imageUrls) {
                ReviewImage reviewImage = ReviewImage.builder().url(url).review(review).build();
                reviewImageRepository.save(reviewImage);
                review.addImgUrlList(reviewImage);
            }
            reviewRepository.save(review);
        }
        Double totalRate = store.getTotalRate() + review.getRate();
        store.setTotalRate(totalRate);
        store.increaseReviewCount();
        storeRepository.save(store);
    }


    @Transactional
    public void uploadImage(MultipartFile[] files, Long reviewId) {
        Review review = resolveReview(reviewId);
        if (files != null) {
            List<MultipartFile> fileList = Arrays.asList(files);
            List<String> imageUrls = s3Service.uploadMultiple(fileList, "review/" + String.valueOf(review.getId()));
            for (String url : imageUrls) {
                ReviewImage reviewImage = ReviewImage.builder().url(url).review(review).build();
                reviewImageRepository.save(reviewImage);
                review.addImgUrlList(reviewImage);
            }
        } else {
            throw new CustomException(ReviewErrorCode.REVIEW_IMG_FILE_EMPTY);
        }
        reviewRepository.save(review);
    }

    @Transactional
    public void deleteImage(RequestReview.DeleteImageDto deleteImageDto, Long reviewId) {
        Review review = resolveReview(reviewId);
        for (String imgUrl : deleteImageDto.getImgUrlList()) {
            ReviewImage reviewImage = reviewImageRepository.findByUrl(imgUrl)
                    .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_ORIGIN_IMG_URL_EMPTY));
            s3Service.deleteFile(imgUrl);
            reviewImageRepository.delete(reviewImage);
            review.removeImgUrlList(reviewImage);
        }
        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public ResponseReview.GetReviewDto getReview(Long reviewId) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Review review = resolveReview(reviewId);
            return ResponseReview.GetReviewDto.toDto(review);
        }, 3);
    }

    @Transactional(readOnly = true)
    public Page<ResponseReview.ReviewListDto> getReviewByStore(Long storeId, Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Review> reviewPage = reviewRepository.findAllByStoreIdOrderByHistoryInfo_CreatedAtDesc(storeId, pageable);
            Long userId = AuthResolver.resolveUserId();
            return reviewPage.map(review -> ResponseReview.ReviewListDto.toDto(review, Objects.equals(review.getWriterId(), userId)));
        }, 3);
    }

    @Transactional(readOnly = true)
    public Page<ResponseReview.GetRecentReviewDto> getRecentReview(Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Review> reviewPage = reviewRepository.findAllByWriterIdOrderByHistoryInfo_CreatedAtDesc(AuthResolver.resolveUserId(), pageable);
            return reviewPage.map(ResponseReview.GetRecentReviewDto::toDto);
        }, 3);
    }

    @Transactional
    public void updateReview(Long reviewId, RequestReview.UpdateReviewDto updateReviewDto) {
        Review originalReview = resolveReview(reviewId);
        Store store = originalReview.getStore();

        Double totalRate = store.getTotalRate() - originalReview.getRate() + updateReviewDto.getRate();
        store.setTotalRate(totalRate);
        storeRepository.save(store);

        Review updatedReview = updateReviewDto.toEntity(originalReview);
        updatedReview.getHistoryInfo().update(AuthResolver.resolveUserId());
        reviewRepository.save(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Auth user = AuthResolver.resolveUser();
        Review review = resolveReview(reviewId);
        if (!Objects.equals(review.getWriterId(), user.getId()) && !user.getRoles().contains(Role.ADMIN)) {
            throw new CustomException(ReviewErrorCode.NOT_REVIEW_WRITER);
        }
        if (!Objects.equals(review.getImgUrlList(), null) && !review.getImgUrlList().isEmpty()) {
            List<ReviewImage> toDelete = new ArrayList<>(review.getImgUrlList());
            for (ReviewImage reviewImage : toDelete) {
                s3Service.deleteFile(reviewImage.getUrl());
                reviewImageRepository.delete(reviewImage);
                review.removeImgUrlList(reviewImage);
            }
        }
        reviewRepository.delete(review);
        Store store = review.getStore();
        if (store.getReviewCount() > 0) {
            store.decreaseReviewCount();
            Double totalRate = store.getTotalRate() - review.getRate();
            store.setTotalRate(totalRate);
        }
        storeRepository.save(store);
    }

    @Transactional
    public void setReply(RequestReview.SetReplyDto setReplyDto) {
        Long userId = AuthResolver.resolveUserId();
        Review review = resolveReview(setReplyDto.getReviewId());
        List<StoreManager> storeManagerList = storeManagerRepository.findAllByStoreId(review.getStore().getId());
        boolean isManager = storeManagerList.stream()
                .anyMatch(manager -> manager.getUserId().equals(userId));
        if (!isManager) {
            throw new CustomException(ReviewErrorCode.REVIEW_REPLY_FORBIDDEN);
        }
        review.setReply(setReplyDto.getReply());
        review.setRepliedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    @Transactional
    public void deleteReply(Long reviewId) {
        Long userId = AuthResolver.resolveUserId();
        Review review = resolveReview(reviewId);
        List<StoreManager> storeManagerList = storeManagerRepository.findAllByStoreId(review.getStore().getId());
        boolean isManager = storeManagerList.stream()
                .anyMatch(manager -> manager.getUserId().equals(userId));
        if (!isManager) {
            throw new CustomException(ReviewErrorCode.REVIEW_REPLY_FORBIDDEN);
        }
        review.setReply(null);
        review.setRepliedAt(null);
        reviewRepository.save(review);
    }
}
