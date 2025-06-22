package com.kymokim.spirit.common.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.common.dto.ResponseLocationDto;
import com.kymokim.spirit.common.dto.ResponseVersionDto;
import com.kymokim.spirit.common.service.VersionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Version API")
@RestController
@RequestMapping("/api/version")
@RequiredArgsConstructor
public class VersionController {

    @Autowired
    private final VersionService versionService;

    @GetMapping("/check")
    public ResponseEntity<ResponseDto> checkVersion(@RequestParam String clientVersion) {
        ResponseVersionDto.CheckVersionDto response = versionService.checkVersion(clientVersion);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Version checked successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
