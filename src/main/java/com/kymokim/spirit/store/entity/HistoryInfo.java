package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Embeddable
public class HistoryInfo {
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updater_id")
    private Long updaterId;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected HistoryInfo(){}

    @Builder
    public HistoryInfo(Long creatorId){
        if (creatorId == null){
            throw new CustomException(StoreErrorCode.STORE_CREATOR_ID_EMPTY);
        }
        this.creatorId = creatorId;
    }

    public void update(Long updaterId) {
        this.updaterId = updaterId;
    }
}