package com.kymokim.spirit.event.exception;

import com.kymokim.spirit.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum EventErrorCode implements ErrorCode {
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, 26000, "EVENT_NOT_FOUND"),
    INVALID_EVENT_PERIOD(HttpStatus.BAD_REQUEST, 26001, "INVALID_EVENT_PERIOD"),
    EVENT_IMG_FILE_EMPTY(HttpStatus.BAD_REQUEST, 26002, "EVENT_IMG_FILE_EMPTY"),
    EVENT_ORIGIN_IMG_URL_EMPTY(HttpStatus.BAD_REQUEST, 26003, "EVENT_ORIGIN_IMG_URL_EMPTY");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    EventErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
