package com.kymokim.spirit.notification.entity;

import lombok.Getter;
import java.util.Map;

@Getter
public enum NotificationType {
    // 매장 사장 권한 등록 관련
    STORE_OWNERSHIP_APPROVED("매장 권한 승인 알림", "{storeName} 매장의 사장님 인증이 승인되었습니다."),
    STORE_OWNERSHIP_REJECTED("매장 권한 거절 알림", "{storeName} 매장의 사장님 인증이 거절되었습니다. \n" +
            "거절 사유 : {rejectionReason}"),
    // 운영자 초대/소유자 변경 관련
    STORE_MANAGER_INVITE_ACCEPTED("매장 운영자 추가 알림", "{storeName} 매장에 새 운영자가 추가되었습니다."),
    STORE_OWNER_CHANGED("매장 소유자 변경 알림", "{storeName} 매장의 소유자가 변경되었습니다."),
    // 리뷰 생성 관련
    STORE_REVIEW_CREATED("새 리뷰 알림", "{storeName} 매장에 새로운 리뷰가 등록되었습니다.");

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
