package com.kymokim.spirit.liveReview.controller;

import com.kymokim.spirit.auth.security.JwtAuthTokenProvider;
import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.liveReview.dto.RequestLiveReview;
import com.kymokim.spirit.liveReview.dto.ResponseLiveReview;
import com.kymokim.spirit.liveReview.service.LiveReviewService;
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
@RequestMapping("/api/liveReview")
public class LiveReviewController {

    private final LiveReviewService liveReviewService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createLiveReview(@RequestBody RequestLiveReview.CreateLiveReviewDto createLiveReviewDto, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        liveReviewService.createLiveReview(createLiveReviewDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("LiveReview created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/{storeId}")
    public ResponseEntity<ResponseDto> getLiveReviewByStoreId(@PathVariable("storeId") Store storeId) {
        List<ResponseLiveReview.GetLiveReviewDto> response = liveReviewService.getLiveReviewByStoreId(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("LiveReview list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Deprecated
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateLiveReview(@RequestBody RequestLiveReview.UpdateLiveReviewDto updateLiveReviewDto) {
        liveReviewService.updateLiveReview(updateLiveReviewDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("LiveReview updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Deprecated
    @DeleteMapping("/delete/{liveReviewId}")
    public ResponseEntity<ResponseDto> deleteLiveReview(@PathVariable("liveReviewId") Long liveReviewId) {
        liveReviewService.deleteLiveReview(liveReviewId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("LiveReview deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("{/delete/all}")
    public ResponseEntity<ResponseDto> deleteAllLiveReview() {
        liveReviewService.deleteAllLiveReview();
        ResponseDto responseDto = ResponseDto.builder()
                .message("All liveReview deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
