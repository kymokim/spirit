package com.kymokim.spirit.auth.repository;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.SocialInfo;
import com.kymokim.spirit.auth.entity.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    @Query("SELECT a FROM Auth a JOIN a.socialInfoList s WHERE s.socialType = :socialType AND s.socialId = :socialId")
    Auth findBySocial(@Param("socialType") SocialType socialType, @Param("socialId") String socialId);
}
