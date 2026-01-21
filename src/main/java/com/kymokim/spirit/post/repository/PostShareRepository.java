package com.kymokim.spirit.post.repository;

import com.kymokim.spirit.post.entity.PostShare;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostShareRepository extends JpaRepository<PostShare, Long> {
    void deleteByUserId(Long userId);
    void deleteByPostId(Long postId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
