package com.kymokim.spirit.review.entity;

import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.entity.StoreImage;
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

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewImage> imgUrlList = new ArrayList<>();

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

    public void addImgUrlList(ReviewImage reviewImage){
        this.imgUrlList.add(reviewImage);
    }
    public void removeImgUrlList(ReviewImage reviewImage){
        this.imgUrlList.remove(reviewImage);
    }
}
