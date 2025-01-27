package com.kymokim.spirit.review.exception;

import com.kymokim.spirit.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReviewErrorCode implements ErrorCode {
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, 25000, "REVIEW_NOT_FOUND"),
    REVIEW_ORIGIN_IMG_URL_EMPTY(HttpStatus.BAD_REQUEST, 25001, "REVIEW_ORIGIN_IMG_URL_EMPTY");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    ReviewErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
