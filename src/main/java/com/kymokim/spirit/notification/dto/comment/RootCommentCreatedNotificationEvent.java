package com.kymokim.spirit.notification.dto.comment;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.comment.entity.Comment;
import com.kymokim.spirit.notification.dto.NotificationEvent;
import lombok.Getter;

@Getter
public class RootCommentCreatedNotificationEvent extends NotificationEvent {
    private final Auth writer;
    private final Auth commentWriter;
    private final Comment comment;

    public RootCommentCreatedNotificationEvent(Auth writer, Auth commentWriter, Comment comment) {
        super();
        this.writer = writer;
        this.commentWriter = commentWriter;
        this.comment = comment;
    }
}
