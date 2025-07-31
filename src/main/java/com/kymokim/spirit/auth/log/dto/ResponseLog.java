package com.kymokim.spirit.auth.log.dto;

import com.kymokim.spirit.auth.auth.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class ResponseLog {
    @Getter
    @AllArgsConstructor
    public static class StoreViewLogStatListDto {
        private LocalDate date;
        private List<StoreViewLogStatDto> statList;
    }

    @Getter
    @AllArgsConstructor
    public static class StoreViewLogStatDto {
        private String ageGroup;
        private Gender gender;
        private long count;
    }
}
