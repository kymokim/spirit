package com.kymokim.spirit.auth.auth.entity;

import com.kymokim.spirit.auth.auth.exception.AuthErrorCode;
import com.kymokim.spirit.common.exception.CustomException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Data
@NoArgsConstructor
public class SocialInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "social_id", nullable = false)
    private String socialId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auth_id")
    private Auth auth;

    @Builder
    public SocialInfo(SocialType socialType, String socialId) {
        setSocialType(socialType);
        setSocialId(socialId);
    }

    private void setSocialType(SocialType socialType){
        if (socialType == null){
            throw new CustomException(AuthErrorCode.USER_SOCIAL_TYPE_EMPTY);
        }
        this.socialType = socialType;
    }

    private void setSocialId(String socialId){
        if (socialId == null || socialId.isEmpty()){
            throw new CustomException(AuthErrorCode.USER_SOCIAL_ID_EMPTY);
        }
        this.socialId = socialId;
    }
}
