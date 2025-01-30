package com.kymokim.spirit.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {
    OLD_IMG_URL_EMPTY(HttpStatus.BAD_REQUEST, 20000, "OLD_IMG_URL_EMPTY"),
    NEW_FILE_CREATE_FAILED(HttpStatus.BAD_REQUEST, 20001, "NEW_FILE_CREATE_FAILED"),
    FILE_CONVERSION_FAILED(HttpStatus.BAD_REQUEST, 20002, "FILE_CONVERSION_FAILED"),
    CREATOR_ID_EMPTY(HttpStatus.BAD_REQUEST, 20003, "CREATOR_ID_EMPTY");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    CommonErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
