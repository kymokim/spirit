package com.kymokim.spirit.review.controller;

import com.kymokim.spirit.auth.security.JwtAuthTokenProvider;
import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.review.dto.RequestReview;
import com.kymokim.spirit.review.dto.ResponseReview;
import com.kymokim.spirit.review.service.ReviewService;
import com.kymokim.spirit.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createReview(@RequestBody RequestReview.CreateReviewDto createReviewDto, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        reviewService.createReview(createReviewDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/{storeId}")
    public ResponseEntity<ResponseDto> getReviewByStoreId(@PathVariable("storeId") Store storeId) {
        List<ResponseReview.GetReviewDto> response = reviewService.getReviewByStoreId(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateReview(@RequestBody RequestReview.UpdateReviewDto updateReviewDto) {
        reviewService.updateReview(updateReviewDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<ResponseDto> deleteReview(@PathVariable("reviewId") Long reviewId, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        reviewService.deleteReview(reviewId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
