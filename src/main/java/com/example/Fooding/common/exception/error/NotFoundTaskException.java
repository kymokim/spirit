package com.example.Fooding.common.exception.error;

import com.example.Fooding.common.exception.ErrorCode;

public class NotFoundTaskException extends RuntimeException{

    public NotFoundTaskException() { super(ErrorCode.NOT_FOUND_TASK.getMessage());}
}