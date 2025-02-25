package com.kymokim.spirit.auth.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Gender {
    MALE("1"),
    FEMALE("2"),
    UNKNOWN("0");

    private final String code;

    Gender(String code){
        this.code = code;
    }

    // find Enum by code
    public static Gender fromCode(String code) {
        return Arrays.stream(Gender.values())
                .filter(gender -> gender.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Gender code: " + code));
    }
}
