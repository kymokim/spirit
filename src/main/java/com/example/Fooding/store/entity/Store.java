package com.example.Fooding.store.entity;

import com.example.Fooding.liveReview.entity.LiveReview;
import com.example.Fooding.menu.entity.Menu;
import com.example.Fooding.review.entity.Review;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "store")
@Entity
@Getter
@NoArgsConstructor
@Data
public class Store {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long storeId;

    @Column(name = "writerId")
    private Long writerId;

    @Column(name = "ownerId")
    private Long ownerId;

    @Column(name = "storeName")
    private String storeName;

    @Column(name = "category")
    private String category;

    @Column(name = "address")
    private String address;

    @Column(name = "storeNumber")
    private String storeNumber;

    @Column(name = "storeContent")
    private String storeContent;

    @Column(name = "imgUrl")
    private String imgUrl;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "openHour")
    private String openHour;

    @Column(name = "closeHour")
    private String closeHour;

    @Column(name = "totalRate")
    private Double totalRate = 0D;

    @Column(name = "reviewCount")
    private Long reviewCount = 0L;

    @Column(name = "storeLikeCount")
    private Long storeLikeCount = 0L;

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Menu> menuList = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LiveReview> liveReviewList = new ArrayList<>();

    @Builder
    public Store(Long writerId, String storeName, String category, String address, String storeNumber, String storeContent,
                 String longitude, String latitude, String openHour, String closeHour) {
        this.writerId = writerId;
        this.storeName = storeName;
        this.category = category;
        this.address = address;
        this.storeNumber = storeNumber;
        this.storeContent = storeContent;
        this.longitude = longitude;
        this.latitude = latitude;
        this.openHour = openHour;
        this.closeHour = closeHour;
    }

    public void update(String storeName, String category, String address, String storeNumber, String storeContent,
                       String longitude, String latitude, String openHour, String closeHour) {
        this.storeName = storeName;
        this.category = category;
        this.address = address;
        this.storeNumber = storeNumber;
        this.storeContent = storeContent;
        this.longitude = longitude;
        this.latitude = latitude;
        this.openHour = openHour;
        this.closeHour = closeHour;
    }

    public void addMenu(Menu menu) {
        this.menuList.add(menu);
    }

    public void increaseReviewCount() {
        this.reviewCount++;
    }
    public void decreaseReviewCount() {
        this.reviewCount--;
    }
}
