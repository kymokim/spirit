package com.kymokim.spirit.store.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationCriteria {
    private double latitude;
    private double longitude;
    private double radius;
}
