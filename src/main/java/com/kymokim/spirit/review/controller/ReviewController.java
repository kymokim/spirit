package com.kymokim.spirit.review.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.review.dto.RequestReview;
import com.kymokim.spirit.review.dto.ResponseReview;
import com.kymokim.spirit.review.service.ReviewService;
import com.kymokim.spirit.store.entity.Store;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Tag(name = "Review API")
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createReview(@RequestPart(value = "files", required = false) MultipartFile[] files,
                                                    @RequestPart(value = "createReviewDto") RequestReview.CreateReviewDto createReviewDto) {
        reviewService.createReview(files, createReviewDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/{reviewId}")
    public ResponseEntity<ResponseDto> getReview(@PathVariable("reviewId") Long reviewId) {
        ResponseReview.GetReviewDto response = reviewService.getReview(reviewId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //토큰 받아서 본인이 작성자인지 알려주는 boolean 값 리턴 필요
    @GetMapping("/get-by/store/{storeId}")
    public ResponseEntity<ResponseDto> getReviewByStore(@PathVariable("storeId") Long storeId) {
        List<ResponseReview.ReviewListDto> response = reviewService.getReviewByStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //토큰 받아서 본인만 수정 가능하게 변경
    @PutMapping("/update/{reviewId}")
    public ResponseEntity<ResponseDto> updateReview(@PathVariable("reviewId") Long reviewId,
                                                    @RequestBody RequestReview.UpdateReviewDto updateReviewDto) {
        reviewService.updateReview(reviewId, updateReviewDto);
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
