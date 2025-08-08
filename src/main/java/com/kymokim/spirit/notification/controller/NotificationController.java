package com.kymokim.spirit.notification.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.notification.dto.ResponseNotification;
import com.kymokim.spirit.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notification API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PatchMapping("/read/{notificationId}")
    public ResponseEntity<ResponseDto> readNotification(@PathVariable("notificationId") Long notificationId) {
        notificationService.readNotification(notificationId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Notification read successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PatchMapping("/read/all")
    public ResponseEntity<ResponseDto> readAllNotification() {
        notificationService.readAllNotification();
        ResponseDto responseDto = ResponseDto.builder()
                .message("All notification read successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-received")
    public ResponseEntity<ResponseDto> getReceivedNotification(@PageableDefault(size = 20) Pageable pageable) {
        Page<ResponseNotification.NotificationResponseDto> notificationResponseDtoPage = notificationService.getReceivedNotification(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Received notification fetched successfully.")
                .data(notificationResponseDtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
