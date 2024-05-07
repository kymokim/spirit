package com.kymokim.spirit.liveReview.repository;

import com.kymokim.spirit.liveReview.entity.LiveReview;
import com.kymokim.spirit.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveReviewRepository extends JpaRepository<LiveReview, Long> {
    List<LiveReview> findAllByStore(Store store);
}
