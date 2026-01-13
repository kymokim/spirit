package com.kymokim.spirit.notification.dto.post;

import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.post.entity.Post;
import com.kymokim.spirit.store.entity.Store;
import lombok.Getter;

@Getter
public class StoreTagPostCreatedNotificationEvent extends NotificationEvent {
    private final Store store;
    private final Post post;

    public StoreTagPostCreatedNotificationEvent(Store store, Post post) {
        super();
        this.store = store;
        this.post = post;
    }
}