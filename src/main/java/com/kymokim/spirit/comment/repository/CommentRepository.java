package com.kymokim.spirit.comment.repository;

import com.kymokim.spirit.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostIdAndRootCommentIsNullAndIsDeletedFalse(Long postId, Pageable pageable);
    Page<Comment> findByRootCommentIdAndIsDeletedFalse(Long rootCommentId, Pageable pageable);
    List<Comment> findAllByRootCommentIdAndIsDeletedFalse(Long rootCommentId);

    long countByPostIdAndRootCommentIsNullAndIsDeletedFalseAndIdGreaterThan(Long postId, Long commentId);
    long countByRootCommentIdAndIsDeletedFalseAndIdLessThan(Long rootCommentId, Long commentId);
}
