package com.kymokim.spirit.notification.dto.store;

import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.store.entity.Store;
import lombok.Getter;

@Getter
public class StoreSuggestionCreatedNotificationEvent extends NotificationEvent {
    private final Store store;

    public StoreSuggestionCreatedNotificationEvent(Store store) {
        super();
        this.store = store;
    }
}
