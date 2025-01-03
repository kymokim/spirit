package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.liveReview.entity.LiveReview;
import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.review.entity.Review;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @ElementCollection(targetClass = Category.class)
    @CollectionTable(name = "store_categories", joinColumns = @JoinColumn(name = "store_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "categories")
    private Set<Category> categories;

    @Column(name = "address")
    private String address;

    @Column(name = "addressDetail")
    private String addressDetail;

    @Column(name = "storeNumber")
    private String storeNumber;

    @Column(name = "storeContent")
    private String storeContent;

    @Column(name = "mainImgUrl")
    private String mainImgUrl;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "openHour")
    private LocalTime openHour;

    @Column(name = "closeHour")
    private LocalTime closeHour;

    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "store_closed_days", joinColumns = @JoinColumn(name = "store_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "closedDays")
    private Set<DayOfWeek> closedDays;

    @Column(name = "hasScreen")
    private Boolean hasScreen;

    @Column(name = "isGroupAvailable")
    private Boolean isGroupAvailable;

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

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StoreImage> imgUrlList = new ArrayList<>();

    @Builder
    public Store(Long writerId, String storeName, Set<Category> categories,
                 String address, String addressDetail, String storeNumber, String storeContent, double longitude, double latitude,
                 LocalTime openHour, LocalTime closeHour, Set<DayOfWeek> closedDays, Boolean hasScreen, Boolean isGroupAvailable) {
        this.writerId = writerId;
        this.storeName = storeName;
        this.categories = categories;
        this.address = address;
        this.addressDetail = addressDetail;
        this.storeNumber = storeNumber;
        this.storeContent = storeContent;
        this.longitude = longitude;
        this.latitude = latitude;
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.closedDays = closedDays;
        this.hasScreen = hasScreen;
        this.isGroupAvailable = isGroupAvailable;
    }

    public void update(String storeName, Set<Category> categories,
                       String address, String addressDetail, String storeNumber, String storeContent, double longitude, double latitude,
                       LocalTime openHour, LocalTime closeHour, Set<DayOfWeek> closedDays, Boolean hasScreen, Boolean isGroupAvailable) {
        this.storeName = storeName;
        this.categories = categories;
        this.address = address;
        this.addressDetail = addressDetail;
        this.storeNumber = storeNumber;
        this.storeContent = storeContent;
        this.longitude = longitude;
        this.latitude = latitude;
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.closedDays = closedDays;
        this.hasScreen = hasScreen;
        this.isGroupAvailable = isGroupAvailable;
    }

    public void addMenu(Menu menu) {
        this.menuList.add(menu);
    }
    public void addImgUrlList(StoreImage storeImage){
        this.imgUrlList.add(storeImage);
    }
    public void increaseReviewCount() {
        this.reviewCount++;
    }
    public void decreaseReviewCount() {
        this.reviewCount--;
    }
    public void increaseStoreLikeCount(){ this. storeLikeCount++; }
    public void decreaseStoreLikeCount(){ this. storeLikeCount--; }
}
