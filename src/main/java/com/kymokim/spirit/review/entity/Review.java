package com.kymokim.spirit.review.entity;

import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
@Table
@Entity
@Getter
@NoArgsConstructor
@Data
public class Review {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "writer_id")
    private Long writerId;

    @Column(name = "writer_nickname")
    private String writerNickname;

    @Column(name = "content")
    private String content;

    @Column(name = "rate")
    private Double rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public Review(Long writerId,String writerNickname, String content, Double rate, Store store) {
        this.writerNickname = writerNickname;
        this.writerId = writerId;
        this.content = content;
        this.rate = rate;
        this.store = store;
    }

    public void update(String content, Double rate) {
        this.content = content;
        this.rate = rate;
    }
}
