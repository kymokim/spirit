package com.kymokim.spirit.auth.entity;


import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.common.exception.CustomException;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Table(name = "auth")
@Entity
@Getter
@NoArgsConstructor
@Data
public class Auth implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Embedded
    private SocialInfo socialInfo;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "refresh_token", length = 1000)
    private String refreshToken;

    @Builder
    public Auth(SocialInfo socialInfo, String nickname, String imgUrl, String refreshToken){
        this.socialInfo = socialInfo;
        setNickname(nickname);
        this.roles.add(Role.USER);
        this.imgUrl = imgUrl;
        this.refreshToken = refreshToken;
    }

    public void setNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            throw new CustomException(AuthErrorCode.USER_NICKNAME_EMPTY);
        }
        this.nickname = nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    //sso 기반이니 비밀번호는 미사용
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * spring security에서 사용하는 principal 값(String)
     * jwt token에 들어가는 subject와 일치시키기 위해 id를 String으로 변환
     * @return String 타입 id
     */
    @Override
    public String getUsername() {
        return String.valueOf(this.id);
    }
}
