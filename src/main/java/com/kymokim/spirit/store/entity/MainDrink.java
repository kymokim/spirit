package com.kymokim.spirit.store.entity;

import com.kymokim.spirit.drink.entity.DrinkType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class MainDrink {
    @Enumerated(value = EnumType.STRING)
    @Column(name = "drink_type")
    private DrinkType type;

    @Column(name = "drink_price")
    private Long price;

    @Builder
    public MainDrink(DrinkType type, Long price){
        this.type = type;
        this.price = price;
    }
}
