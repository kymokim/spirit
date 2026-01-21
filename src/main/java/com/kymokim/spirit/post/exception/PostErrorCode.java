package com.kymokim.spirit.post.exception;

import com.kymokim.spirit.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PostErrorCode implements ErrorCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, 25000, "POST_NOT_FOUND"),
    POST_ORIGIN_IMG_URL_EMPTY(HttpStatus.BAD_REQUEST, 25001, "POST_ORIGIN_IMG_URL_EMPTY"),
    POST_IMG_FILE_EMPTY(HttpStatus.BAD_REQUEST, 25002, "POST_IMG_FILE_EMPTY"),
    NOT_POST_WRITER(HttpStatus.BAD_REQUEST, 25003, "NOT_POST_WRITER"),
    POST_DAILY_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, 25005, "POST_DAILY_LIMIT_EXCEEDED"),
    POST_ALREADY_WRITTEN_TODAY(HttpStatus.BAD_REQUEST, 25006, "POST_ALREADY_WRITTEN_TODAY"),
    POST_IMG_FILE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, 25007, "POST_IMG_FILE_LIMIT_EXCEEDED");


    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    PostErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
