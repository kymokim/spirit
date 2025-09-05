package com.kymokim.spirit.log.controller;

import com.kymokim.spirit.auth.entity.Gender;
import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.log.dto.ResponseLog;
import com.kymokim.spirit.log.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<ResponseDto> getStoreViewLogStats(
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

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "매장 권한 승인 통계")
    @GetMapping("/ownership/stats")
    public ResponseEntity<ResponseDto> getOwnershipStats(
            @RequestParam String period,
            @RequestParam String showBy
    ) {
        List<ResponseLog.OwnershipStatListDto> dtoList = logService.getOwnershipStats(period, showBy);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Ownership stats retrieved successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
