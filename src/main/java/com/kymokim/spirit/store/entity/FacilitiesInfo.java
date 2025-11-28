package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FacilitiesInfo {

    @Column(name = "has_screen", nullable = false)
    private Boolean hasScreen;

    @Column(name = "has_room", nullable = false)
    private Boolean hasRoom;

    @Column(name = "is_group_available", nullable = false)
    private Boolean isGroupAvailable;

    @Column(name = "is_parking_available", nullable = false)
    private Boolean isParkingAvailable;

    @Column(name = "is_corkage_available", nullable = false)
    private Boolean isCorkageAvailable;

    @Column(name = "has_outdoor", nullable = false)
    private Boolean hasOutdoor;

    @Builder
    public FacilitiesInfo(Boolean hasScreen, Boolean hasRoom, Boolean isGroupAvailable,
                          Boolean isParkingAvailable, Boolean isCorkageAvailable, Boolean hasOutdoor) {
        this.hasScreen = validateNotNull(hasScreen, StoreErrorCode.HAS_SCREEN_EMPTY);
        this.hasRoom = validateNotNull(hasRoom, StoreErrorCode.HAS_ROOM_EMPTY);
        this.isGroupAvailable = validateNotNull(isGroupAvailable, StoreErrorCode.IS_GROUP_AVAILABLE_EMPTY);
        this.isParkingAvailable = validateNotNull(isParkingAvailable, StoreErrorCode.IS_PARKING_AVAILABLE_EMPTY);
        this.isCorkageAvailable = validateNotNull(isCorkageAvailable, StoreErrorCode.IS_CORKAGE_AVAILABLE_EMPTY);
        this.hasOutdoor = validateNotNull(hasOutdoor, StoreErrorCode.HAS_OUTDOOR_EMPTY);
    }

    public void updateHasScreen(Boolean hasScreen) {
        this.hasScreen = validateNotNull(hasScreen, StoreErrorCode.HAS_SCREEN_EMPTY);
    }

    public void updateHasRoom(Boolean hasRoom) {
        this.hasRoom = validateNotNull(hasRoom, StoreErrorCode.HAS_ROOM_EMPTY);
    }

    public void updateIsGroupAvailable(Boolean isGroupAvailable) {
        this.isGroupAvailable = validateNotNull(isGroupAvailable, StoreErrorCode.IS_GROUP_AVAILABLE_EMPTY);
    }

    public void updateIsParkingAvailable(Boolean isParkingAvailable) {
        this.isParkingAvailable = validateNotNull(isParkingAvailable, StoreErrorCode.IS_PARKING_AVAILABLE_EMPTY);
    }

    public void updateIsCorkageAvailable(Boolean isCorkageAvailable) {
        this.isCorkageAvailable = validateNotNull(isCorkageAvailable, StoreErrorCode.IS_CORKAGE_AVAILABLE_EMPTY);
    }

    public void updateHasOutdoor(Boolean hasOutdoor) {
        this.hasOutdoor = validateNotNull(hasOutdoor, StoreErrorCode.HAS_OUTDOOR_EMPTY);
    }

    private Boolean validateNotNull(Boolean value, StoreErrorCode errorCode) {
        if (value == null) {
            throw new CustomException(errorCode);
        }
        return value;
    }
}
