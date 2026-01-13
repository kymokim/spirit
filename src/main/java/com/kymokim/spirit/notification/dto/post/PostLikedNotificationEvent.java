package com.kymokim.spirit.notification.dto.post;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.post.entity.Post;
import lombok.Getter;

@Getter
public class PostLikedNotificationEvent extends NotificationEvent {
    private final Auth writer;
    private final String likedUserNickName;
    private final Post post;

    public PostLikedNotificationEvent(Auth writer, String likedUserNickName, Post post) {
        super();
        this.writer = writer;
        this.likedUserNickName = likedUserNickName;
        this.post = post;
    }
}

