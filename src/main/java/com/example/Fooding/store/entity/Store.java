package com.example.Fooding.store.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "store")
@Entity
@Getter
@NoArgsConstructor
@Data


public class Store {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long storeId;

    //    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    //    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ownerId;

    @Column(name = "storeName")
    private String storeName;

    @Column(name = "category")
    private String category;

    @Column(name = "totalRate")
    private Long totalRate;

    @Column(name = "address")
    private String address;

    @Column(name = "longitude")
    private Long longitude;

    @Column(name = "latitude")
    private Long latitude;

    @Column(name = "openHour")
    private Long openHour;

    @Column(name = "closeHour")
    private Long closeHour;

    @Column(name = "storeNumber")
    private Long storeNumber;

    @Column(name = "storeContent")
    private String storeContent;

    @Column(name = "storeLikeCount")
    private Long storeLikeCount;

    @Builder
    public Store(Long ownerId, String storeName, String address, Long longitude, Long latitude,
                 Long openHour, Long closeHour, Long storeNumber, String storeContent) {
        this.ownerId = ownerId;
        this.storeName = storeName;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.storeNumber = storeNumber;
        this.storeContent = storeContent;
    }

    public void update(Long storeId, String storeName, String address, Long longitude, Long latitude,
                       Long openHour, Long closeHour, Long storeNumber, String storeContent) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.storeNumber = storeNumber;
        this.storeContent = storeContent;
    }


    public void nearlist(Long longitude, Long latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }



}
