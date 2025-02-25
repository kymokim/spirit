package com.kymokim.spirit.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

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

    // 안 들어왔을 때 CustomException 추가 필수
    @Builder
    public PersonalInfo(String ci, String name, String birthDate, Gender gender, String phoneNumber){
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
