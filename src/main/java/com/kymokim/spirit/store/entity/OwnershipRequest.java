package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "ownership_request")
@Entity
@Getter
@NoArgsConstructor
@Data
public class OwnershipRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id", nullable = false)
    private Auth requester;

    @Column(name = "received_store_name")
    private String receivedStoreName;

    @Column(name = "received_store_contact")
    private String receivedStoreContact;

    @Column(name = "received_user_contact")
    private String receivedUserContact;

    @Column(name = "business_registration_number")
    private String businessRegistrationNumber;

    @CollectionTable(name = "representative_info_list", joinColumns = @JoinColumn(name = "ownership_request_id"))
    @ElementCollection(targetClass = RepresentativeInfo.class)
    @Column(name = "representative_info_list")
    private List<RepresentativeInfo> representativeInfoList = new ArrayList<>();

    @Column(name = "opening_date")
    private String openingDate;

    @Column(name = "liquorReportNo")
    private String liquorReportNumber;

    @Embedded
    @Column(name = "business_location")
    private Location businessLocation;

    @Column(name = "business_registration_certificate_img_url")
    private String businessRegistrationCertificateImgUrl;

    @Builder
    public OwnershipRequest(Store store, Auth requester, String receivedStoreName, String receivedStoreContact, String receivedUserContact, String businessRegistrationNumber,
                            List<RepresentativeInfo> representativeInfoList, String openingDate, String liquorReportNumber, Location businessLocation, String businessRegistrationCertificateImgUrl){
        this.store = store;
        this.requester = requester;
        setReceivedStoreName(receivedStoreName);
        setReceivedStoreContact(receivedStoreContact);
        setReceivedUserContact(receivedUserContact);
        setBusinessRegistrationNumber(businessRegistrationNumber);
        this.representativeInfoList = representativeInfoList;
        setOpeningDate(openingDate);
        setLiquorReportNumber(liquorReportNumber);
        this.businessLocation = businessLocation;
        this.businessRegistrationCertificateImgUrl = businessRegistrationCertificateImgUrl;
    }

    public void setReceivedStoreName(String receivedStoreName) {
        if(receivedStoreName == null || receivedStoreName.isEmpty()) {
            throw new CustomException(StoreErrorCode.OWNERSHIP_STORE_NAME_EMPTY);
        }
        this.receivedStoreName = receivedStoreName;
    }

    public void setReceivedStoreContact(String receivedStoreContact) {
        if(receivedStoreContact == null || receivedStoreContact.isEmpty()) {
            throw new CustomException(StoreErrorCode.OWNERSHIP_STORE_CONTACT_EMPTY);
        }
        this.receivedStoreContact = receivedStoreContact;
    }

    public void setReceivedUserContact(String receivedUserContact) {
        if(receivedUserContact == null || receivedUserContact.isEmpty()) {
            throw new CustomException(StoreErrorCode.OWNERSHIP_USER_CONTACT_EMPTY);
        }
        this.receivedUserContact = receivedUserContact;
    }

    public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
        if(businessRegistrationNumber == null || businessRegistrationNumber.isEmpty()) {
            throw new CustomException(StoreErrorCode.OWNERSHIP_BUSINESS_NUMBER_EMPTY);
        }
        this.businessRegistrationNumber = businessRegistrationNumber;
    }


    public void setOpeningDate(String openingDate) {
        if(openingDate == null || openingDate.isEmpty()) {
            throw new CustomException(StoreErrorCode.OWNERSHIP_OPENING_DATE_EMPTY);
        }
        this.openingDate = openingDate;
    }


    public void setLiquorReportNumber(String liquorReportNumber) {
        if(liquorReportNumber == null || liquorReportNumber.isEmpty()) {
            throw new CustomException(StoreErrorCode.OWNERSHIP_LIQUOR_REPORT_EMPTY);
        }
        this.liquorReportNumber = liquorReportNumber;
    }



}
