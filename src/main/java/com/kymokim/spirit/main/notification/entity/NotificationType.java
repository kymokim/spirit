package com.kymokim.spirit.main.notification.entity;

import lombok.Getter;
import java.util.Map;

@Getter
public enum NotificationType {
    // 매장 사장 권한 등록 관련
    STORE_OWNERSHIP_APPROVED("매장 권한 승인 알림", "{storeName} 매장의 사장님 인증이 승인되었습니다."),
    STORE_OWNERSHIP_REJECTED("매장 권한 거절 알림", "{storeName} 매장의 사장님 인증이 거절되었습니다. \n" +
            "거절 사유 : {rejectionReason}");

    private final String title;
    private final String body;

    NotificationType(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String format(Map<String, String> args) {
        String msg = body;
        for (Map.Entry<String, String> entry : args.entrySet()) {
            msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return msg;
    }
}
