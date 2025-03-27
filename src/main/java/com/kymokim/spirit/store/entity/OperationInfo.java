package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "operation_info")
@Getter
@NoArgsConstructor
public class OperationInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Builder
    public OperationInfo(DayOfWeek dayOfWeek, Boolean isClosed, LocalTime openTime, LocalTime closeTime,
                         LocalTime breakStartTime, LocalTime breakEndTime, Store store){
        if (Boolean.TRUE.equals(isClosed) && (!Objects.equals(openTime, null) || !Objects.equals(closeTime, null) || !Objects.equals(breakStartTime, null) || !Objects.equals(breakEndTime, null)))
            throw new CustomException(StoreErrorCode.WRONG_OPERATION_INFO);
        if (Boolean.FALSE.equals(isClosed) && (Objects.equals(openTime, null) || Objects.equals(closeTime, null)))
            throw new CustomException(StoreErrorCode.WRONG_OPERATION_INFO);
        this.dayOfWeek = dayOfWeek;
        this.isClosed = isClosed;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
        this.store = store;
    }
}
