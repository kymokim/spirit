package com.kymokim.spirit.review.entity;

import com.kymokim.spirit.store.entity.Store;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "review_image")
@Entity
@Getter
@NoArgsConstructor
@Data
public class ReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Builder
    public ReviewImage(String url, Review review){
        this.url = url;
        this.review = review;
    }
}
