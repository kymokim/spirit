package com.kymokim.spirit.notification.dto.comment;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.post.entity.Post;
import lombok.Getter;

@Getter
public class RootCommentCreatedNotificationEvent extends NotificationEvent {
    private final Auth writer;
    private final String commentWriterNickName;
    private final Post post;

    public RootCommentCreatedNotificationEvent(Auth writer, String commentWriterNickName, Post post) {
        super();
        this.writer = writer;
        this.commentWriterNickName = commentWriterNickName;
        this.post = post;
    }
}
