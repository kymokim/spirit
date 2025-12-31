package com.kymokim.spirit.post.repository;

import com.kymokim.spirit.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    Optional<PostImage> findByUrl(String url);
}
