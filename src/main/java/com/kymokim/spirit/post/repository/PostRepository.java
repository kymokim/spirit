package com.kymokim.spirit.post.repository;

import com.kymokim.spirit.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByStoreIdOrderByHistoryInfo_CreatedAtDesc(Long storeId, Pageable pageable);
    Page<Post> findAllByHistoryInfo_CreatorIdOrderByHistoryInfo_CreatedAtDesc(Long creatorId, Pageable pageable);
    Page<Post> findAllByOrderByHistoryInfo_CreatedAtDesc(Pageable pageable);
    long countByHistoryInfo_CreatorIdAndHistoryInfo_CreatedAtBetween(Long creatorId, LocalDateTime startInclusive, LocalDateTime endInclusive);
    boolean existsByHistoryInfo_CreatorIdAndStoreIdAndHistoryInfo_CreatedAtBetween(Long creatorId, Long storeId, LocalDateTime startInclusive, LocalDateTime endInclusive);
}
