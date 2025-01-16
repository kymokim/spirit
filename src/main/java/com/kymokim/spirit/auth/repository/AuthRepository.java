package com.kymokim.spirit.auth.repository;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.SocialInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Auth findBySocialInfo(SocialInfo socialInfo);
    Auth findByNickname(String nickname);
}
