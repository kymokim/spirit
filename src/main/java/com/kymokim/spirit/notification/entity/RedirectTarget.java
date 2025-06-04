package com.kymokim.spirit.notification.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Embeddable
@Getter
public class RedirectTarget {

    @Enumerated(EnumType.STRING)
    public RedirectType redirectType;

    private Long redirectId;

    @Builder
    public RedirectTarget(RedirectType redirectType, Long redirectId) {
        this.redirectType = redirectType;
        this.redirectId = redirectId;
    }
}
