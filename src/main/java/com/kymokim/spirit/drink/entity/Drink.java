package com.kymokim.spirit.drink.entity;

import com.kymokim.spirit.store.entity.Store;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "drink")
@Entity
@Getter
@NoArgsConstructor
@Data
public class Drink {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private String price;
    @Column(name = "img_url")
    private String imgUrl;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "drink_type")
    private DrinkType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public Drink(String name, String description, String price, Store store, DrinkType type) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.store = store;
        this.type = type;
    }

    public void update(String name, String description, String price, DrinkType type) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
    }
}
