package com.example.Fooding.review.repository;

import com.example.Fooding.review.entity.Review;
import com.example.Fooding.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByStore(Store store);
    //List<Review> findAllByMakerId(Long makerId);
}
