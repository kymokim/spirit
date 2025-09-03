package com.kymokim.spirit.notification.dto.store;

import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.store.entity.Store;
import lombok.Getter;

@Getter
public class StoreOwnerChangedNotificationEvent extends NotificationEvent {
    private final Store store;

    public StoreOwnerChangedNotificationEvent(Store store) {
        super();
        this.store = store;
    }
}

