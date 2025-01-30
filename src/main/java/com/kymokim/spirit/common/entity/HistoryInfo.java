package com.kymokim.spirit.common.entity;

import com.kymokim.spirit.common.exception.CommonErrorCode;
import com.kymokim.spirit.common.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Embeddable
public class HistoryInfo {
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updater_id")
    private Long updaterId;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected HistoryInfo(){}

    @Builder
    public HistoryInfo(Long creatorId){
        if (creatorId == null){
            throw new CustomException(CommonErrorCode.CREATOR_ID_EMPTY);
        }
        this.creatorId = creatorId;
        this.createdAt = LocalDateTime.now();
    }

    public void update(Long updaterId) {
        this.updaterId = updaterId;
        this.updatedAt = LocalDateTime.now();
    }
}