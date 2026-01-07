package com.kymokim.spirit.comment.entity;

import com.kymokim.spirit.common.entity.HistoryInfo;
import com.kymokim.spirit.post.entity.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table
@Entity
@Getter
@NoArgsConstructor
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_comment_id")
    private Comment rootComment;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    private Long likeCount = 0L;

    private Long replyCount = 0L;

    private boolean isDeleted = false;

    @Embedded
    private HistoryInfo historyInfo;

    @Builder
    public Comment(Post post, String content, Long creatorId) {
        this.post = post;
        this.content = content;
        this.historyInfo = new HistoryInfo(creatorId);
    }

    public static Comment createReply(Post post, Comment rootComment, String content, Long creatorId) {
        Comment comment = new Comment();
        comment.post = post;
        comment.rootComment = rootComment;
        comment.content = content;
        comment.historyInfo = new HistoryInfo(creatorId);
        return comment;
    }

    public void update(String content) {
        this.content = content;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public boolean isRoot() {
        return rootComment == null;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if(this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void increaseReplyCount() {
        this.replyCount++;
    }

    public void decreaseReplyCount() {
        if (replyCount > 0) {
            this.replyCount--;
        }
    }
}
