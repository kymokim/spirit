package com.kymokim.spirit.store.dto;

import lombok.*;

@Data
@Builder
public class FacilitiesCondition {
    private Boolean hasScreen;
    private Boolean hasRoom;
    private Boolean isGroupAvailable;
    private Boolean isParkingAvailable;
    private Boolean isCorkageAvailable;
    private Boolean hasOutdoor;
}
