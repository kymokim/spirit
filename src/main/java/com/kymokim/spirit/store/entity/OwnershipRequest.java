package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.auth.entity.Auth;
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

    @Embedded
    @Column(name = "business_location")
    private Location business_location;

    @Column(name = "business_registration_certificate_img_url")
    private String businessRegistrationCertificateImgUrl;

    @Builder
    public OwnershipRequest(Store store, Auth requester, String receivedStoreName, String receivedStoreContact, String receivedUserContact, String businessRegistrationNumber,
                            List<RepresentativeInfo> representativeInfoList, String openingDate, Location business_location, String businessRegistrationCertificateImgUrl){
        this.store = store;
        this.requester = requester;
        this.receivedStoreName = receivedStoreName;
        this.receivedStoreContact = receivedStoreContact;
        this.receivedUserContact = receivedUserContact;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.representativeInfoList = representativeInfoList;
        this.openingDate = openingDate;
        this.business_location = business_location;
        this.businessRegistrationCertificateImgUrl = businessRegistrationCertificateImgUrl;
    }
}
