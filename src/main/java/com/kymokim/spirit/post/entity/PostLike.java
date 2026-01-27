package com.kymokim.spirit.post.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(
        indexes = {
                @Index(
                        name = "idx_post_like_liked_at_post_id",
                        columnList = "liked_at, post_id"
                )
        }
)
@Entity
@Getter
@NoArgsConstructor
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @CreationTimestamp
    private LocalDateTime likedAt;

    @Builder
    public PostLike(Long userId, Post post) {
        this.userId = userId;
        this.post = post;
    }
}
