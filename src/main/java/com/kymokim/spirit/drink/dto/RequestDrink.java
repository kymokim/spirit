package com.kymokim.spirit.drink.dto;

import com.kymokim.spirit.drink.entity.Drink;
import com.kymokim.spirit.drink.entity.DrinkType;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

public class RequestDrink {

    @Data
    @Builder
    public static class CreateDrinkDto {
        @NotEmpty
        private String name;
        private String description;
        @NotEmpty
        private String price;
        @NotEmpty
        private Long storeId;
        @NotEmpty
        private DrinkType type;

        public Drink toEntity(Store store) {
            return Drink.builder()
                    .name(this.name)
                    .description(this.description)
                    .price(this.price)
                    .type(this.type)
                    .store(store)
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateDrinkDto {
        @NotEmpty
        private String name;
        private String description;
        @NotEmpty
        private String price;
        @NotEmpty
        private DrinkType type;

        public Drink toEntity(Drink drink) {
            drink.update(this.name, this.description, this.price, this.type);
            return drink;
        }
    }
}
