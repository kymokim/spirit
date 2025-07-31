package com.kymokim.spirit.main.drink.dto;

import com.kymokim.spirit.main.drink.entity.Drink;
import com.kymokim.spirit.main.drink.entity.DrinkType;
import lombok.Builder;
import lombok.Getter;

public class ResponseDrink {

    @Builder
    @Getter
    public static class GetDrinkDto {
        private Long id;
        private String name;
        private String description;
        private String price;
        private Long storeId;
        private String imgUrl;
        private DrinkType type;

        public static GetDrinkDto toDto(Drink drink) {
            return GetDrinkDto.builder()
                    .id(drink.getId())
                    .name(drink.getName())
                    .description(drink.getDescription())
                    .price(drink.getPrice())
                    .storeId(drink.getStore().getId())
                    .imgUrl(drink.getImgUrl())
                    .type(drink.getType())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class DrinkListDto {
        private Long id;
        private String name;
        private String description;
        private String price;
        private String imgUrl;
        private DrinkType type;

        public static DrinkListDto toDto(Drink drink) {
            return DrinkListDto.builder()
                    .id(drink.getId())
                    .name(drink.getName())
                    .description(drink.getDescription())
                    .price(drink.getPrice())
                    .imgUrl(drink.getImgUrl())
                    .type(drink.getType())
                    .build();
        }
    }
}
