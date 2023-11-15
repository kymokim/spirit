package com.example.Fooding.review.entity;

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
public class Review {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reviewId;

    @Column(name = "makerId")
    private Long makerId;


    @Column(name = "reviewContent")
    private String reviewContent;

    @Column(name = "rate")
    private Double rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public Review(Long makerId, String reviewContent, Double rate, Store store) {
        this.makerId = makerId;
        this.reviewContent = reviewContent;
        this.rate = rate;
        this.store = store;
    }

    public void update(String reviewContent, Double rate) {
        this.reviewContent = reviewContent;
        this.rate = rate;
    }
}
