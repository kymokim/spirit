package com.kymokim.spirit.auth.log.entity;

import com.kymokim.spirit.auth.auth.entity.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "store_view_log")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StoreViewLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long storeId;

    private LocalDate viewDate;

    @CreationTimestamp
    private LocalDateTime viewDateTime;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String birthYear;

    @Builder
    public StoreViewLog(Long userId, Long storeId, Gender gender, String birthYear) {
        this.userId = userId;
        this.storeId = storeId;
        this.viewDate = LocalDate.now();
        this.gender = gender;
        this.birthYear = birthYear;
    }
}