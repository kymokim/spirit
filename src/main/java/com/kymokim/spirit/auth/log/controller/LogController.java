package com.kymokim.spirit.auth.log.controller;

import com.kymokim.spirit.auth.auth.entity.Gender;
import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.auth.log.dto.ResponseLog;
import com.kymokim.spirit.auth.log.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Log API")
@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @Operation(summary = "매장 조회 로그 통계", description = "groupBy field : ageGroup, gender, role")
    @GetMapping("/store-view/stats")
    public ResponseEntity<ResponseDto> getAccessLogStats(
            @RequestParam Long storeId,
            @RequestParam String period,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) List<String> ageGroup,
            @RequestParam List<String> groupBy,
            @RequestParam String showBy
    ) {
        List<ResponseLog.StoreViewLogStatListDto> dtoList =  logService.getStoreViewLogStats(storeId, period, gender, ageGroup, groupBy, showBy);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store view log stats retrieved successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}