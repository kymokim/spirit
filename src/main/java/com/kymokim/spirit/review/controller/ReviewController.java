package com.kymokim.spirit.review.controller;

import com.kymokim.spirit.auth.controller.AuthController;
import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.review.dto.RequestReview;
import com.kymokim.spirit.review.dto.ResponseReview;
import com.kymokim.spirit.review.service.ReviewService;
import com.kymokim.spirit.store.entity.Store;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private final Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createReview(@RequestPart(value = "files", required = false) MultipartFile[] files,
                                                    @RequestPart(value = "createReviewDto") RequestReview.CreateReviewDto createReviewDto) {
        LOGGER.info("Review/createReview API called.");
        reviewService.createReview(files, createReviewDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/upload-image/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> uploadReviewImage(@RequestPart(value = "files", required = true) MultipartFile[] files,
                                                         @PathVariable("reviewId") Long reviewId) {
        LOGGER.info("Review/uploadReviewImage API called.");
        reviewService.uploadImage(files, reviewId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image uploaded successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete-image/{reviewId}")
    public ResponseEntity<ResponseDto> deleteReviewImage(@RequestBody RequestReview.DeleteImageDto deleteImageDto,
                                                         @PathVariable("reviewId") Long reviewId) {
        LOGGER.info("Review/deleteReviewImage API called.");
        reviewService.deleteImage(deleteImageDto, reviewId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/{reviewId}")
    public ResponseEntity<ResponseDto> getReview(@PathVariable("reviewId") Long reviewId) {
        LOGGER.info("Review/getReview API called.");
        ResponseReview.GetReviewDto response = reviewService.getReview(reviewId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //토큰 받아서 본인이 작성자인지 알려주는 boolean 값 리턴 필요
    @GetMapping("/get-by/store/{storeId}")
    public ResponseEntity<ResponseDto> getReviewByStore(@PathVariable("storeId") Long storeId,
                                                        @PageableDefault(size = 10) Pageable pageable) {
        LOGGER.info("Review/getReviewByStore API called.");
        Page<ResponseReview.ReviewListDto> response = reviewService.getReviewByStore(storeId, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/recent")
    public ResponseEntity<ResponseDto> getRecentReview(@PageableDefault(size = 10) Pageable pageable) {
        LOGGER.info("Review/getRecentReview API called.");
        Page<ResponseReview.GetRecentReviewDto> response = reviewService.getRecentReview(pageable);
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
        LOGGER.info("Review/updateReview API called.");
        reviewService.updateReview(reviewId, updateReviewDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<ResponseDto> deleteReview(@PathVariable("reviewId") Long reviewId) {
        LOGGER.info("Review/deleteReview API called.");
        reviewService.deleteReview(reviewId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Review deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
