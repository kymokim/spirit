package com.example.Fooding.common.exception.error;

import com.example.Fooding.common.exception.ErrorCode;

public class NotFoundUserException extends RuntimeException{
    public NotFoundUserException(){
        super(ErrorCode.NOT_FOUND_USER.getMessage());
    }
}