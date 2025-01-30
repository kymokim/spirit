package com.kymokim.spirit.auth.controller;

import com.kymokim.spirit.auth.dto.RequestAuth;
import com.kymokim.spirit.auth.dto.ResponseAuth;
import com.kymokim.spirit.auth.service.AuthService;
import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.common.exception.ErrorResponse;
import com.kymokim.spirit.common.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Tag(name = "Auth API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "이미 해당 소셜 정보로 가입한 경우[21003]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<ResponseDto> registerUser(@Valid @RequestBody RequestAuth.RegisterUserDto registerUserDto) {
        System.out.println("Auth/registerUser API called.");
        authService.registerUser(registerUserDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User registered successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "로그인", description = "반환되는 accessToken, refreshToken 전부 저장 후" +
            "\n\n모든 요청의 x-auth-token 헤더에 accessToken을 담아서 사용(/reissue-token, /logout API는 refreshToken)" +
            "\n\naccessToken(유효기간 1시간) 만료(401 에러) 시 /reissue-token API로 액세스 토큰 재발급" +
            "\n\nrefreshToken(유효기간 30일)은 만료(401 에러) 시 /login API로 액세스, 리프레쉬 전부 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "해당 유저 정보가 없을 경우[21005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> loginUser(@Valid @RequestBody RequestAuth.LoginUserRqDto loginUserRqDto) {
        System.out.println("Auth/loginUser API called.");
        ResponseAuth.LoginUserRsDto loginUserRsDto = authService.loginUser(loginUserRqDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User logged in successfully.")
                .data(loginUserRsDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "액세스 토큰 재발급", description = "x-auth-token 헤더에 refreshToken 입력" +
            "\n\n해당 API 호출 시 리프레쉬 토큰이 만료 7일 전부터 자동 갱신되므로 반환되는 액세스, 리프레쉬 토큰 전부 저장 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(리프레쉬 토큰 만료), 리프레시 토큰이 일치하지 않는 경우[21002]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 유저 정보가 없을 경우[21005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/reissue-token")
    public ResponseEntity<ResponseDto> reissueToken(HttpServletRequest request){
        System.out.println("Auth/reissueToken API called.");
        String refreshToken = jwtTokenProvider.resolveToken(request);
        ResponseAuth.ReissueTokenDto reissueTokenDto = authService.reissueToken(refreshToken);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Token reissued successfully.")
                .data(reissueTokenDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "유저 이미지 등록/수정", description = "새로 등록된 파일 전송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "이미지 파일이 없을 경우[21006]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 유저 정보가 없을 경우[21005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> uploadImg(@Valid @RequestPart(value = "file") MultipartFile file) {
        System.out.println("Auth/uploadImg API called.");
        authService.uploadImg(file);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User image uploaded successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "유저 이미지 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "저장된 이미지 주소가 없을 경우[21001]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 유저 정보가 없을 경우[21005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete-image")
    public ResponseEntity<ResponseDto> deleteImg(){
        System.out.println("Auth/deleteImg API called.");
        authService.deleteImg();
        ResponseDto responseDto = ResponseDto.builder()
                .message("User image deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "닉네임 사용 가능 여부(중복 여부) 확인", description = "토큰 불필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/check-nickname")
    public ResponseEntity<ResponseDto> checkNickname(@Valid @RequestParam String nickname) {
        System.out.println("Auth/checkNickname API called.");
        ResponseAuth.CheckNicknameDto checkNicknameDto = authService.checkNickname(nickname);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User nickname check result retrieved.")
                .data(checkNicknameDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "닉네임 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "이미 사용중인 닉네임인 경우[21004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 유저 정보가 없을 경우[21005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/update-nickname")
    public ResponseEntity<ResponseDto> updateNickname(@Valid @RequestParam String nickname) {
        System.out.println("Auth/updateNickname API called.");
        authService.updateNickname(nickname);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User nickname updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "유저 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 유저 정보가 없을 경우[21005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/get")
    public ResponseEntity<ResponseDto> getUser() {
        System.out.println("Auth/getUser API called.");
        ResponseAuth.GetUserDto response = authService.getUser();
        ResponseDto responseDto = ResponseDto.builder()
                .message("User information retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "로그아웃", description = "x-auth-token 헤더에 refreshToken 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료), 리프레시 토큰이 일치하지 않는 경우[21002]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 유저 정보가 없을 경우[21005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto> logoutUser(HttpServletRequest request){
        System.out.println("Auth/logoutUser API called.");
        String refreshToken = jwtTokenProvider.resolveToken(request);
        authService.logoutUser(refreshToken);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User logged out successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}