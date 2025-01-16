package com.kymokim.spirit.review.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.auth.repository.AuthRepository;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.review.dto.RequestReview;
import com.kymokim.spirit.review.dto.ResponseReview;
import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.review.repository.ReviewRepository;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final AuthRepository authRepository;

    public void createReview(RequestReview.CreateReviewDto createReviewDto) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        Auth writer = authRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(createReviewDto.getStoreId()).get();
        if(store == null) {
            throw new EntityNotFoundException();
        }

        Review review = RequestReview.CreateReviewDto.toEntity(createReviewDto, store, writer.getId(), writer.getNickname());
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

    public void deleteReview(Long reviewId) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        Review review = reviewRepository.findById(reviewId).get();
        if(review.getWriterId().equals(userId)) {
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
