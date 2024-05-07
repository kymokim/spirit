package com.kymokim.spirit.review.repository;

import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByStore(Store store);
    List<Review> findAllByWriterId(Long writerId);
}
