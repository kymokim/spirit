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

        public static Drink toEntity(CreateDrinkDto createDrinkDto, Store store) {
            return Drink.builder()
                    .name(createDrinkDto.getName())
                    .description(createDrinkDto.getDescription())
                    .price(createDrinkDto.getPrice())
                    .type(createDrinkDto.getType())
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

        public static Drink toEntity(Drink drink, UpdateDrinkDto updateDrinkDto) {
            drink.update(updateDrinkDto.getName(), updateDrinkDto.getDescription(), updateDrinkDto.getPrice(), updateDrinkDto.getType());
            return drink;
        }
    }
}
