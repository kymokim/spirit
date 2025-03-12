package com.kymokim.spirit.menu.entity;

import com.kymokim.spirit.common.entity.HistoryInfo;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private String price;
    @Column(name = "img_url")
    private String imgUrl;
    @Column(name = "is_main")
    private Boolean isMain;

    @Embedded
    private HistoryInfo historyInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public Menu(String name, String description, String price, Store store, boolean isMain, Long creatorId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.store = store;
        this.isMain = isMain;
        this.historyInfo = new HistoryInfo(creatorId);
    }

    public void update(String name, String description, String price, boolean isMain) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.isMain = isMain;
    }
}
