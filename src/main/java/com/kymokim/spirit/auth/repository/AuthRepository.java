package com.kymokim.spirit.auth.repository;

import com.kymokim.spirit.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Auth findByEmail(String email);
    Auth findByNickName(String nickName);
    Auth findByEmailAndPassword(String email, String password);

}
