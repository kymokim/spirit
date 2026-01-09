package com.kymokim.spirit.notification.entity;

import lombok.Getter;
import java.util.Map;

@Getter
public enum NotificationType {
    // store
    STORE_OWNERSHIP_APPROVED("매장 권한 승인", "{storeName} 매장의 사장님 인증이 승인되었습니다."),
    STORE_OWNERSHIP_REJECTED("매장 권한 거절", "{storeName} 매장의 사장님 인증이 거절되었습니다. \n" +
            "거절 사유 : {rejectionReason}"),
    STORE_MANAGER_INVITE_ACCEPTED("매장 운영자 추가", "{storeName} 매장에 새 운영자가 추가되었습니다."),
    STORE_OWNER_CHANGED("매장 소유자 변경", "{storeName} 매장의 소유자가 변경되었습니다."),
    STORE_OWNERSHIP_REQUEST_CREATED("사장님 인증 요청", "{storeName} 매장의 사장님 인증 요청이 접수되었습니다."),
    STORE_SUGGESTION_CREATED("매장 제보", "새 매장 제보가 접수되었습니다."),

    // post
    STORE_TAG_POST_CREATED("매장 게시글", "{storeName} 매장의 새 후기 게시글이 등록되었습니다."),
    POST_LIKED("게시글 좋아요", "{nickName}님이 회원님의 게시글을 좋아합니다."),

    // comment
    ROOT_COMMENT_CREATED("게시글 댓글", "{nickName}님이 회원님의 게시글에 댓글을 남겼습니다."),
    REPLY_COMMENT_CREATED("댓글 답글", "{nickName}님이 회원님의 댓글에 답글을 남겼습니다."),
    COMMENT_LIKED("댓글 좋아요", "{nickName}님이 회원님의 댓글을 좋아합니다."),

    // report
    REPORT_RECEIVED("신고 접수", "새 {targetDisplayName} 신고가 접수되었습니다.");

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

    public String format() {
        return body;
    }
}
