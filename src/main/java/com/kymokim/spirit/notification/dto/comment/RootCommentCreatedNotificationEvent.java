package com.kymokim.spirit.notification.dto.comment;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.notification.dto.NotificationEvent;
import lombok.Getter;

@Getter
public class RootCommentCreatedNotificationEvent extends NotificationEvent {
    private final Auth writer;
    private final String commentWriterNickName;
    private final Long postId;

    public RootCommentCreatedNotificationEvent(Auth writer, String commentWriterNickName, Long postId) {
        super();
        this.writer = writer;
        this.commentWriterNickName = commentWriterNickName;
        this.postId = postId;
    }
}
