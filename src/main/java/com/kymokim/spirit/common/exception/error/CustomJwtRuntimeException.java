package com.kymokim.spirit.common.exception.error;

import com.kymokim.spirit.common.exception.ErrorCode;

public class CustomJwtRuntimeException extends RuntimeException{
    public CustomJwtRuntimeException(){
        super(ErrorCode.AUTHENTICATION_FAILED.getMessage());
    }
}
