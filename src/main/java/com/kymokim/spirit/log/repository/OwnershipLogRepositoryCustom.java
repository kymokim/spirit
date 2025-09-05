package com.kymokim.spirit.log.repository;

import com.kymokim.spirit.log.dto.RequestLog;
import com.kymokim.spirit.log.dto.ResponseLog;

import java.util.List;

public interface OwnershipLogRepositoryCustom {
    List<ResponseLog.OwnershipStatListDto> getOwnershipStats(RequestLog.OwnershipLogStatFilter filter);
}

