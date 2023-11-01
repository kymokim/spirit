package com.example.Fooding.auth.dto;

import com.example.Fooding.auth.domain.Auth;
import lombok.Builder;
import lombok.Data;

public class RequestAuth {
    @Builder
    @Data
    public static class RegisterUserDto{
        private String email;
        private String password;
        private String username;

        public static Auth toEntity(RegisterUserDto registerUserDto, String salt, String encryptedPassword){
            return Auth.builder()
                    .email(registerUserDto.getEmail())
                    .password(encryptedPassword)
                    .username(registerUserDto.getUsername())
                    .salt(salt)
                    .build();
        }
    }

    @Builder
    @Data
    public static class LoginUserRqDto{
        private String email;
        private String password;
    }

    @Builder
    @Data
    public static class UpdateUserDto{
        private String password;
        private String username;

        public static Auth toEntity(Auth user, UpdateUserDto updateUserDto, String salt, String encryptedPassword){
            user.update(encryptedPassword, updateUserDto.getUsername(), salt);
            return user;
        }
    }
}
