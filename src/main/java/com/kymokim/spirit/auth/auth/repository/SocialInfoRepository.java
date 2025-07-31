package com.kymokim.spirit.auth.auth.repository;

import com.kymokim.spirit.auth.auth.entity.SocialInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialInfoRepository extends JpaRepository<SocialInfo, Long> {
}
