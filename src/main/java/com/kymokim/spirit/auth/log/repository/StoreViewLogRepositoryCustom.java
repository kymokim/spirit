package com.kymokim.spirit.auth.log.repository;

import com.kymokim.spirit.auth.log.dto.RequestLog;
import com.kymokim.spirit.auth.log.dto.ResponseLog;

import java.util.List;

public interface StoreViewLogRepositoryCustom {
    List<ResponseLog.StoreViewLogStatListDto> getStoreViewLogStats(RequestLog.StoreViewLogStatFilter filter);
}
