package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Embeddable
public class Location {
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    protected Location(){}

    @Builder
    public Location(String address, String addressDetail, double latitude, double longitude){
        setAddress(address);
        this.addressDetail = addressDetail;
        setLatitude(latitude);
        setLongitude(longitude);
    }

    private void setAddress(String address){
        if (address == null || address.isEmpty()){
            throw new CustomException(StoreErrorCode.LOCATION_ADDRESS_EMPTY);
        }
        this.address = address;
    }

    private void setLatitude(double latitude){
        if (latitude == 0.0 || Double.isNaN(latitude)){
            throw new CustomException(StoreErrorCode.LOCATION_LATITUDE_EMPTY);
        }
        this.latitude = latitude;
    }

    private void setLongitude(double longitude){
        if (longitude == 0.0 || Double.isNaN(longitude)){
            throw new CustomException(StoreErrorCode.LOCATION_LONGITUDE_EMPTY);
        }
        this.longitude = longitude;
    }
}
