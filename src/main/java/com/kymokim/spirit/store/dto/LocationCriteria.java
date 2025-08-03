package com.kymokim.spirit.store.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LocationCriteria {
    private double latitude;
    private double longitude;
    private double radius;
}
