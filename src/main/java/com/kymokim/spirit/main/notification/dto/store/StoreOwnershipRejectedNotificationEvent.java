package com.kymokim.spirit.main.notification.dto.store;

import com.kymokim.spirit.auth.auth.entity.Auth;
import com.kymokim.spirit.main.notification.dto.NotificationEvent;
import com.kymokim.spirit.main.store.entity.Store;
import lombok.Getter;

@Getter
public class StoreOwnershipRejectedNotificationEvent extends NotificationEvent {
    private final Auth user;

    private final Store store;
    private final String rejectionReason;

    public StoreOwnershipRejectedNotificationEvent(Auth user, Store store, String rejectionReason) {
        super();
        this.user = user;
        this.store = store;
        this.rejectionReason = rejectionReason;
    }
}
