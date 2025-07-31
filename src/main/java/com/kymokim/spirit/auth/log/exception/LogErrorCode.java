package com.kymokim.spirit.auth.log.exception;

import com.kymokim.spirit.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LogErrorCode implements ErrorCode {
    INVALID_PERIOD(HttpStatus.BAD_REQUEST, 28000, "INVALID_PERIOD"),
    INVALID_GROUP_BY(HttpStatus.BAD_REQUEST, 28001, "INVALID_GROUP_BY"),
    INVALID_SHOW_BY(HttpStatus.BAD_REQUEST, 28002, "INVALID_SHOW_BY");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    LogErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
