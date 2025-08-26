package com.kymokim.spirit.auth.service;

import com.kymokim.spirit.archive.entity.ArchiveType;
import com.kymokim.spirit.archive.service.ArchiveService;
import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.Role;
import com.kymokim.spirit.auth.repository.AuthRepository;
import com.kymokim.spirit.common.annotation.AuthTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@AuthTransactional
public class AuthService {

    private final AuthRepository authRepository;
    private final ArchiveService archiveService;
    private final MainDataCleaner mainDataCleaner;

    public void cleanUpUser() {
        Auth user = AuthResolver.resolveUser();
        mainDataCleaner.cleanUp(user);
        if (!Objects.equals(user.getPersonalInfo().getCi(), null)) {
            archiveService.archiveUser(user.getId(), user.getPersonalInfo().getCi(), ArchiveType.WITHDREW);
        }
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