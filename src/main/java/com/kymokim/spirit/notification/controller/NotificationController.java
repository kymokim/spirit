package com.kymokim.spirit.notification.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.notification.dto.ResponseNotification;
import com.kymokim.spirit.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    @PatchMapping("/read/{notificationId}")
    public ResponseEntity<ResponseDto> readNotification(@PathVariable("notificationId") Long notificationId) {
        LOGGER.info("Notification/readNotification API called.");
        notificationService.readNotification(notificationId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Notification read successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PatchMapping("/read/all")
    public ResponseEntity<ResponseDto> readAllNotification() {
        LOGGER.info("Notification/readAllNotification API called.");
        notificationService.readAllNotification();
        ResponseDto responseDto = ResponseDto.builder()
                .message("All notification read successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-received")
    public ResponseEntity<ResponseDto> getReceivedNotification(@PageableDefault(size = 20) Pageable pageable) {
        LOGGER.info("Notification/getReceivedNotification API called.");
        Page<ResponseNotification.NotificationResponseDto> notificationResponseDtoPage = notificationService.getReceivedNotification(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Received notification fetched successfully.")
                .data(notificationResponseDtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
