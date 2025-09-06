package com.kymokim.spirit.notification.dto.review;

import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.store.entity.Store;
import lombok.Getter;

@Getter
public class ReviewCreatedNotificationEvent extends NotificationEvent {
    private final Store store;
    private final Long reviewId;

    public ReviewCreatedNotificationEvent(Store store, Long reviewId) {
        super();
        this.store = store;
        this.reviewId = reviewId;
    }
}

