package com.kymokim.spirit.report.exception;

import com.kymokim.spirit.common.exception.ErrorCode;
import com.kymokim.spirit.report.entity.ReportStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReportErrorCode implements ErrorCode {
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, 27000, "REPORT_NOT_FOUND"),
    REPORT_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, 27001, "REPORT_STATUS_NOT_FOUND");
    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    ReportErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}


