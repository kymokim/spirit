package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.common.entity.HistoryInfo;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.*;

@Table(name = "store")
@Entity
@Getter
@NoArgsConstructor
@Data
public class Store {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "is_always_open")
    private Boolean isAlwaysOpen;

    @Column(name = "is_deleted")
    @ColumnDefault("false")
    private Boolean isDeleted = false;

    @Embedded
    private HistoryInfo historyInfo;

    @Embedded
    private Location location;

    @CollectionTable(name = "categories", joinColumns = @JoinColumn(name = "store_id"))
    @ElementCollection(targetClass = Category.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "categories", nullable = false)
    private Set<Category> categories;

    @CollectionTable(name = "main_drinks", joinColumns = @JoinColumn(name = "store_id"))
    @ElementCollection(targetClass = MainDrink.class)
    @Column(name = "main_drinks")
    private Set<MainDrink> mainDrinks;

    @Column(name = "total_rate")
    private Double totalRate = 0D;

    @Column(name = "review_count")
    private Long reviewCount = 0L;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OperationInfo> operationInfos = new HashSet<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Menu> menuList = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StoreImage> imgUrlList = new ArrayList<>();

    @Builder
    public Store(String name, String contact, String description, Boolean hasScreen, Boolean isGroupAvailable,
                 Long creatorId, Location location, Set<Category> categories, Set<MainDrink> mainDrinks) {
        setName(name);
        this.contact = contact;
        this.description = description;
        setHasScreen(hasScreen);
        setIsGroupAvailable(isGroupAvailable);
        this.historyInfo = new HistoryInfo(creatorId);
        this.location = location;
        setCategories(categories);
        this.mainDrinks = mainDrinks;
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
    public void addOperationInfos(OperationInfo operationInfo){
        this.operationInfos.add(operationInfo);
    }
    public void removeOperationInfos(OperationInfo operationInfo){
        this.operationInfos.remove(operationInfo);
    }
    public void addMenuList(Menu menu) {
        this.menuList.add(menu);
    }
    public void removeMenuList(Menu menu){
        this.menuList.remove(menu);
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
    public void increaseLikeCount(){
        this.likeCount++;
    }
    public void decreaseLikeCount(){
        this.likeCount--;
    }
    public void delete(){
        this.isDeleted = true;
    }
}
