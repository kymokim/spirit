package com.kymokim.spirit.auth.auth.exception;

import com.kymokim.spirit.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements ErrorCode {
    USER_NICKNAME_EMPTY(HttpStatus.BAD_REQUEST, 21000, "USER_NICKNAME_EMPTY"),
    USER_ORIGIN_IMG_URL_EMPTY(HttpStatus.BAD_REQUEST, 21001, "USER_ORIGIN_IMG_URL_EMPTY"),
    REFRESH_TOKEN_MATCH_FAILED(HttpStatus.UNAUTHORIZED, 21002, "REFRESH_TOKEN_MATCH_FAILED"),
    USER_SOCIAL_INFO_EXISTS(HttpStatus.BAD_REQUEST, 21003, "USER_SOCIAL_INFO_EXISTS"),
    USER_NICKNAME_EXISTS(HttpStatus.BAD_REQUEST, 21004, "USER_NICKNAME_EXISTS"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 21005, "USER_NOT_FOUND"),
    USER_IMG_FILE_EMPTY(HttpStatus.BAD_REQUEST, 21006, "USER_IMG_FILE_EMPTY"),
    USER_SOCIAL_TYPE_EMPTY(HttpStatus.BAD_REQUEST, 21007, "USER_SOCIAL_TYPE_EMPTY"),
    USER_SOCIAL_ID_EMPTY(HttpStatus.BAD_REQUEST, 21008, "USER_SOCIAL_ID_EMPTY"),
    INVALID_SOCIAL_TOKEN(HttpStatus.BAD_REQUEST, 21009, "INVALID_SOCIAL_TOKEN"),
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, 21010, "ADMIN_NOT_FOUND"),
    PERSONAL_INFO_EMPTY(HttpStatus.BAD_REQUEST, 21011, "PERSONAL_INFO_EMPTY");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    AuthErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
