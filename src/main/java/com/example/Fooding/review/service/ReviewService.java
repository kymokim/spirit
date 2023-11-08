package com.example.Fooding.review.service;

import com.example.Fooding.auth.repository.AuthRepository;
import com.example.Fooding.auth.security.JwtAuthToken;
import com.example.Fooding.auth.security.JwtAuthTokenProvider;
import com.example.Fooding.review.dto.RequestReview;
import com.example.Fooding.review.dto.ResponseReview;
import com.example.Fooding.review.entity.Review;
import com.example.Fooding.review.repository.ReviewRepository;
import com.example.Fooding.store.entity.Store;
import com.example.Fooding.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;

    public void createReview(RequestReview.CreateReviewDto createReviewDto, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long makerId = authRepository.findByEmail(email).getId();
        Store store = storeRepository.findById(createReviewDto.getStoreId()).get();
        if(store == null) {
            throw new EntityNotFoundException();
        }

        Review review = RequestReview.CreateReviewDto.toEntity(createReviewDto, store, makerId);
        reviewRepository.save(review);
    }

    public List<ResponseReview.GetReviewDto> getReviewByStoreId(Store storeId) {
        List<Review> entityList = reviewRepository.findAllByStore(storeId);
        List<ResponseReview.GetReviewDto> dtolist = new ArrayList<>();
        entityList.stream().forEach(review -> dtolist.add(ResponseReview.GetReviewDto.toDto(review)));
        return dtolist;
    }

    public void updateReview(RequestReview.UpdateReviewDto updateReviewDto) {
        Review originalReview = reviewRepository.findById(updateReviewDto.getReviewId()).get();
        Review updatedReview = RequestReview.UpdateReviewDto.toEntity(originalReview, updateReviewDto);
        reviewRepository.save(updatedReview);
    }

    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).get();
        reviewRepository.delete(review);
    }
}
