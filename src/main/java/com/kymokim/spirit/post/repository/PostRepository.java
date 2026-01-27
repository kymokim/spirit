package com.kymokim.spirit.post.repository;

import com.kymokim.spirit.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByStoreIdAndIsDeletedFalseOrderByHistoryInfo_CreatedAtDesc(Long storeId, Pageable pageable);

    Page<Post> findAllByHistoryInfo_CreatorIdAndIsDeletedFalseOrderByHistoryInfo_CreatedAtDesc(Long creatorId, Pageable pageable);

    Page<Post> findAllByIsDeletedFalseOrderByBoostedAtDesc(Pageable pageable);

    long countByHistoryInfo_CreatorIdAndIsDeletedFalseAndHistoryInfo_CreatedAtBetween(Long creatorId, LocalDateTime startInclusive, LocalDateTime endInclusive);

    boolean existsByHistoryInfo_CreatorIdAndStoreIdAndIsDeletedFalseAndHistoryInfo_CreatedAtBetween(Long creatorId, Long storeId, LocalDateTime startInclusive, LocalDateTime endInclusive);

    @Query("""
                SELECT p
                FROM PostLike pl
                JOIN pl.post p
                WHERE pl.likedAt >= :from
                  AND p.isDeleted = false
                GROUP BY p
                ORDER BY COUNT(pl.id) DESC, p.likeCount DESC, p.boostedAt DESC
            """)
    Page<Post> findPopularPosts(@Param("from") LocalDateTime from, Pageable pageable);
}
