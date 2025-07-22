package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.auth.entity.Gender;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Table(name = "likedStore")
@Entity
@Getter
@NoArgsConstructor
@Data
public class LikedStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "storeId", nullable = false)
    private Long storeId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String birthYear;

    @Builder
    public LikedStore(Long storeId, Long userId, Gender gender, String birthYear){
        this.storeId = storeId;
        this.userId = userId;
        this.gender = gender;
        this.birthYear = birthYear;
    }
}
