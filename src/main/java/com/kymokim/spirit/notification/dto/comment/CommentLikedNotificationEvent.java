package com.kymokim.spirit.notification.dto.comment;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.comment.entity.Comment;
import com.kymokim.spirit.notification.dto.NotificationEvent;
import lombok.Getter;

@Getter
public class CommentLikedNotificationEvent extends NotificationEvent {
    private final Auth writer;
    private final Auth likedUser;
    private final Comment comment;

    public CommentLikedNotificationEvent(Auth writer, Auth likedUser, Comment comment) {
        super();
        this.writer = writer;
        this.likedUser = likedUser;
        this.comment = comment;
    }
}
