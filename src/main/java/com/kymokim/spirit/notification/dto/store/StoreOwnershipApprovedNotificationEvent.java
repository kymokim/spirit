package com.kymokim.spirit.notification.dto.store;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.store.entity.Store;
import lombok.Getter;

@Getter
public class StoreOwnershipApprovedNotificationEvent extends NotificationEvent {
    private final Auth user;

    private final Store store;

    public StoreOwnershipApprovedNotificationEvent(Auth user, Store store) {
        super();
        this.user = user;
        this.store = store;
    }
}
