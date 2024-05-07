package com.kymokim.spirit.auth.dto;

import com.kymokim.spirit.auth.entity.Auth;
import lombok.Builder;
import lombok.Data;

public class RequestAuth {
    @Builder
    @Data
    public static class RegisterUserDto{
        private String email;
        private String password;
        private String name;
        private String nickName;
        private String ssNumber;
        private String phoneNumber;

        public static Auth toEntity(RegisterUserDto registerUserDto, String salt, String encryptedPassword){
            return Auth.builder()
                    .email(registerUserDto.getEmail())
                    .password(encryptedPassword)
                    .name(registerUserDto.getName())
                    .nickName(registerUserDto.getNickName())
                    .ssNumber(registerUserDto.getSsNumber())
                    .phoneNumber(registerUserDto.getPhoneNumber())
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
        private String name;
        private String nickName;
        private String ssNumber;
        private String phoneNumber;

        public static Auth toEntity(Auth user, UpdateUserDto updateUserDto, String salt, String encryptedPassword){
            user.update(encryptedPassword, updateUserDto.getName(), updateUserDto.getNickName(), updateUserDto.getSsNumber(), updateUserDto.getPhoneNumber(), salt);
            return user;
        }
    }
}
