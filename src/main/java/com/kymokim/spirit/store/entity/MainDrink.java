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

    @Column(name = "is_visible")
    private Boolean isVisible;

    @Builder
    public MainDrink(DrinkType type, Long price, Boolean isVisible){
        this.type = type;
        this.price = price;
        this.isVisible = isVisible != null ? isVisible : Boolean.FALSE;
    }

    public void updatePrice(Long price) {
        this.price = price;
    }

    public void updateVisibility(Boolean isVisible) {
        this.isVisible = isVisible != null ? isVisible : Boolean.FALSE;
    }

    public boolean isVisible() {
        return Boolean.TRUE.equals(this.isVisible);
    }
}
