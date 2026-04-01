package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.common.entity.HistoryInfo;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.drink.entity.Drink;
import com.kymokim.spirit.event.entity.Event;
import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.*;

@Table(name = "store", indexes = {
        @Index(name = "idx_store_latitude", columnList = "latitude"),
        @Index(name = "idx_store_longitude", columnList = "longitude")
})
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

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_always_open")
    private Boolean isAlwaysOpen;

    @Column(name = "is_deleted")
    @ColumnDefault("false")
    private Boolean isDeleted = false;

    @Embedded
    private HistoryInfo historyInfo;

    @Embedded
    private Location location;

    @Embedded
    private FacilitiesInfo facilitiesInfo;

    @CollectionTable(name = "categories", joinColumns = @JoinColumn(name = "store_id"))
    @ElementCollection(targetClass = Category.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "categories")
    private Set<Category> categories;

    @CollectionTable(name = "moods", joinColumns = @JoinColumn(name = "store_id"))
    @ElementCollection(targetClass = Mood.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "mood")
    private Set<Mood> moods = new HashSet<>();

    @CollectionTable(name = "main_drinks", joinColumns = @JoinColumn(name = "store_id"))
    @ElementCollection(targetClass = MainDrink.class)
    @Column(name = "main_drinks")
    private Set<MainDrink> mainDrinks;

    @Column(name = "total_rate")
    private Double totalRate = 0D;

    @Column(name = "post_count")
    private Long postCount = 0L;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OperationInfo> operationInfos = new HashSet<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<Menu> menuList = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<Drink> drinkList = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<StoreImage> imgUrlList = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<BoardImage> boardImgUrlList = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id DESC")
    private List<Event> eventList = new ArrayList<>();

    @Builder
    public Store(String name, String contact, String description, FacilitiesInfo facilitiesInfo,
                 Long creatorId, Location location, Set<Category> categories, Set<MainDrink> mainDrinks,
                 Set<Mood> moods) {
        setName(name);
        this.contact = contact;
        this.description = description;
        setFacilitiesInfo(facilitiesInfo);
        this.historyInfo = new HistoryInfo(creatorId);
        this.location = location;
        setCategories(categories);
        this.mainDrinks = mainDrinks != null ? new HashSet<>(mainDrinks) : new HashSet<>();
        setMoods(moods);
    }

    public static Store fromSuggestion(String name, String contact, String description, FacilitiesInfo facilitiesInfo,
                                       Long creatorId, Location location, Set<Category> categories,
                                       Set<MainDrink> mainDrinks, Set<Mood> moods) {
        Store store = new Store();
        store.setName(name);
        store.contact = contact;
        store.description = description;
        store.setFacilitiesInfo(facilitiesInfo);
        store.historyInfo = new HistoryInfo(creatorId);
        store.location = location;
        store.categories = categories;
        store.mainDrinks = mainDrinks != null ? new HashSet<>(mainDrinks) : new HashSet<>();
        store.setMoods(moods);
        store.delete();
        return store;
    }

    public void setName(String name){
        if (name == null || name.isEmpty()){
            throw new CustomException(StoreErrorCode.STORE_NAME_EMPTY);
        }
        this.name = name;
    }
    public void setCategories(Set<Category> categories){
        if (categories == null || categories.isEmpty()){
            throw new CustomException(StoreErrorCode.STORE_CATEGORIES_EMPTY);
        }
        if (categories.size() > 3) {
            throw new CustomException(StoreErrorCode.STORE_CATEGORIES_LIMIT_EXCEEDED);
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
    public void addDrinkList(Drink drink) {
        this.drinkList.add(drink);
    }
    public void removeDrinkList(Drink drink){
        this.drinkList.remove(drink);
    }
    public void addImgUrlList(StoreImage storeImage){
        this.imgUrlList.add(storeImage);
    }
    public void removeImgUrlList(StoreImage storeImage){
        this.imgUrlList.remove(storeImage);
    }
    public void addBoardImgUrlList(BoardImage boardImage){
        this.boardImgUrlList.add(boardImage);
    }
    public void removeBoardImgUrlList(BoardImage boardImage){
        this.boardImgUrlList.remove(boardImage);
    }
    public void addEventList(Event event) {
        this.eventList.add(event);
    }
    public void removeEventList(Event event) {
        this.eventList.remove(event);
    }
    public void increaseTotalRate(Double rate) {
        this.totalRate += rate;
    }
    public void decreaseTotalRate(Double rate) {
        if (this.totalRate >= rate) {
            this.totalRate -= rate;
        }
    }
    public void increasePostCount() {
        this.postCount++;
    }
    public void decreasePostCount() {
        if (this.postCount > 0) {
            this.postCount--;
        }
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

    public void setFacilitiesInfo(FacilitiesInfo facilitiesInfo) {
        if (facilitiesInfo == null) {
            throw new CustomException(StoreErrorCode.FACILITIES_INFO_EMPTY);
        }
        this.facilitiesInfo = facilitiesInfo;
    }

    public void setMoods(Set<Mood> moods) {
        if (moods == null || moods.isEmpty()) {
            this.moods = new HashSet<>();
            return;
        }

        if (moods.size() > 4) {
            throw new CustomException(StoreErrorCode.STORE_MOODS_LIMIT_EXCEEDED);
        }

        this.moods = new HashSet<>(moods);
    }
}
