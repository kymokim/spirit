package com.kymokim.spirit.store.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "manager_invitation")
@Entity
@Getter
@Data
@NoArgsConstructor
public class ManagerInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_image")
    private String storeImage;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Builder
    public ManagerInvitation(Long storeId, String storeName, String storeImage) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeImage = storeImage;
        this.expiresAt = LocalDateTime.now().plusDays(1);
    }
}
