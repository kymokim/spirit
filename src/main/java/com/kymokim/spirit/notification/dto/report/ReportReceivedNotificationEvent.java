package com.kymokim.spirit.notification.dto.report;

import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.store.entity.Store;
import lombok.Getter;

@Getter
public class ReportReceivedNotificationEvent extends NotificationEvent {
    private final Long reportId;
    private final String targetDisplayName;

    public ReportReceivedNotificationEvent(Long reportId, String targetDisplayName) {
        super();
        this.reportId = reportId;
        this.targetDisplayName = targetDisplayName;
    }
}
