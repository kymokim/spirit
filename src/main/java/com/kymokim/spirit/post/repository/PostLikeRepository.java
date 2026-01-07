package com.kymokim.spirit.post.repository;

import com.kymokim.spirit.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    PostLike findByPostIdAndUserId(Long postId, Long userId);
    void deleteByUserId(Long userId);
    void deleteByPostId(Long postId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
