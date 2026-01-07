package com.kymokim.spirit.post.repository;

import com.kymokim.spirit.post.entity.PostShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostShareRepository extends JpaRepository<PostShare, Long> {
    List<PostShare> findAllByUserId(Long userId);
    List<PostShare> findAllByPostId(Long postId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
