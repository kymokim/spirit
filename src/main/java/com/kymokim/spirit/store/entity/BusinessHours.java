package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Embeddable
public class BusinessHours {
    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Column(name = "break_start_time")
    private LocalTime breakStartTime;

    @Column(name = "break_end_time")
    private LocalTime breakEndTime;

    protected BusinessHours(){}

    @Builder
    public BusinessHours(LocalTime openTime, LocalTime closeTime, LocalTime breakStartTime, LocalTime breakEndTime){
        setOpenTime(openTime);
        setCloseTime(closeTime);
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
    }

    private void setOpenTime(LocalTime openTime){
        if (openTime == null){
            throw new CustomException(StoreErrorCode.STORE_OPEN_TIME_EMPTY);
        }
        this.openTime = openTime;
    }

    private void setCloseTime(LocalTime closeTime){
        if (closeTime == null){
            throw new CustomException(StoreErrorCode.STORE_CLOSE_TIME_EMPTY);
        }
        this.closeTime = closeTime;
    }
}
