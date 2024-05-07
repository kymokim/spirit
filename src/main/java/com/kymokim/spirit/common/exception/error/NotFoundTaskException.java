package com.kymokim.spirit.common.exception.error;

import com.kymokim.spirit.common.exception.ErrorCode;

public class NotFoundTaskException extends RuntimeException{

    public NotFoundTaskException() { super(ErrorCode.NOT_FOUND_TASK.getMessage());}
}