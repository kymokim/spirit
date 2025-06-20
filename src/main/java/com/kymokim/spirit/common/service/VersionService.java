package com.kymokim.spirit.common.service;

import com.kymokim.spirit.common.dto.ResponseVersionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VersionService {

    @Value("${app.version}")
    private String version;

    public ResponseVersionDto.CheckVersionDto checkVersion(String clientVersion) {
        int[] client = parseVersion(clientVersion);
        int[] server = parseVersion(version);

        for (int i = 0; i < 3; i++) {
            if (client[i] > server[i]) {
                return ResponseVersionDto.CheckVersionDto.builder().isUpdateRequired(false).build();
            } else if (client[i] < server[i]) {
                return ResponseVersionDto.CheckVersionDto.builder().isUpdateRequired(true).build();
            }
        }
        return ResponseVersionDto.CheckVersionDto.builder().isUpdateRequired(false).build();
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
