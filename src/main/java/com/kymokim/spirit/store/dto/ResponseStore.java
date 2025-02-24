package com.kymokim.spirit.store.dto;

import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResponseStore {

    @Getter
    @Builder
    public static class CreateStoreRsDto{
        private Long id;
        public static CreateStoreRsDto toDto(Store store){
            return CreateStoreRsDto.builder()
                    .id(store.getId())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ImageListDto{
        private List<String> imgUrlList;
        public static ImageListDto toDto(List<String> urlList){
            return ImageListDto.builder()
                    .imgUrlList(urlList)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetStoreDto {
        private Long id;
        private String mainImgUrl;
        private String name;
        private String contact;
        private String description;
        private Boolean hasScreen;
        private Boolean isGroupAvailable;
        private CommonStore.LocationDto locationDto;
        private CommonStore.BusinessHoursDto businessHoursDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<DayOfWeek> closedDays;
        private Double storeRate;
        private Long reviewCount;
        private Long likeCount;
        private Boolean isStoreLiked;
        private List<String> imgUrlList;

        public static GetStoreDto toDto(Store store, Double storeRate, Boolean isStoreLiked) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = new HashSet<>();
            if (!store.getMainDrinks().isEmpty()){
                store.getMainDrinks().forEach(mainDrink -> mainDrinkDtos.add(CommonStore.MainDrinkDto.toDto(mainDrink)));
            }

            List<String> imgUrlList = new ArrayList<>();
            if(!store.getImgUrlList().isEmpty()) {
                store.getImgUrlList().forEach(storeImage -> imgUrlList.add(storeImage.getUrl()));
            }

            return GetStoreDto.builder()
                    .id(store.getId())
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .contact(store.getContact())
                    .description(store.getDescription())
                    .hasScreen(store.getHasScreen())
                    .isGroupAvailable(store.getIsGroupAvailable())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .businessHoursDto(CommonStore.BusinessHoursDto.toDto(store.getBusinessHours()))
                    .categories(store.getCategories())
                    .mainDrinkDtos(mainDrinkDtos)
                    .closedDays(store.getClosedDays())
                    .storeRate(storeRate)
                    .reviewCount(store.getReviewCount())
                    .likeCount(store.getLikeCount())
                    .isStoreLiked(isStoreLiked)
                    .imgUrlList(imgUrlList)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SearchStoreDto {
        private Long id;
        private String mainImgUrl;
        private String name;
        private String contact;
        private CommonStore.LocationDto locationDto;
        private CommonStore.BusinessHoursDto businessHoursDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<DayOfWeek> closedDays;
        private Double storeRate;
        private Long reviewCount;

        public static SearchStoreDto toDto(Store store, Double storeRate){

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = new HashSet<>();
            if (!store.getMainDrinks().isEmpty()){
                store.getMainDrinks().forEach(mainDrink -> mainDrinkDtos.add(CommonStore.MainDrinkDto.toDto(mainDrink)));
            }

            return SearchStoreDto.builder()
                    .id(store.getId())
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .contact(store.getContact())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .businessHoursDto(CommonStore.BusinessHoursDto.toDto(store.getBusinessHours()))
                    .categories(store.getCategories())
                    .mainDrinkDtos(mainDrinkDtos)
                    .closedDays(store.getClosedDays())
                    .storeRate(storeRate)
                    .reviewCount(store.getReviewCount())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SearchAllStoreDto {
        private Long id;
        private String mainImgUrl;
        private String name;
        private String contact;
        private CommonStore.LocationDto locationDto;
        private CommonStore.BusinessHoursDto businessHoursDto;
        private Set<Category> categories;
        private Set<DayOfWeek> closedDays;

        public static SearchAllStoreDto toDto(Store store){
            return SearchAllStoreDto.builder()
                    .id(store.getId())
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .contact(store.getContact())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .businessHoursDto(CommonStore.BusinessHoursDto.toDto(store.getBusinessHours()))
                    .categories(store.getCategories())
                    .closedDays(store.getClosedDays())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetByDistanceDto {
        private Long id;
        private String mainImgUrl;
        private String name;
        private CommonStore.LocationDto locationDto;
        private CommonStore.BusinessHoursDto businessHoursDto;
        private Set<Category> categories;
        private Set<DayOfWeek> closedDays;
        private Double storeRate;
        private Long reviewCount;

        public static GetByDistanceDto toDto(Store store, Double storeRate){
            return GetByDistanceDto.builder()
                    .id(store.getId())
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .businessHoursDto(CommonStore.BusinessHoursDto.toDto(store.getBusinessHours()))
                    .categories(store.getCategories())
                    .closedDays(store.getClosedDays())
                    .storeRate(storeRate)
                    .reviewCount(store.getReviewCount())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetByCategoryDto {
        private Long id;
        private String mainImgUrl;
        private String name;
        private CommonStore.LocationDto locationDto;
        private CommonStore.BusinessHoursDto businessHoursDto;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<DayOfWeek> closedDays;
        private Double storeRate;
        private Long reviewCount;
        private Long storeLikeCount;
        private List<MenuListDto> menuList;

        public static GetByCategoryDto toDto(Store store, Double storeRate){

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = new HashSet<>();
            if (!store.getMainDrinks().isEmpty()){
                store.getMainDrinks().forEach(mainDrink -> mainDrinkDtos.add(CommonStore.MainDrinkDto.toDto(mainDrink)));
            }

            List<MenuListDto> menuList = new ArrayList<>();
            if (!store.getMenuList().isEmpty()){
                store.getMenuList().forEach(menu -> {
                    if (menu.getIsMain()) {
                        menuList.add(MenuListDto.toDto(menu));
                    }
                });
            }

            return GetByCategoryDto.builder()
                    .id(store.getId())
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .businessHoursDto(CommonStore.BusinessHoursDto.toDto(store.getBusinessHours()))
                    .mainDrinkDtos(mainDrinkDtos)
                    .closedDays(store.getClosedDays())
                    .storeRate(storeRate)
                    .reviewCount(store.getReviewCount())
                    .storeLikeCount(store.getLikeCount())
                    .menuList(menuList)
                    .build();
        }
    }

    @Getter
    @Builder
    private static class MenuListDto{
        private String name;
        private String price;
        private String imgUrl;

        private static MenuListDto toDto(Menu menu){
            return MenuListDto.builder()
                    .name(menu.getName())
                    .price(menu.getPrice())
                    .imgUrl(menu.getImgUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetByBusinessHoursDto {
        private Long id;
        private String mainImgUrl;
        private String name;
        private CommonStore.LocationDto locationDto;
        private CommonStore.BusinessHoursDto businessHoursDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<DayOfWeek> closedDays;

        public static GetByBusinessHoursDto toDto(Store store){

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = new HashSet<>();
            if (!store.getMainDrinks().isEmpty()){
                store.getMainDrinks().forEach(mainDrink -> mainDrinkDtos.add(CommonStore.MainDrinkDto.toDto(mainDrink)));
            }

            return GetByBusinessHoursDto.builder()
                    .id(store.getId())
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .businessHoursDto(CommonStore.BusinessHoursDto.toDto(store.getBusinessHours()))
                    .categories(store.getCategories())
                    .mainDrinkDtos(mainDrinkDtos)
                    .closedDays(store.getClosedDays())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetByRadiusDto {
        private Long id;
        private String mainImgUrl;
        private String name;
        private CommonStore.LocationDto locationDto;
        private CommonStore.BusinessHoursDto businessHoursDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<DayOfWeek> closedDays;
        private Double storeRate;
        private Long reviewCount;
        private List<MenuListDto> menuList;

        public static GetByRadiusDto toDto(Store store, Double storeRate){

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = new HashSet<>();
            if (!store.getMainDrinks().isEmpty()){
                store.getMainDrinks().forEach(mainDrink -> mainDrinkDtos.add(CommonStore.MainDrinkDto.toDto(mainDrink)));
            }

            List<MenuListDto> menuList = new ArrayList<>();
            if (!store.getMenuList().isEmpty()){
                store.getMenuList().forEach(menu -> {
                    if (menu.getIsMain()) {
                        menuList.add(MenuListDto.toDto(menu));
                    }
                });
            }

            return GetByRadiusDto.builder()
                    .id(store.getId())
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .businessHoursDto(CommonStore.BusinessHoursDto.toDto(store.getBusinessHours()))
                    .mainDrinkDtos(mainDrinkDtos)
                    .categories(store.getCategories())
                    .closedDays(store.getClosedDays())
                    .storeRate(storeRate)
                    .reviewCount(store.getReviewCount())
                    .menuList(menuList)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetLikedStoreDto {
        private Long id;
        private String mainImgUrl;
        private String name;
        private CommonStore.LocationDto locationDto;
        private CommonStore.BusinessHoursDto businessHoursDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<DayOfWeek> closedDays;
        private Double storeRate;
        private Long reviewCount;
        private List<MenuListDto> menuList;

        public static GetLikedStoreDto toDto(Store store, Double storeRate) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = new HashSet<>();
            if (!store.getMainDrinks().isEmpty()) {
                store.getMainDrinks().forEach(mainDrink -> mainDrinkDtos.add(CommonStore.MainDrinkDto.toDto(mainDrink)));
            }

            List<MenuListDto> menuList = new ArrayList<>();
            if (!store.getMenuList().isEmpty()) {
                store.getMenuList().forEach(menu -> {
                    if (menu.getIsMain()) {
                        menuList.add(MenuListDto.toDto(menu));
                    }
                });
            }

            return GetLikedStoreDto.builder()
                    .id(store.getId())
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .businessHoursDto(CommonStore.BusinessHoursDto.toDto(store.getBusinessHours()))
                    .categories(store.getCategories())
                    .mainDrinkDtos(mainDrinkDtos)
                    .closedDays(store.getClosedDays())
                    .storeRate(storeRate)
                    .reviewCount(store.getReviewCount())
                    .menuList(menuList)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetRecentStoreDto {
        private Long id;
        private String mainImgUrl;
        private String name;
        private CommonStore.LocationDto locationDto;
        private CommonStore.BusinessHoursDto businessHoursDto;
        private Set<Category> categories;
        private Set<DayOfWeek> closedDays;
        private Double storeRate;
        private Long reviewCount;

        public static GetRecentStoreDto toDto(Store store, Double storeRate){
            return GetRecentStoreDto.builder()
                    .id(store.getId())
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .businessHoursDto(CommonStore.BusinessHoursDto.toDto(store.getBusinessHours()))
                    .categories(store.getCategories())
                    .closedDays(store.getClosedDays())
                    .storeRate(storeRate)
                    .reviewCount(store.getReviewCount())
                    .build();
        }
    }
}
