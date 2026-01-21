package com.kymokim.spirit.post.entity;

import com.kymokim.spirit.comment.entity.Comment;
import com.kymokim.spirit.common.entity.HistoryInfo;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Table
@Entity
@Getter
@NoArgsConstructor
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Embedded
    private HistoryInfo historyInfo;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostImage> imageList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "rate")
    private Double rate;

    @Column(name = "place")
    private String place;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    private Long likeCount = 0L;

    private Long commentCount = 0L;

    private Long shareCount = 0L;

    private boolean isDeleted = false;

    @Builder
    public Post(String content, Double rate, Long creatorId, Store store) {
        this.content = content;
        this.rate = rate;
        this.historyInfo = new HistoryInfo(creatorId);
        this.store = store;
    }

    public void update(String content, Double rate) {
        this.content = content;
        this.rate = rate;
    }

    public void addImageList(PostImage postImage) {
        this.imageList.add(postImage);
    }

    public void removeImageList(PostImage postImage) {
        this.imageList.remove(postImage);
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }
    public void decreaseLikeCount() {
        if(this.likeCount > 0) {
            this.likeCount--;
        }
    }
    public void increaseCommentCount() {
        this.commentCount++;
    }
    public void decreaseCommentCount() {
        if(this.commentCount > 0) {
            this.commentCount--;
        }
    }
    public void increaseShareCount() {
        this.shareCount++;
    }
    public void decreaseShareCount() {
        if(this.shareCount > 0) {
            this.shareCount--;
        }
    }
}
