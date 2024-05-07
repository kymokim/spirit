package com.kymokim.spirit.common.exception.error;

import com.kymokim.spirit.common.exception.ErrorCode;

public class RegisterFailedException extends RuntimeException{
    public RegisterFailedException(){
        super(ErrorCode.AUTHENTICATION_CONFLICT.getMessage());
    }
}