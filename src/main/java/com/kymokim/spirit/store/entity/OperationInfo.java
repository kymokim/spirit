package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Embeddable
public class OperationInfo {
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "is_closed")
    private Boolean isClosed;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "break_start_time")
    private LocalTime breakStartTime;

    @Column(name = "break_end_time")
    private LocalTime breakEndTime;

    protected OperationInfo(){}

    @Builder
    public OperationInfo(DayOfWeek dayOfWeek, Boolean isClosed, LocalTime openTime, LocalTime closeTime, LocalTime breakStartTime, LocalTime breakEndTime){
        this.dayOfWeek = dayOfWeek;
        this.isClosed = isClosed;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
    }
}
