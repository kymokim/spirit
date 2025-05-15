package com.kymokim.spirit.report.controller;


import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.report.dto.RequestReport;
import com.kymokim.spirit.report.dto.ResponseReport;
import com.kymokim.spirit.report.entity.ReportStatus;
import com.kymokim.spirit.report.entity.ReportTarget;
import com.kymokim.spirit.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Report API")
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private final ReportService reportService;
    private final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

    @PostMapping(value = "/create")
    public ResponseEntity<ResponseDto> createReport(@RequestBody RequestReport.CreateReportRqDto createReportRqDto){
        LOGGER.info("Report/createReport API called.");
        reportService.createReport(createReportRqDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Report created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "신고된 가게 리스트 조회")
    @GetMapping("/store")
    public ResponseEntity<ResponseDto> getStoreReports(@PageableDefault(size = 10) Pageable pageable,
                                                       @RequestParam(value = "status") ReportStatus reportStatus) {
        LOGGER.info("Report/getStoreReports API called.");
        Page<ResponseReport.StoreReportListDto> dtoPage = reportService.getStoreReports(pageable, reportStatus);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Reported store list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "특정 타겟(store, review) 신고 전체 리스트 조회")
    @GetMapping("/get-by/target/{targetId}")
    public ResponseEntity<ResponseDto> getReportsByTargetId(@RequestParam(value = "target")ReportTarget reportTarget, @PathVariable Long targetId) {
        LOGGER.info("Report/getReportsByTargetId API called.");
        List<ResponseReport.ReportDto> dtoList = reportService.getReportsByTargetId(reportTarget, targetId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Reported by target list retrieved successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "우선 신고 사유가 있는 가게 신고 리스트 조회")
    @GetMapping("/store/priority")
    public ResponseEntity<ResponseDto> getPriorityStoreReports(@PageableDefault(size = 10) Pageable pageable,
                                                               @RequestParam(value = "status") ReportStatus reportStatus) {
        LOGGER.info("Report/getPriorityStoreReports API called.");
        Page<ResponseReport.StoreReportListDto> dtoPage = reportService.getPriorityStoreReports(pageable, reportStatus);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Reported priority store list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }




    @Operation(summary = "신고된 리뷰 리스트 조회")
    @GetMapping("/review")
    public ResponseEntity<ResponseDto> getReviewReports(@PageableDefault(size = 10) Pageable pageable,
                                                        @RequestParam(value = "status") ReportStatus reportStatus) {
        LOGGER.info("Report/getReviewReports API called.");
        Page<ResponseReport.ReviewReportListDto> dtoPage = reportService.getReviewReports(pageable, reportStatus);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Reported review list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "신고 처리 완료")
    @PatchMapping("complete/{reportId}")
    public ResponseEntity<ResponseDto> completeReport(@PathVariable Long reportId) {
        LOGGER.info("Report/completeReport API called.");
        reportService.completeReport(reportId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Report completed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "신고 보관")
    @PostMapping("archive/{reportId}")
    public ResponseEntity<ResponseDto> archiveReport(@PathVariable Long reportId, @RequestBody RequestReport.ArchiveReportDto archiveReportDto) {
        LOGGER.info("Report/archiveReport API called.");
        reportService.archiveReport(reportId, archiveReportDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Report archived successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
