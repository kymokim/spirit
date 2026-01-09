package com.kymokim.spirit.report.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.report.dto.RequestReport;
import com.kymokim.spirit.report.dto.ResponseReport;
import com.kymokim.spirit.report.entity.ReportStatus;
import com.kymokim.spirit.report.entity.ReportTarget;
import com.kymokim.spirit.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Report API")
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private final ReportService reportService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/create")
    public ResponseEntity<ResponseDto> createReport(@Valid @RequestBody RequestReport.CreateReportRqDto createReportRqDto) {
        reportService.createReport(createReportRqDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Report created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "신고 처리 완료")
    @PatchMapping("complete/{reportId}")
    public ResponseEntity<ResponseDto> completeReport(@PathVariable Long reportId) {
        reportService.completeReport(reportId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Report completed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "신고 보관")
    @PostMapping("archive/{reportId}")
    public ResponseEntity<ResponseDto> archiveReport(@PathVariable Long reportId,
                                                     @Valid @RequestBody RequestReport.ArchiveReportDto archiveReportDto) {
        reportService.archiveReport(reportId, archiveReportDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Report archived successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "매장 운영자 제보 처리 완료")
    @PatchMapping("complete/store-manager/{reportId}")
    public ResponseEntity<ResponseDto> completeReportByStoreManager(@PathVariable Long reportId) {
        reportService.completeReportByStoreManager(reportId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Report completed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "매장 운영자 제보 조회")
    @GetMapping("/get-by/store-manager/{storeId}")
    public ResponseEntity<ResponseDto> getReportsByStoreManager(@PathVariable Long storeId) {
        List<ResponseReport.GetReportsByStoreManagerDto> dtoList = reportService.getReportsByStoreManager(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store report list retrieved successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "특정 타겟 대기중 신고 전체 조회", description = "전체 상태(대기중, 처리 완료, 보관 완료)에 대해 조회 시 fetchAllStatus = true")
    @GetMapping("/get-by/target/{targetId}")
    public ResponseEntity<ResponseDto> getReportsByTargetId(@RequestParam(value = "target") ReportTarget reportTarget,
                                                            @RequestParam(value = "fetchAllStatus", defaultValue = "false") boolean fetchAllStatus,
                                                            @PathVariable Long targetId) {
        List<ResponseReport.ReportDto> dtoList = reportService.getReportsByTargetId(reportTarget, targetId, fetchAllStatus);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Reported by target list retrieved successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "신고된 매장 리스트 조회", description = "우선순위 사유 신고 조회 시 applyPriority = true")
    @GetMapping("/get-by/store")
    public ResponseEntity<ResponseDto> getStoreReports(@ParameterObject @PageableDefault(size = 10) Pageable pageable,
                                                       @RequestParam(value = "status") ReportStatus reportStatus,
                                                       @RequestParam(value = "applyPriority", defaultValue = "false") boolean applyPriority) {
        Page<ResponseReport.StoreReportListDto> dtoPage = reportService.getStoreReports(pageable, reportStatus, applyPriority);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Reported store list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "신고된 게시글 리스트 조회")
    @GetMapping("/get-by/post")
    public ResponseEntity<ResponseDto> getPostReports(@ParameterObject @PageableDefault(size = 10) Pageable pageable,
                                                      @RequestParam(value = "status") ReportStatus reportStatus) {
        Page<ResponseReport.PostReportListDto> dtoPage = reportService.getPostReports(pageable, reportStatus);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Reported post list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "신고된 댓글 리스트 조회")
    @GetMapping("/get-by/comment")
    public ResponseEntity<ResponseDto> getCommentReports(@ParameterObject @PageableDefault(size = 10) Pageable pageable,
                                                         @RequestParam(value = "status") ReportStatus reportStatus) {
        Page<ResponseReport.CommentReportListDto> dtoPage = reportService.getCommentReports(pageable, reportStatus);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Reported comment list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
