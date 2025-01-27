package com.kymokim.spirit.review.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.auth.repository.AuthRepository;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.review.dto.RequestReview;
import com.kymokim.spirit.review.dto.ResponseReview;
import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.review.entity.ReviewImage;
import com.kymokim.spirit.review.exception.ReviewErrorCode;
import com.kymokim.spirit.review.repository.ReviewImageRepository;
import com.kymokim.spirit.review.repository.ReviewRepository;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.entity.StoreImage;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final AuthRepository authRepository;
    private final S3Service s3Service;

    private Store resolveStore(Long storeId){
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private Review resolveReview(Long reviewId){
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    private Long resolveUserId(){
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public void createReview(MultipartFile[] files, RequestReview.CreateReviewDto createReviewDto) {
        Long userId = resolveUserId();
        Auth writer = authRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
        Store store = resolveStore(createReviewDto.getStoreId());
        Review review = RequestReview.CreateReviewDto.toEntity(createReviewDto, store, writer.getId(), writer.getNickname());
        reviewRepository.save(review);

        if (files != null) {
            List<MultipartFile> fileList = Arrays.asList(files);
            List<String> imageUrls = s3Service.uploadMultiple(fileList, "review/" + String.valueOf(review.getId()));
            for (String url : imageUrls){
                ReviewImage reviewImage = ReviewImage.builder().url(url).review(review).build();
                reviewImageRepository.save(reviewImage);
                review.addImgUrlList(reviewImage);
            }
            reviewRepository.save(review);
        }
        Double totalRate = store.getTotalRate() + review.getRate();
        store.setTotalRate(totalRate);
        store.increaseReviewCount();
        storeRepository.save(store);
    }

    public ResponseReview.GetReviewDto getReview(Long reviewId){
        Review review = resolveReview(reviewId);
        return ResponseReview.GetReviewDto.toDto(review);
    }

    public List<ResponseReview.ReviewListDto> getReviewByStore(Long storeId) {
        List<Review> entityList = reviewRepository.findAllByStoreId(storeId);
        List<ResponseReview.ReviewListDto> dtoList = new ArrayList<>();
        entityList.forEach(review -> dtoList.add(ResponseReview.ReviewListDto.toDto(review)));
        return dtoList;
    }

    public void updateReview(Long reviewId, RequestReview.UpdateReviewDto updateReviewDto) {
        Review originalReview = resolveReview(reviewId);
        Review updatedReview = RequestReview.UpdateReviewDto.toEntity(originalReview, updateReviewDto);
        reviewRepository.save(updatedReview);
    }

    public void deleteReview(Long reviewId) {
        Long userId = resolveUserId();
        Review review = resolveReview(reviewId);
        if(review.getWriterId().equals(userId)) {
            reviewRepository.delete(review);
        } else
            throw new EntityNotFoundException();
        Store store = review.getStore();

        if (store.getReviewCount() > 0) {
            store.decreaseReviewCount();
        } else throw new RuntimeException("Review must be larger than 0.");

        Double totalRate = store.getTotalRate() - review.getRate();
        store.setTotalRate(totalRate);
        storeRepository.save(store);

    }
}
