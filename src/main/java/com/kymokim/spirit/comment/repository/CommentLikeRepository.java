package com.kymokim.spirit.comment.repository;

import com.kymokim.spirit.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    CommentLike findByCommentIdAndUserId(Long commentId, Long userId);
    void deleteByUserId(Long userId);
    void deleteByCommentId(Long commentId);
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);
}
