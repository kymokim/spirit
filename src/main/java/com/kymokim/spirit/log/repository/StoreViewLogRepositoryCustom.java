package com.kymokim.spirit.log.repository;

import com.kymokim.spirit.log.dto.RequestLog;
import com.kymokim.spirit.log.dto.ResponseLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreViewLogRepositoryCustom {
    List<ResponseLog.StoreViewLogStatListDto> getStoreViewLogStats(RequestLog.StoreViewLogStatFilter filter);
    Page<Long> findViewedStoreIds(Long userId, Pageable pageable);
}
