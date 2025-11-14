package com.kymokim.spirit.store.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreMarkerProjection {
    private Long storeId;
    private Double latitude;
    private Double longitude;
    private String storeName;
}
