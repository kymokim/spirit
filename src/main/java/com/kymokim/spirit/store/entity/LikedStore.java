package com.kymokim.spirit.store.entity;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long likedStoreId;

    @Column(name = "storeId")
    private Long storeId;

    @Column(name = "userId")
    private Long userId;

    @Builder
    public LikedStore(Long storeId, Long userId){
        this.storeId = storeId;
        this.userId = userId;
    }
}
