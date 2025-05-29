package com.kymokim.spirit.auth.repository;

import com.kymokim.spirit.auth.entity.SocialInfo;
import com.kymokim.spirit.auth.entity.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialInfoRepository extends JpaRepository<SocialInfo, Long> {
}
