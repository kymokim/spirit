package com.kymokim.spirit.common.service;

import com.kymokim.spirit.common.dto.ResponseVersionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VersionService {

    @Value("${app.version}")
    private String version;

    @Value("${app.force-update}")
    private Boolean forceUpdate;

    public ResponseVersionDto.CheckVersionDto checkVersion(String clientVersion) {
        Boolean isVersionMatched = version.equals(clientVersion);
        return ResponseVersionDto.CheckVersionDto.toDto(isVersionMatched, isVersionMatched ? false : forceUpdate);
    }
}
