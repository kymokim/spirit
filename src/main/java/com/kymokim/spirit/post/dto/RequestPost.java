package com.kymokim.spirit.post.dto;

import com.kymokim.spirit.post.entity.Post;
import com.kymokim.spirit.store.entity.Store;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;


public class RequestPost {

    @Data
    @Builder
    public static class CreatePostDto {
        @NotEmpty
        @Size(max = 3000)
        private String content;
        private Long storeId;
        private Double rate;
        @Size(max = 30)
        private String place;

        public Post toEntity(Store store, Long creatorId) {
            return Post.builder()
                    .content(this.content)
                    .rate(this.rate)
                    .creatorId(creatorId)
                    .store(store)
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdatePostDto {
        @NotEmpty
        private String content;
        private Double rate;
        public Post toEntity(Post post) {
            post.update(this.content, this.rate);
            return post;
        }
    }

    @Data
    @Builder
    public static class DeleteImageDto {
        @NotEmpty
        private List<String> imgUrlList;
    }
}
