package com.kymokim.spirit.comment.exception;

import com.kymokim.spirit.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommentErrorCode implements ErrorCode {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 26000, "COMMENT_NOT_FOUND"),
    NESTED_REPLY_NOT_ALLOWED(HttpStatus.BAD_REQUEST, 26001, "NESTED_REPLY_NOT_ALLOWED"),
    NOT_COMMENT_WRITER(HttpStatus.BAD_REQUEST, 26002, "NOT_COMMENT_WRITER");


    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    CommentErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
