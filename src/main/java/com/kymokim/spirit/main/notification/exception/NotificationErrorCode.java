package com.kymokim.spirit.main.notification.exception;

import com.kymokim.spirit.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NotificationErrorCode implements ErrorCode {
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, 26000, "NOTIFICATION_NOT_FOUND"),
    NOTIFICATION_USER_UNMATCHED(HttpStatus.BAD_REQUEST, 26001, "NOTIFICATION_USER_UNMATCHED");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    NotificationErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
