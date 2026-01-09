package com.kymokim.spirit.notification.dto.post;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.notification.dto.NotificationEvent;
import lombok.Getter;

@Getter
public class PostLikedNotificationEvent extends NotificationEvent {
    private final Auth writer;
    private final String likedUserNickName;
    private final Long postId;

    public PostLikedNotificationEvent(Auth writer, String likedUserNickName, Long postId) {
        super();
        this.writer = writer;
        this.likedUserNickName = likedUserNickName;
        this.postId = postId;
    }
}

