package com.kymokim.spirit.drink.dto;

import com.kymokim.spirit.drink.entity.Drink;
import com.kymokim.spirit.drink.entity.DrinkType;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class RequestDrink {

    @Data
    @Builder
    public static class CreateDrinkDto {
        @NotEmpty
        private String name;
        private String description;
        @NotEmpty
        private String price;
        @NotNull
        private DrinkType type;
        @NotNull
        private Long storeId;

        public Drink toEntity(Store store, Integer sortOrder, Long creatorId) {
            return Drink.builder()
                    .name(this.name)
                    .description(this.description)
                    .price(this.price)
                    .type(this.type)
                    .creatorId(creatorId)
                    .sortOrder(sortOrder)
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
        @NotNull
        private DrinkType type;

        public Drink toEntity(Drink drink) {
            drink.update(this.name, this.description, this.price, this.type);
            return drink;
        }
    }

    @Data
    @Builder
    public static class UpdateDrinkSortOrderDto{
        @NotNull
        private Long storeId;
        @NotEmpty
        private List<Long> drinkIdInOrderList;
    }
}
