package com.example.Fooding.liveReview.repository;

import com.example.Fooding.liveReview.entity.LiveReview;
import com.example.Fooding.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveReviewRepository extends JpaRepository<LiveReview, Long> {
    List<LiveReview> findAllByStore(Store store);
}
