package com.kymokim.spirit.common.service;

import com.kymokim.spirit.common.dto.ResponseVersionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VersionService {

    @Value("${app.version.latest}")
    private String latestVersion;

    @Value("${app.version.required}")
    private String requiredVersion;

    @Value("${app.test.mode}")
    private Boolean testMode;

    public ResponseVersionDto.CheckVersionDto checkVersion(String clientVersion) {

        Boolean isTestEnabled = clientVersion.equals(latestVersion) && testMode;

        int[] client = parseVersion(clientVersion);
        int[] server = parseVersion(requiredVersion);

        for (int i = 0; i < 3; i++) {
            if (client[i] > server[i]) {
                return ResponseVersionDto.CheckVersionDto.builder().isUpdateRequired(false).isTestEnabled(isTestEnabled).build();
            } else if (client[i] < server[i]) {
                return ResponseVersionDto.CheckVersionDto.builder().isUpdateRequired(true).isTestEnabled(isTestEnabled).build();
            }
        }
        return ResponseVersionDto.CheckVersionDto.builder().isUpdateRequired(false).isTestEnabled(isTestEnabled).build();
    }

    private static int[] parseVersion(String version) {
        String[] parts = version.split("\\.");
        int[] numbers = new int[3]; // major.minor.patch 기본 3자리

        for (int i = 0; i < Math.min(parts.length, 3); i++) {
            try {
                numbers[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                numbers[i] = 0; // 숫자 아닌 경우는 0 처리
            }
        }
        return numbers;
    }
}
