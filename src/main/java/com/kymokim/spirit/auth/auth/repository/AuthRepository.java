package com.kymokim.spirit.auth.auth.repository;

import com.kymokim.spirit.auth.auth.entity.Auth;
import com.kymokim.spirit.auth.auth.entity.Role;
import com.kymokim.spirit.auth.auth.entity.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    @Query("SELECT a FROM Auth a JOIN a.socialInfoList s WHERE s.socialType = :socialType AND s.socialId = :socialId")
    Auth findBySocial(@Param("socialType") SocialType socialType, @Param("socialId") String socialId);

    List<Auth> findAllByRolesContaining(Role role);
}
