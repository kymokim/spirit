package com.kymokim.spirit.common.exception.error;

import com.kymokim.spirit.common.exception.ErrorCode;

public class ExistingEmailException extends RuntimeException{
    public ExistingEmailException(){
        super(ErrorCode.EXISTING_EMAIL.getMessage());
    }
}
