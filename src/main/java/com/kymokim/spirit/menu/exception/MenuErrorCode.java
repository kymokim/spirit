package com.kymokim.spirit.menu.exception;

import com.kymokim.spirit.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MenuErrorCode implements ErrorCode {
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, 23000, "MENU_NOT_FOUND"),
    MENU_ORIGIN_IMG_URL_EMPTY(HttpStatus.BAD_REQUEST, 23001, "MENU_ORIGIN_IMG_URL_EMPTY");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    MenuErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
