package com.kymokim.spirit.common.exception.error;

import com.kymokim.spirit.common.exception.ErrorCode;

public class ExistingNicknameException extends RuntimeException{
    public ExistingNicknameException(){
        super(ErrorCode.EXISTING_NICKNAME.getMessage());
    }
}
