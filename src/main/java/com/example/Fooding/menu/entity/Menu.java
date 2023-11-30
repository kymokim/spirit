package com.example.Fooding.menu.entity;

import com.example.Fooding.store.entity.Store;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(name = "menuLikeCount")
    private Long menuLikeCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public Menu(String menuName, String menuContent, Long price, Store store ) {
        this.menuName = menuName;
        this.menuContent = menuContent;
        this.price = price;
        this.store = store;
    }

    public void update(String menuName, String menuContent, Long price ) {
        this.menuName = menuName;
        this.menuContent = menuContent;
        this.price = price;
    }

    public void increaseMenuLikeCount(){
        this.menuLikeCount++;
    }
    public void decreaseMenuLikeCount(){
        this.menuLikeCount--;
    }
}
