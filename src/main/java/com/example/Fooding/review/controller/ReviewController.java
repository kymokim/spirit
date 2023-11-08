package com.example.Fooding.review.controller;

import com.example.Fooding.auth.security.JwtAuthTokenProvider;
import com.example.Fooding.common.dto.ResponseDto;
import com.example.Fooding.review.dto.RequestReview;
import com.example.Fooding.review.dto.ResponseReview;
import com.example.Fooding.review.service.ReviewService;
import com.example.Fooding.store.entity.Store;
import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<ResponseDto> deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
