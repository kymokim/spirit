package com.kymokim.spirit.menu.exception;

import com.kymokim.spirit.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MenuErrorCode implements ErrorCode {
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, 23000, "MENU_NOT_FOUND"),
    MENU_ORIGIN_IMG_URL_EMPTY(HttpStatus.BAD_REQUEST, 23001, "MENU_ORIGIN_IMG_URL_EMPTY"),
    MENU_IMG_FILE_EMPTY(HttpStatus.BAD_REQUEST, 23002, "MENU_IMG_FILE_EMPTY"),
    INVALID_MENU_STORE_RELATION(HttpStatus.BAD_REQUEST, 23003, "INVALID_MENU_STORE_RELATION");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    MenuErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
