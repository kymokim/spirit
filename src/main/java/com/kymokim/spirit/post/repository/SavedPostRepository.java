package com.kymokim.spirit.post.repository;

import com.kymokim.spirit.post.entity.SavedPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {

    SavedPost findByUserIdAndPostId(Long userId, Long postId);
    Page<SavedPost> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);
    void deleteByUserId(Long userId);
    void deleteByPostId(Long postId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
