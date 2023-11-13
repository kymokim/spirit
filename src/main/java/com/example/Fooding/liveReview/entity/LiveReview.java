package com.example.Fooding.liveReview.entity;

import com.example.Fooding.store.entity.Store;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Table
@Entity
@Getter
@NoArgsConstructor
@Data
public class LiveReview {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long liveReviewId;

    @Column(name = "makerId")
    private Long makerId;


    @Column(name = "liveReviewContent")
    private String liveReviewContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public LiveReview(Long makerId, String liveReviewContent, Store store) {
        this.makerId = makerId;
        this.liveReviewContent = liveReviewContent;
        this.store = store;
    }

    public void update(String liveReviewContent) {
        this.liveReviewContent = liveReviewContent;
    }
}
