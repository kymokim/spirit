package com.kymokim.spirit.auth.auth.controller;

import com.kymokim.spirit.auth.auth.dto.RequestAuth;
import com.kymokim.spirit.auth.auth.service.AuthService;
import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.common.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Auth API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원 통합")
    @PostMapping("/merge")
    public ResponseEntity<ResponseDto> mergeUser(@Valid @RequestBody RequestAuth.MergeUserDto mergeUserDto) {
        authService.mergeUser(mergeUserDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User merged successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "회원 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료), 리프레시 토큰이 일치하지 않는 경우[21002]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 유저 정보가 없을 경우[21005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/withdraw")
    public ResponseEntity<ResponseDto> withdrawUser(){
        authService.withdrawUser();
        ResponseDto responseDto = ResponseDto.builder()
                .message("User withdrew successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}