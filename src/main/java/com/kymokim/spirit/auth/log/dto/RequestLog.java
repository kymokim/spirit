package com.kymokim.spirit.auth.log.dto;

import com.kymokim.spirit.auth.auth.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class RequestLog {
    @Getter
    @AllArgsConstructor
    public static class StoreViewLogStatFilter {
        private Long storeId;
        private LocalDate startDate;
        private LocalDate endDate;
        private Gender gender;
        private List<String> ageGroups;
        private List<String> groupBy;
        private String showBy;
    }
}
