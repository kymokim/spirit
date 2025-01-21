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
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private Long price;
    @Column(name = "img_url")
    private String imgUrl;
    @Column(name = "is_main")
    private Boolean isMain;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public Menu(String name, String description, Long price, Store store, boolean isMain) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.store = store;
        this.isMain = isMain;
    }

    public void update(String name, String description, Long price, boolean isMain) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.isMain = isMain;
    }
}
