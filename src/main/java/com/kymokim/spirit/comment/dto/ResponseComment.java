package com.kymokim.spirit.comment.dto;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

public class ResponseComment {

    @Getter
    @Builder
    public static class GetRootCommentsDto {

        private Long id;
        private String content;
        private Long replyCount;
        private String writerNickname;
        private String writerImgUrl;
        private LocalDateTime createdAt;
        private Boolean isWriter;

        public static GetRootCommentsDto toDto(Comment comment, Auth writer, Long userId) {
            return GetRootCommentsDto.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .replyCount(comment.getReplyCount())
                    .writerNickname(writer.getNickname())
                    .writerImgUrl(writer.getImgUrl())
                    .createdAt(comment.getHistoryInfo().getCreatedAt())
                    .isWriter(Objects.equals(writer.getId(), userId))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetReplyCommentsDto {

        private Long id;
        private String content;
        private String writerNickname;
        private String writerImgUrl;
        private LocalDateTime createdAt;
        private Boolean isWriter;

        public static GetReplyCommentsDto toDto(Comment comment, Auth writer, Long userId) {
            return GetReplyCommentsDto.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .writerNickname(writer.getNickname())
                    .writerImgUrl(writer.getImgUrl())
                    .createdAt(comment.getHistoryInfo().getCreatedAt())
                    .isWriter(Objects.equals(writer.getId(), userId))
                    .build();
        }
    }
}
