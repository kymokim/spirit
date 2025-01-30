package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.drink.entity.Drink;
import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.DayOfWeek;
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
    private Long id;

    @Column(name = "main_img_url")
    private String mainImgUrl;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact")
    private String contact;

    @Column(name = "description")
    private String description;

    @Column(name = "has_screen", nullable = false)
    private Boolean hasScreen;

    @Column(name = "is_group_available", nullable = false)
    private Boolean isGroupAvailable;

    @Embedded
    private HistoryInfo historyInfo;

    @Embedded
    private Location location;

    @Embedded
    private BusinessHours businessHours;

    @CollectionTable(name = "categories", joinColumns = @JoinColumn(name = "store_id"))
    @ElementCollection(targetClass = Category.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "categories", nullable = false)
    private Set<Category> categories;

    @CollectionTable(name = "main_drinks", joinColumns = @JoinColumn(name = "store_id"))
    @ElementCollection(targetClass = MainDrink.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "main_drinks")
    private Set<MainDrink> mainDrinks;

    @CollectionTable(name = "closed_days", joinColumns = @JoinColumn(name = "store_id"))
    @ElementCollection(targetClass = DayOfWeek.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "closed_days")
    private Set<DayOfWeek> closedDays;

    @Column(name = "total_rate")
    private Double totalRate = 0D;

    @Column(name = "review_count")
    private Long reviewCount = 0L;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Menu> menuList = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StoreImage> imgUrlList = new ArrayList<>();

    @Builder
    public Store(String name, String contact, String description, Boolean hasScreen, Boolean isGroupAvailable, Long creatorId,
                 Location location, BusinessHours businessHours, Set<Category> categories, Set<MainDrink> mainDrinks, Set<DayOfWeek> closedDays) {
        setName(name);
        this.contact = contact;
        this.description = description;
        setHasScreen(hasScreen);
        setIsGroupAvailable(isGroupAvailable);
        this.historyInfo = new HistoryInfo(creatorId);
        this.location = location;
        this.businessHours = businessHours;
        setCategories(categories);
        this.mainDrinks = mainDrinks;
        this.closedDays = closedDays;
    }

    public void setName(String name){
        if (name == null || name.isEmpty()){
            throw new CustomException(StoreErrorCode.STORE_NAME_EMPTY);
        }
        this.name = name;
    }

    public void setHasScreen(Boolean hasScreen){
        if (hasScreen == null ){
            throw new CustomException(StoreErrorCode.HAS_SCREEN_EMPTY);
        }
        this.hasScreen = hasScreen;
    }

    public void setIsGroupAvailable(Boolean isGroupAvailable){
        if (isGroupAvailable == null ){
            throw new CustomException(StoreErrorCode.IS_GROUP_AVAILABLE_EMPTY);
        }
        this.isGroupAvailable = isGroupAvailable;
    }

    public void setCategories(Set<Category> categories){
        if (categories == null || categories.isEmpty()){
            throw new CustomException(StoreErrorCode.STORE_CATEGORIES_EMPTY);
        }
        this.categories = categories;
    }

    public void addMenuList(Menu menu) {
        this.menuList.add(menu);
    }
    public void addImgUrlList(StoreImage storeImage){
        this.imgUrlList.add(storeImage);
    }
    public void removeImgUrlList(StoreImage storeImage){
        this.imgUrlList.remove(storeImage);
    }
    public void increaseReviewCount() {
        this.reviewCount++;
    }
    public void decreaseReviewCount() {
        this.reviewCount--;
    }
    public void increaseLikeCount(){ this.likeCount++; }
    public void decreaseLikeCount(){ this.likeCount--; }
}
