package com.kymokim.spirit.log.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "ownership_log")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OwnershipLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long storeId;

    private LocalDate approvedDate;

    @CreationTimestamp
    private LocalDateTime approvedDateTime;

    @Builder
    public OwnershipLog(Long storeId) {
        this.storeId = storeId;
        this.approvedDate = LocalDate.now();
    }
}

