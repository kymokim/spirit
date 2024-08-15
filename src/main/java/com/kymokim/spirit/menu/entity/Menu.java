package com.kymokim.spirit.menu.entity;

import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Table(name = "menu")
@Entity
@Getter
@NoArgsConstructor
@Data
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long menuId;

    @Column(name = "menuName")
    private String menuName;
    @Column(name = "menuContent")
    private String menuContent;
    @Column(name = "price")
    private Long price;
    @Column(name = "imgUrl")
    private String imgUrl;
    @Column(name = "isMain")
    private Boolean isMain;

    @Column(name = "menuLikeCount")
    private Long menuLikeCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public Menu(String menuName, String menuContent, Long price, Store store, boolean isMain) {
        this.menuName = menuName;
        this.menuContent = menuContent;
        this.price = price;
        this.store = store;
        this.isMain = isMain;
    }

    public void update(String menuName, String menuContent, Long price, boolean isMain) {
        this.menuName = menuName;
        this.menuContent = menuContent;
        this.price = price;
        this.isMain = isMain;
    }
}
