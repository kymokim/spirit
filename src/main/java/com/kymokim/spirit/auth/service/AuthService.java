package com.kymokim.spirit.auth.service;

import com.kymokim.spirit.archive.entity.ArchiveType;
import com.kymokim.spirit.archive.service.ArchiveService;
import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.NotificationConsent;
import com.kymokim.spirit.auth.entity.Role;
import com.kymokim.spirit.auth.entity.SocialInfo;
import com.kymokim.spirit.auth.dto.RequestAuth;
import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.auth.repository.AuthRepository;
import com.kymokim.spirit.auth.repository.NotificationConsentRepository;
import com.kymokim.spirit.auth.repository.SocialInfoRepository;
import com.kymokim.spirit.common.annotation.AuthTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@AuthTransactional
public class AuthService {

    private final AuthRepository authRepository;
    private final S3Service s3Service;
    private final ArchiveService archiveService;
    private final SocialInfoRepository socialInfoRepository;
    private final NotificationConsentRepository notificationConsentRepository;
    private final MainDataCleaner mainDataCleaner;

    public void mergeUser(RequestAuth.MergeUserDto mergeUserDto) {
        Auth originalUser = authRepository.findBySocial(mergeUserDto.getOriginalSocialInfoDto().getSocialType(), mergeUserDto.getOriginalSocialInfoDto().getSocialId());
        if (originalUser == null) {
            throw new CustomException(AuthErrorCode.USER_NOT_FOUND);
        }

        Auth newUser = authRepository.findBySocial(mergeUserDto.getNewSocialInfoDto().getSocialType(), mergeUserDto.getNewSocialInfoDto().getSocialId());
        if (newUser != null) {
            withdraw(newUser);
        }
        SocialInfo newSocialInfo = mergeUserDto.getNewSocialInfoDto().toEntity();
        originalUser.addSocialInfo(newSocialInfo);
        socialInfoRepository.save(newSocialInfo);
        authRepository.save(originalUser);
    }

    public void withdrawUser() {
        Auth user = AuthResolver.resolveUser();
        withdraw(user);
    }

    private void withdraw(Auth user) {
        mainDataCleaner.cleanUp(user);
        if (!Objects.equals(user.getImgUrl(), null) && !user.getImgUrl().isEmpty()) {
            s3Service.deleteFile(user.getImgUrl());
            user.setImgUrl(null);
        }
        if (!Objects.equals(user.getPersonalInfo().getCi(), null)) {
            archiveService.archiveUser(user.getId(), user.getPersonalInfo().getCi(), ArchiveType.WITHDREW);
        }
        if (user.getNotificationConsent() != null) {
            NotificationConsent notificationConsent = user.getNotificationConsent();
            user.initNotificationConsent(null);
            notificationConsentRepository.delete(notificationConsent);
        }
        user.withdraw();
        authRepository.save(user);
    }

    public void addRole(Auth user, Role role) {
        user.getRoles().add(role);
        authRepository.save(user);
    }

    public void removeRole(Auth user, Role role) {
        user.getRoles().remove(role);
        authRepository.save(user);
    }
}