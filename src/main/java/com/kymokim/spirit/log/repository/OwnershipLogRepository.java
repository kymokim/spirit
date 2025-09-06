package com.kymokim.spirit.log.repository;

import com.kymokim.spirit.log.entity.OwnershipLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnershipLogRepository extends JpaRepository<OwnershipLog, Long>, OwnershipLogRepositoryCustom {
}

