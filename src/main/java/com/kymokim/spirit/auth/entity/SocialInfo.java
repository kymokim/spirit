package com.kymokim.spirit.auth.entity;

import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.common.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
@Embeddable
public class SocialInfo {
    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType type;

    @Column(name = "social_id", nullable = false)
    private String id;

    protected SocialInfo(){
    }

    @Builder
    public SocialInfo(SocialType type, String id) {
        setType(type);
        setId(id);
    }

    private void setType(SocialType type){
        if (type == null){
            throw new CustomException(AuthErrorCode.USER_SOCIAL_TYPE_EMPTY);
        }
        this.type = type;
    }

    private void setId(String id){
        if (id == null || id.isEmpty()){
            throw new CustomException(AuthErrorCode.USER_SOCIAL_ID_EMPTY);
        }
        this.id = id;
    }
}
