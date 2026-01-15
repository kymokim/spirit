package com.kymokim.spirit.post.dto;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.post.entity.Post;
import com.kymokim.spirit.store.dto.ResponseStore;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResponsePost {

    @Builder
    @Getter
    public static class GetPostDto {
        private Long id;
        private Long writerId;
        private String writerNickname;
        private String writerImgUrl;
        private LocalDateTime createdAt;
        private String content;
        private List<String> postImgUrlList;
        private Long likeCount;
        private Long commentCount;
        private Long shareCount;
        private Boolean isLiked;
        private Boolean isSaved;

        private Long storeId;
        private String storeName;
        private Boolean isStoreDeleted;
        private Double rate;
        private String place;

        public static GetPostDto toDto(Post post, Auth writer, boolean isLiked, boolean isSaved) {

            List<String> postImgUrlList = new ArrayList<>();
            if (!post.getImageList().isEmpty()) {
                post.getImageList().forEach(postImage -> postImgUrlList.add(postImage.getUrl()));
            }

            return GetPostDto.builder()
                    .id(post.getId())
                    .writerId(writer.getId())
                    .writerNickname(writer.getNickname())
                    .writerImgUrl(writer.getImgUrl())
                    .content(post.getContent())
                    .rate(post.getRate())
                    .createdAt(post.getHistoryInfo().getCreatedAt())
                    .storeId(post.getStore() == null ? null : post.getStore().getId())
                    .storeName(post.getStore() == null ? null : post.getStore().getName())
                    .isStoreDeleted(post.getStore() == null ? null : post.getStore().getIsDeleted())
                    .place(post.getPlace() == null ? null : post.getPlace())
                    .postImgUrlList(postImgUrlList)
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .shareCount(post.getShareCount())
                    .isLiked(isLiked)
                    .isSaved(isSaved)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetPostByStoreDto {
        private Long id;
        private String writerNickname;
        private String writerImgUrl;
        private String content;
        private Double rate;
        private LocalDateTime createdAt;
        private Boolean isWriter;
        private List<String> postImgUrlList;

        public static GetPostByStoreDto toDto(Post post, Auth writer, Long userId) {

            List<String> postImgUrlList = new ArrayList<>();
            if (!post.getImageList().isEmpty()) {
                post.getImageList().forEach(postImage -> postImgUrlList.add(postImage.getUrl()));
            }

            return GetPostByStoreDto.builder()
                    .id(post.getId())
                    .writerNickname(writer.getNickname())
                    .writerImgUrl(writer.getImgUrl())
                    .content(post.getContent())
                    .rate(post.getRate())
                    .createdAt(post.getHistoryInfo().getCreatedAt())
                    .isWriter(Objects.equals(writer.getId(), userId))
                    .postImgUrlList(postImgUrlList)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetMyPostDto {
        private Long id;
        private String content;
        private Double rate;
        private LocalDateTime createdAt;
        private List<String> postImgUrlList;
        private String address;
        private Long storeId;
        private String storeName;
        private Boolean isStoreDeleted;
        private String place;
        private Long likeCount;
        private Long commentCount;
        private Long shareCount;
        private Boolean isLiked;
        private Boolean isSaved;

        public static GetMyPostDto toDto(Post post, boolean isLiked, boolean isSaved) {

            List<String> postImgUrlList = new ArrayList<>();
            if (!post.getImageList().isEmpty()) {
                post.getImageList().forEach(postImage -> postImgUrlList.add(postImage.getUrl()));
            }

            return GetMyPostDto.builder()
                    .id(post.getId())
                    .content(post.getContent())
                    .rate(post.getRate())
                    .createdAt(post.getHistoryInfo().getCreatedAt())
                    .address(post.getStore() == null ? null : post.getStore().getLocation().getAddress())
                    .storeId(post.getStore() == null ? null : post.getStore().getId())
                    .storeName(post.getStore() == null ? null : post.getStore().getName())
                    .isStoreDeleted(post.getStore() == null ? null : post.getStore().getIsDeleted())
                    .place(post.getPlace() == null ? null : post.getPlace())
                    .postImgUrlList(postImgUrlList)
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .shareCount(post.getShareCount())
                    .isLiked(isLiked)
                    .isSaved(isSaved)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetRecentPostDto {
        private Long id;
        private String content;
        private Double rate;
        private Long writerId;
        private String writerNickname;
        private String writerImgUrl;
        private Boolean isWriter;
        private LocalDateTime createdAt;
        private List<String> postImgUrlList;
        private String address;
        private Long storeId;
        private String storeName;
        private Boolean isStoreDeleted;
        private String place;
        private Long likeCount;
        private Long commentCount;
        private Long shareCount;
        private Boolean isLiked;
        private Boolean isSaved;

        public static GetRecentPostDto toDto(Post post, Auth writer, Long userId, boolean isLiked, boolean isSaved) {

            List<String> postImgUrlList = new ArrayList<>();
            if (!post.getImageList().isEmpty()) {
                post.getImageList().forEach(postImage -> postImgUrlList.add(postImage.getUrl()));
            }

            return GetRecentPostDto.builder()
                    .id(post.getId())
                    .content(post.getContent())
                    .rate(post.getRate())
                    .writerId(writer.getId())
                    .writerNickname(writer.getNickname())
                    .writerImgUrl(writer.getImgUrl())
                    .isWriter(Objects.equals(writer.getId(), userId))
                    .createdAt(post.getHistoryInfo().getCreatedAt())
                    .address(post.getStore() == null ? null : post.getStore().getLocation().getAddress())
                    .storeId(post.getStore() == null ? null : post.getStore().getId())
                    .storeName(post.getStore() == null ? null : post.getStore().getName())
                    .isStoreDeleted(post.getStore() == null ? null : post.getStore().getIsDeleted())
                    .place(post.getPlace() == null ? null : post.getPlace())
                    .postImgUrlList(postImgUrlList)
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .shareCount(post.getShareCount())
                    .isLiked(isLiked)
                    .isSaved(isSaved)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetSavedPostDto {
        private Long id;
        private String content;
        private Double rate;
        private Long writerId;
        private String writerNickname;
        private String writerImgUrl;
        private Boolean isWriter;
        private LocalDateTime createdAt;
        private List<String> postImgUrlList;
        private String address;
        private Long storeId;
        private String storeName;
        private Boolean isStoreDeleted;
        private String place;
        private Long likeCount;
        private Long commentCount;
        private Long shareCount;
        private Boolean isLiked;
        private Boolean isSaved;

        public static GetSavedPostDto toDto(Post post, Auth writer, Long userId, boolean isLiked, boolean isSaved) {

            List<String> postImgUrlList = new ArrayList<>();
            if (!post.getImageList().isEmpty()) {
                post.getImageList().forEach(postImage -> postImgUrlList.add(postImage.getUrl()));
            }

            return GetSavedPostDto.builder()
                    .id(post.getId())
                    .content(post.getContent())
                    .rate(post.getRate())
                    .writerId(writer.getId())
                    .writerNickname(writer.getNickname())
                    .writerImgUrl(writer.getImgUrl())
                    .isWriter(Objects.equals(writer.getId(), userId))
                    .createdAt(post.getHistoryInfo().getCreatedAt())
                    .address(post.getStore() == null ? null : post.getStore().getLocation().getAddress())
                    .storeId(post.getStore() == null ? null : post.getStore().getId())
                    .storeName(post.getStore() == null ? null : post.getStore().getName())
                    .isStoreDeleted(post.getStore() == null ? null : post.getStore().getIsDeleted())
                    .place(post.getPlace() == null ? null : post.getPlace())
                    .postImgUrlList(postImgUrlList)
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .shareCount(post.getShareCount())
                    .isLiked(isLiked)
                    .isSaved(isSaved)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SharePostDto {
        private String shareLink;

        public static ResponsePost.SharePostDto toDto(String shareLink) {
            return ResponsePost.SharePostDto.builder()
                    .shareLink(shareLink)
                    .build();
        }
    }
}
