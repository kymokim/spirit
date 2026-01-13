package com.kymokim.spirit.notification.dto.comment;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.comment.entity.Comment;
import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.post.entity.Post;
import lombok.Getter;

@Getter
public class ReplyCommentCreatedNotificationEvent extends NotificationEvent {
    private final Auth writer;
    private final Auth replyWriter;
    private final Comment comment;

    public ReplyCommentCreatedNotificationEvent(Auth writer, Auth replyWriter, Comment comment) {
        super();
        this.writer = writer;
        this.replyWriter = replyWriter;
        this.comment = comment;
    }
}
