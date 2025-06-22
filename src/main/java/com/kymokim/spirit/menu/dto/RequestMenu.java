package com.kymokim.spirit.menu.dto;

import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.menu.entity.MenuType;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class RequestMenu {

    @Data
    @Builder
    public static class CreateMenuDto {
        @NotEmpty
        private String name;
        private String description;
        @NotEmpty
        private String price;
        @NotEmpty
        private Long storeId;
        @NotEmpty
        private MenuType menuType;

        public Menu toEntity(Store store, Integer sortOrder, Long creatorId) {
            return Menu.builder()
                    .name(this.name)
                    .description(this.description)
                    .price(this.price)
                    .menuType(this.menuType)
                    .sortOrder(sortOrder)
                    .creatorId(creatorId)
                    .store(store)
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateMenuDto {
        @NotEmpty
        private String name;
        private String description;
        @NotEmpty
        private String price;
        @NotEmpty
        private MenuType menuType;

        public Menu toEntity(Menu menu) {
            menu.update(this.name, this.description, this.price, this.menuType);
            return menu;
        }
    }

    @Data
    @Builder
    public static class UpdateMenuSortOrderDto{
        @NotEmpty
        private Long storeId;
        @NotEmpty
        private List<Long> menuIdInOrderList;
    }
}
