package com.kymokim.spirit.comment.dto;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.comment.entity.Comment;
import com.kymokim.spirit.post.entity.Post;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

public class RequestComment {

    @Getter
    @Builder
    public static class CreateCommentRqDto {
        @NotNull
        private Long postId;
        @NotEmpty
        private String content;
        private Long rootCommentId;
        private Long taggedUserId;

        public Comment toEntity(Post post, Long creatorId) {
            return Comment.builder()
                    .post(post)
                    .content(this.content)
                    .creatorId(creatorId)
                    .build();
        }
    }
}
