package com.kymokim.spirit.store.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Table(name = "storeManager")
@Entity
@Getter
@NoArgsConstructor
@Data
public class StoreManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @CreationTimestamp
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Builder
    public StoreManager(Long storeId, Long userId){
        this.storeId = storeId;
        this.userId = userId;
    }
}
