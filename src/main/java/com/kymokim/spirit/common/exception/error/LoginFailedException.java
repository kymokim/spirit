package com.kymokim.spirit.common.exception.error;

import com.kymokim.spirit.common.exception.ErrorCode;

public class LoginFailedException extends RuntimeException{
    public LoginFailedException(){
        super(ErrorCode.LOGIN_FAILED.getMessage());
    }
}