package com.kymokim.spirit.review.entity;

import com.kymokim.spirit.common.entity.HistoryInfo;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table
@Entity
@Getter
@NoArgsConstructor
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "rate")
    private Double rate;

    @Column(name = "visited_at")
    private LocalDateTime visitedAt;

    @Embedded
    private HistoryInfo historyInfo;

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewImage> imgUrlList = new ArrayList<>();

    @Column
    private Long writerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "reply")
    private String reply;

    @Column(name = "replied_at")
    private LocalDateTime repliedAt;

    @Builder
    public Review(String content, Double rate, LocalDateTime visitedAt, Long writerId, Store store) {
        this.content = content;
        this.rate = rate;
        this.visitedAt = visitedAt;
        this.historyInfo = new HistoryInfo(writerId);
        this.writerId = writerId;
        this.store = store;
    }

    public void update(String content, Double rate, LocalDateTime visitedAt) {
        this.content = content;
        this.rate = rate;
        this.visitedAt = visitedAt;
    }

    public void addImgUrlList(ReviewImage reviewImage) {
        this.imgUrlList.add(reviewImage);
    }

    public void removeImgUrlList(ReviewImage reviewImage) {
        this.imgUrlList.remove(reviewImage);
    }
}
