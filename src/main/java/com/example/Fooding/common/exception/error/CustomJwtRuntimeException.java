package com.example.Fooding.common.exception.error;

import com.example.Fooding.common.exception.ErrorCode;

public class CustomJwtRuntimeException extends RuntimeException{
    public CustomJwtRuntimeException(){
        super(ErrorCode.AUTHENTICATION_FAILED.getMessage());
    }
}
