package com.kymokim.spirit.main.store.repository;

import com.kymokim.spirit.main.store.entity.OperationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationInfoRepository extends JpaRepository<OperationInfo, Long> {
}
