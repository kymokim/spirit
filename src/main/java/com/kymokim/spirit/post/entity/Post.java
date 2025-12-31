package com.kymokim.spirit.post.entity;

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
}
