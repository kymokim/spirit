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

        Double totalRate = store.getTotalRate() + review.getRate();
        store.setTotalRate(totalRate);

        store.increaseReviewCount();
        storeRepository.save(store);

    }

    public List<ResponseReview.GetReviewDto> getReviewByStoreId(Store storeId) {
        List<Review> entityList = reviewRepository.findAllByStore(storeId);
        List<ResponseReview.GetReviewDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(review -> dtoList.add(ResponseReview.GetReviewDto.toDto(review)));
        return dtoList;
    }

    public void updateReview(RequestReview.UpdateReviewDto updateReviewDto) {
        Review originalReview = reviewRepository.findById(updateReviewDto.getReviewId()).get();
        Review updatedReview = RequestReview.UpdateReviewDto.toEntity(originalReview, updateReviewDto);
        reviewRepository.save(updatedReview);
    }

    public void deleteReview(Long reviewId, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long makerId = authRepository.findByEmail(email).getId();
        Review review = reviewRepository.findById(reviewId).get();
        if(review.getMakerId().equals(makerId)) {
            reviewRepository.delete(review);
        } else throw new EntityNotFoundException();
        Store store = review.getStore();

        if (store.getReviewCount() > 0) {
            store.decreaseReviewCount();
        } else throw new RuntimeException("Review must be larger than 0.");

        Double totalRate = store.getTotalRate() - review.getRate();
        store.setTotalRate(totalRate);
        storeRepository.save(store);

    }
}
