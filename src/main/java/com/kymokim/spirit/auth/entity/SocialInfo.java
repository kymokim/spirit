package com.kymokim.spirit.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Embeddable
public class SocialInfo {
    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType type;

    @Column(name = "social_id", nullable = false)
    private String id;

    protected SocialInfo(){
    }

    public SocialInfo(SocialType type, String id) {
        this.type = type;
        this.id = id;
    }
}
