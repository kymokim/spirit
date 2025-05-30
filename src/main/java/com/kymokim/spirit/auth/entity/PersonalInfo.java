package com.kymokim.spirit.auth.entity;

import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.common.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Embeddable
@NoArgsConstructor
public class PersonalInfo {
    @Column(name = "ci", nullable = false)
    private String ci;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "birth_date", nullable = false)
    private String birthDate;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Builder
    public PersonalInfo(String ci, String name, String birthDate, Gender gender, String phoneNumber){
        if (ci == null || ci.isEmpty() || name == null || name.isEmpty() || birthDate == null || birthDate.isEmpty() || gender == null || phoneNumber == null || phoneNumber.isEmpty()){
            throw new CustomException(AuthErrorCode.PERSONAL_INFO_EMPTY);
        }
        this.ci = ci;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }

    public void withdraw(){
        this.ci = "";
        this.name = "";
        this.birthDate = "";
        this.gender = Gender.UNKNOWN;
        this.phoneNumber = "";
    }
}
