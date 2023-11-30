package com.example.Fooding.menu.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "likedMenu")
@Entity
@Getter
@NoArgsConstructor
@Data
public class LikedMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long liedMenuId;

    @Column(name = "menuId")
    private Long menuId;

    @Column(name = "userId")
    private Long userId;

    @Builder
    public LikedMenu(Long menuId, Long userId) {
        this.menuId = menuId;
        this.userId = userId;
    }
}
