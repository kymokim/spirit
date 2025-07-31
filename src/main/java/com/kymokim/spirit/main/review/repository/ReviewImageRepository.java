package com.kymokim.spirit.main.review.repository;

import com.kymokim.spirit.main.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    Optional<ReviewImage> findByUrl(String url);
}
