package com.kymokim.spirit.drink.exception;

import com.kymokim.spirit.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DrinkErrorCode implements ErrorCode {
    DRINK_NOT_FOUND(HttpStatus.NOT_FOUND, 24000, "DRINK_NOT_FOUND"),
    DRINK_ORIGIN_IMG_URL_EMPTY(HttpStatus.BAD_REQUEST, 24001, "DRINK_ORIGIN_IMG_URL_EMPTY"),
    DRINK_IMG_FILE_EMPTY(HttpStatus.BAD_REQUEST, 24002, "DRINK_IMG_FILE_EMPTY");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    DrinkErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
