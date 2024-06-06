package com.kymokim.spirit.store.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class StoreSearchCriteria {
    private double latitude;
    private double longitude;
    private double radius;
}
