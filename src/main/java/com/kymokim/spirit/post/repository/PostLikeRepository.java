package com.kymokim.spirit.post.repository;

import com.kymokim.spirit.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    PostLike findByPostIdAndUserId(Long postId, Long userId);
    List<PostLike> findAllByUserId(Long userId);
    List<PostLike> findAllByPostId(Long postId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
