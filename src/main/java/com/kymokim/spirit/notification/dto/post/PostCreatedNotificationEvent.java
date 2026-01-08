package com.kymokim.spirit.notification.dto.post;

import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.store.entity.Store;
import lombok.Getter;

@Getter
public class PostCreatedNotificationEvent extends NotificationEvent {
    private final Store store;
    private final Long postId;

    public PostCreatedNotificationEvent(Store store, Long postId) {
        super();
        this.store = store;
        this.postId = postId;
    }
}

