package com.kymokim.spirit.auth.service;

import com.kymokim.spirit.archive.entity.ArchiveType;
import com.kymokim.spirit.archive.service.ArchiveService;
import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.dto.RequestAuth;
import com.kymokim.spirit.auth.dto.ResponseAuth;
import com.kymokim.spirit.auth.entity.PersonalInfo;
import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.auth.repository.AuthRepository;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.security.JwtTokenProvider;
import com.kymokim.spirit.common.service.AESUtil;
import com.kymokim.spirit.common.service.RedisService;
import com.kymokim.spirit.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuthService{

    private final AuthRepository authRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Service s3Service;
    private final ArchiveService archiveService;
    private final AESUtil aesUtil;

    private Auth resolveUser(){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        Auth user = authRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
        return user;
    }

    @Transactional
    public void registerUser(RequestAuth.RegisterUserDto registerUserDto) {

        Auth user = authRepository.findBySocialInfo(registerUserDto.getSocialInfoDto().toEntity());
        if(user != null){
            throw new CustomException(AuthErrorCode.USER_SOCIAL_INFO_EXISTS);
        }

        // 본인인증 api 추가 시 해당 부분 변경
        PersonalInfo personalInfo = new RequestAuth.PersonalInfoRqDto().toEntity(aesUtil);

        user = registerUserDto.toEntity(personalInfo);
        authRepository.save(user);
    }

    //일단 socialToken은 받기만 하고 추후 검증 로직 추가
    @Transactional
    public ResponseAuth.LoginUserRsDto loginUser(RequestAuth.LoginUserRqDto loginUserRqDto) {
        Auth user = authRepository.findBySocialInfo(loginUserRqDto.getSocialInfoDto().toEntity());
        if(user == null) {
            throw new CustomException(AuthErrorCode.USER_NOT_FOUND);
        }
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        user.setRefreshToken(refreshToken);
        authRepository.save(user);
        return ResponseAuth.LoginUserRsDto.toDto(accessToken, refreshToken);
    }

    @Transactional
    public ResponseAuth.ReissueTokenDto reissueToken(String refreshToken){
        Auth user = resolveUser();
        String accessToken;
        if (user.getRefreshToken().equals(refreshToken)) {
            accessToken = jwtTokenProvider.createAccessToken(user.getId());
            if (jwtTokenProvider.checkExpiry(refreshToken)){
                refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
                user.setRefreshToken(refreshToken);
                authRepository.save(user);
            }
            return ResponseAuth.ReissueTokenDto.toDto(accessToken, refreshToken);
        }
        else
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_MATCH_FAILED);
    }

    // 기존에 등록된 이미지가 없으면 업로드, 있으면 업데이트
    @Transactional
    public void uploadImg(MultipartFile file) {
        Auth user = resolveUser();
        String imageUrl;
        if (file != null){
            if (user.getImgUrl() == null) {
                imageUrl = s3Service.upload(file, "user/" + String.valueOf(user.getId()));
            }
            else {
                imageUrl = s3Service.update(file, "user/" + String.valueOf(user.getId()), user.getImgUrl());
            }
        } else {
            throw new CustomException(AuthErrorCode.USER_IMG_FILE_EMPTY);
        }
        user.setImgUrl(imageUrl);
        authRepository.save(user);
    }

    @Transactional
    public void deleteImg(){
        Auth user = resolveUser();
        String originUrl;
        if ( !(user.getImgUrl() == null) && !user.getImgUrl().isEmpty()){
            originUrl = user.getImgUrl();
        } else {
            throw new CustomException(AuthErrorCode.USER_ORIGIN_IMG_URL_EMPTY);
        }
        s3Service.deleteFile(originUrl);
        user.setImgUrl(null);
        authRepository.save(user);
    }

    @Transactional
    public ResponseAuth.CheckNicknameDto checkNickname(String nickname){
        return ResponseAuth.CheckNicknameDto.toDto(isNicknameUsable(nickname));
    }

    //현재는 닉네임 중복검사 로직만 존재
    @Transactional
    public Boolean isNicknameUsable(String nickname){
        Auth user = authRepository.findByNickname(nickname);
        return user == null;
    }

    @Transactional
    public void updateNickname(String nickname) {

        Auth user = resolveUser();
        if (!isNicknameUsable(nickname)) {
            throw new CustomException(AuthErrorCode.USER_NICKNAME_EXISTS);
        }

        user.setNickname(nickname);
        authRepository.save(user);
    }

    @Transactional
    public ResponseAuth.GetUserDto getUser() {
        Auth user = resolveUser();
        return ResponseAuth.GetUserDto.toDto(user);
    }

    @Transactional
    public void logoutUser(String refreshToken){
        Auth user = resolveUser();
        if (user.getRefreshToken().equals(refreshToken)) {
            user.setRefreshToken(null);
            authRepository.save(user);
        } else
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_MATCH_FAILED);
    }

    @Transactional
    public void withdrawUser(){
        Auth user = resolveUser();
        archiveService.archiveUser(user.getId(), user.getPersonalInfo().getCi(), ArchiveType.WITHDREW);
        user.withdraw();
        authRepository.save(user);
    }
}