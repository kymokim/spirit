package com.kymokim.spirit.store.dto;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.Gender;
import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.common.service.AESUtil;
import com.kymokim.spirit.drink.entity.DrinkType;
import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.menu.entity.MenuType;
import com.kymokim.spirit.store.entity.*;
import com.kymokim.spirit.store.repository.dto.StoreMarkerProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

@RequiredArgsConstructor
public class ResponseStore {

    @Getter
    @Builder
    public static class CreateStoreRsDto {
        private Long id;

        public static CreateStoreRsDto toDto(Store store) {
            return CreateStoreRsDto.builder()
                    .id(store.getId())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class BusinessValidationDto {
        private Boolean isValid;

        public static BusinessValidationDto of(Boolean isValid) {
            return BusinessValidationDto.builder()
                    .isValid(isValid)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ImageListDto {
        private List<String> imgUrlList;

        public static ImageListDto toDto(List<String> urlList) {
            return ImageListDto.builder()
                    .imgUrlList(urlList)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetStoreDto {
        private Long id;
        private Boolean isOwner;
        private Boolean isUpdatable;
        private String mainImgUrl;
        private String name;
        private String contact;
        private String description;
        private CommonStore.FacilitiesInfoDto facilitiesInfoDto;
        private Boolean isAlwaysOpen;
        private Boolean isDeleted;
        private CommonStore.LocationDto locationDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
        private Set<Mood> moods;
        private Double storeRate;
        private Long postCount;
        private Long likeCount;
        private Boolean isStoreLiked;
        private List<String> imgUrlList;
        private List<BoardImageListDto> boardImageDtoList;

        public static GetStoreDto toDto(Store store, Boolean isOwner, Boolean isUpdatable, Double storeRate, Boolean isStoreLiked) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = extractVisibleMainDrinks(store);

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                store.getOperationInfos().forEach(operationInfo -> operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo)));
            }

            Set<Mood> moods = new HashSet<>();
            if (!store.getMoods().isEmpty()) {
                moods.addAll(store.getMoods());
            }

            List<String> imgUrlList = new ArrayList<>();
            if (!store.getImgUrlList().isEmpty()) {
                store.getImgUrlList().forEach(storeImage -> imgUrlList.add(storeImage.getUrl()));
            }

            List<BoardImageListDto> boardImageDtoList = new ArrayList<>();
            if (!store.getBoardImgUrlList().isEmpty()) {
                store.getBoardImgUrlList().forEach(boardImage -> boardImageDtoList.add(BoardImageListDto.toDto(boardImage)));
            }

            return GetStoreDto.builder()
                    .id(store.getId())
                    .isOwner(isOwner)
                    .isUpdatable(isUpdatable)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .contact(store.getContact())
                    .description(store.getDescription())
                    .facilitiesInfoDto(CommonStore.FacilitiesInfoDto.toDto(store.getFacilitiesInfo()))
                    .isAlwaysOpen(store.getIsAlwaysOpen())
                    .isDeleted(store.getIsDeleted())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .categories(store.getCategories())
                    .mainDrinkDtos(mainDrinkDtos)
                    .operationInfoDtos(operationInfoDtos)
                    .moods(moods)
                    .storeRate(storeRate)
                    .postCount(store.getPostCount())
                    .likeCount(store.getLikeCount())
                    .isStoreLiked(isStoreLiked)
                    .imgUrlList(imgUrlList)
                    .boardImageDtoList(boardImageDtoList)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetStorePreviewDto {
        private Long id;
        private String mainImgUrl;
        private String name;
        private String contact;
        private Boolean isAlwaysOpen;
        private Boolean isDeleted;
        private CommonStore.LocationDto locationDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
        private Double storeRate;
        private Long postCount;
        private Long likeCount;
        private Boolean isStoreLiked;
        private List<String> imgUrlList;

        public static GetStorePreviewDto toDto(Store store, Double storeRate, Boolean isStoreLiked) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = extractVisibleMainDrinks(store);

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                store.getOperationInfos().forEach(operationInfo -> operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo)));
            }

            List<String> imgUrlList = new ArrayList<>();
            if (!store.getImgUrlList().isEmpty()) {
                store.getImgUrlList().forEach(storeImage -> imgUrlList.add(storeImage.getUrl()));
            }

            return GetStorePreviewDto.builder()
                    .id(store.getId())
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .contact(store.getContact())
                    .isAlwaysOpen(store.getIsAlwaysOpen())
                    .isDeleted(store.getIsDeleted())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .categories(store.getCategories())
                    .mainDrinkDtos(mainDrinkDtos)
                    .operationInfoDtos(operationInfoDtos)
                    .storeRate(storeRate)
                    .postCount(store.getPostCount())
                    .likeCount(store.getLikeCount())
                    .isStoreLiked(isStoreLiked)
                    .imgUrlList(imgUrlList)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class BoardImageListDto {
        private Long id;
        private String url;
        private BoardType boardType;

        public static BoardImageListDto toDto(BoardImage boardImage) {
            return BoardImageListDto.builder()
                    .id(boardImage.getId())
                    .url(boardImage.getUrl())
                    .boardType(boardImage.getBoardType())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SearchStoreDto {
        private Long id;
        private Boolean isCertified;
        private String mainImgUrl;
        private String name;
        private String contact;
        private Boolean isAlwaysOpen;
        private CommonStore.LocationDto locationDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
        private Double storeRate;
        private Long postCount;

        public static SearchStoreDto toDto(Store store, Double storeRate) {
            return toDto(store, storeRate, null);
        }

        public static SearchStoreDto toDto(Store store, Double storeRate, DrinkType requestedDrinkType) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = extractVisibleMainDrinks(store, requestedDrinkType);

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                LocalDate today = LocalDate.now();
                store.getOperationInfos().forEach(operationInfo -> {
                    if (operationInfo.getDayOfWeek().equals(today.minusDays(1).getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.plusDays(1).getDayOfWeek())) {
                        operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo));
                    }
                });
            }

            return SearchStoreDto.builder()
                    .id(store.getId())
                    .isCertified(store.getOwnerId() != null)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .contact(store.getContact())
                    .isAlwaysOpen(store.getIsAlwaysOpen())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .categories(store.getCategories())
                    .mainDrinkDtos(mainDrinkDtos)
                    .operationInfoDtos(operationInfoDtos)
                    .storeRate(storeRate)
                    .postCount(store.getPostCount())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SearchAllStoreDto {
        private Long id;
        private Boolean isCertified;
        private String mainImgUrl;
        private String name;
        private String contact;
        private Boolean isAlwaysOpen;
        private CommonStore.LocationDto locationDto;
        private Set<Category> categories;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;

        public static SearchAllStoreDto toDto(Store store) {

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                LocalDate today = LocalDate.now();
                store.getOperationInfos().forEach(operationInfo -> {
                    if (operationInfo.getDayOfWeek().equals(today.minusDays(1).getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.plusDays(1).getDayOfWeek())) {
                        operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo));
                    }
                });
            }

            return SearchAllStoreDto.builder()
                    .id(store.getId())
                    .isCertified(store.getOwnerId() != null)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .contact(store.getContact())
                    .isAlwaysOpen(store.getIsAlwaysOpen())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .categories(store.getCategories())
                    .operationInfoDtos(operationInfoDtos)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetByDistanceDto {
        private Long id;
        private Boolean isCertified;
        private String mainImgUrl;
        private String name;
        private Boolean isAlwaysOpen;
        private CommonStore.LocationDto locationDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
        private Double storeRate;
        private Long postCount;

        public static GetByDistanceDto toDto(Store store, Double storeRate) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = extractVisibleMainDrinks(store);

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                LocalDate today = LocalDate.now();
                store.getOperationInfos().forEach(operationInfo -> {
                    if (operationInfo.getDayOfWeek().equals(today.minusDays(1).getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.plusDays(1).getDayOfWeek())) {
                        operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo));
                    }
                });
            }

            return GetByDistanceDto.builder()
                    .id(store.getId())
                    .isCertified(store.getOwnerId() != null)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .isAlwaysOpen(store.getIsAlwaysOpen())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .categories(store.getCategories())
                    .storeRate(storeRate)
                    .postCount(store.getPostCount())
                    .mainDrinkDtos(mainDrinkDtos)
                    .operationInfoDtos(operationInfoDtos)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetByCategoryDto {
        private Long id;
        private Boolean isCertified;
        private String mainImgUrl;
        private String name;
        private Boolean isAlwaysOpen;
        private CommonStore.LocationDto locationDto;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
        private Double storeRate;
        private Long postCount;
        private Long storeLikeCount;
        private List<MenuListDto> menuList;

        public static GetByCategoryDto toDto(Store store, Double storeRate, DrinkType requestedDrinkType) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = extractVisibleMainDrinks(store, requestedDrinkType);

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                LocalDate today = LocalDate.now();
                store.getOperationInfos().forEach(operationInfo -> {
                    if (operationInfo.getDayOfWeek().equals(today.minusDays(1).getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.plusDays(1).getDayOfWeek())) {
                        operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo));
                    }
                });
            }

            List<MenuListDto> menuList = new ArrayList<>();
            if (!store.getMenuList().isEmpty()) {
                store.getMenuList().forEach(menu -> {
                    if (menu.getMenuType().equals(MenuType.MAIN)) {
                        menuList.add(MenuListDto.toDto(menu));
                    }
                });
            }

            return GetByCategoryDto.builder()
                    .id(store.getId())
                    .isCertified(store.getOwnerId() != null)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .isAlwaysOpen(store.getIsAlwaysOpen())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .mainDrinkDtos(mainDrinkDtos)
                    .operationInfoDtos(operationInfoDtos)
                    .storeRate(storeRate)
                    .postCount(store.getPostCount())
                    .storeLikeCount(store.getLikeCount())
                    .menuList(menuList)
                    .build();
        }
    }

    @Getter
    @Builder
    private static class MenuListDto {
        private String name;
        private String price;
        private String imgUrl;

        private static MenuListDto toDto(Menu menu) {
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
        private Boolean isCertified;
        private String mainImgUrl;
        private String name;
        private Boolean isAlwaysOpen;
        private CommonStore.LocationDto locationDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
        private Double storeRate;
        private Long postCount;

        public static GetByBusinessHoursDto toDto(Store store, Double storeRate) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = extractVisibleMainDrinks(store);

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                LocalDate today = LocalDate.now();
                store.getOperationInfos().forEach(operationInfo -> {
                    if (operationInfo.getDayOfWeek().equals(today.minusDays(1).getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.plusDays(1).getDayOfWeek())) {
                        operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo));
                    }
                });
            }

            return GetByBusinessHoursDto.builder()
                    .id(store.getId())
                    .isCertified(store.getOwnerId() != null)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .isAlwaysOpen(store.getIsAlwaysOpen())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .categories(store.getCategories())
                    .mainDrinkDtos(mainDrinkDtos)
                    .operationInfoDtos(operationInfoDtos)
                    .storeRate(storeRate)
                    .postCount(store.getPostCount())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetViewedStoreDto {
        private Long id;
        private Boolean isCertified;
        private String mainImgUrl;
        private String name;
        private CommonStore.LocationDto locationDto;
        private Set<Category> categories;

        public static GetViewedStoreDto toDto(Store store) {
            return GetViewedStoreDto.builder()
                    .id(store.getId())
                    .isCertified(store.getOwnerId() != null)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .categories(store.getCategories())
                    .build();
        }
    }

    @Deprecated
    @Getter
    @Builder
    public static class GetByRadiusDto {
        private Long id;
        private Boolean isCertified;
        private String mainImgUrl;
        private String name;
        private Boolean isAlwaysOpen;
        private CommonStore.LocationDto locationDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
        private Double storeRate;
        private Long postCount;

        public static GetByRadiusDto toDto(Store store, Double storeRate) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = new HashSet<>();
            if (!store.getMainDrinks().isEmpty()) {
                store.getMainDrinks().forEach(mainDrink -> mainDrinkDtos.add(CommonStore.MainDrinkDto.toDto(mainDrink)));
            }

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                LocalDate today = LocalDate.now();
                store.getOperationInfos().forEach(operationInfo -> {
                    if (operationInfo.getDayOfWeek().equals(today.minusDays(1).getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.plusDays(1).getDayOfWeek())) {
                        operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo));
                    }
                });
            }

            return GetByRadiusDto.builder()
                    .id(store.getId())
                    .isCertified(store.getOwnerId() != null)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .isAlwaysOpen(store.getIsAlwaysOpen())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .mainDrinkDtos(mainDrinkDtos)
                    .categories(store.getCategories())
                    .operationInfoDtos(operationInfoDtos)
                    .storeRate(storeRate)
                    .postCount(store.getPostCount())
                    .build();
        }
    }


    @Getter
    @Builder
    public static class GetPopularStoreDto {
        private Long id;
        private Boolean isCertified;
        private String mainImgUrl;
        private String name;
        private Boolean isAlwaysOpen;
        private CommonStore.LocationDto locationDto;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
        private Double storeRate;
        private Long postCount;
        private Long storeLikeCount;
        private List<MenuListDto> menuList;
        private Set<Category> categories;

        public static GetPopularStoreDto toDto(Store store, Double storeRate, DrinkType requestedDrinkType) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = extractVisibleMainDrinks(store, requestedDrinkType);

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                LocalDate today = LocalDate.now();
                store.getOperationInfos().forEach(operationInfo -> {
                    if (operationInfo.getDayOfWeek().equals(today.minusDays(1).getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.plusDays(1).getDayOfWeek())) {
                        operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo));
                    }
                });
            }

            List<MenuListDto> menuList = new ArrayList<>();
            if (!store.getMenuList().isEmpty()) {
                store.getMenuList().forEach(menu -> {
                    if (menu.getMenuType().equals(MenuType.MAIN)) {
                        menuList.add(MenuListDto.toDto(menu));
                    }
                });
            }

            return GetPopularStoreDto.builder()
                    .id(store.getId())
                    .isCertified(store.getOwnerId() != null)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .isAlwaysOpen(store.getIsAlwaysOpen())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .mainDrinkDtos(mainDrinkDtos)
                    .operationInfoDtos(operationInfoDtos)
                    .storeRate(storeRate)
                    .postCount(store.getPostCount())
                    .storeLikeCount(store.getLikeCount())
                    .menuList(menuList)
                    .categories(store.getCategories())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetByDrinkPriceDto {
        private Long id;
        private Boolean isCertified;
        private String mainImgUrl;
        private String name;
        private Boolean isAlwaysOpen;
        private CommonStore.LocationDto locationDto;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
        private Double storeRate;
        private Long postCount;
        private Long storeLikeCount;
        private List<MenuListDto> menuList;
        private Set<Category> categories;

        public static GetByDrinkPriceDto toDto(Store store, Double storeRate, DrinkType requestedDrinkType) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = extractVisibleMainDrinks(store, requestedDrinkType);

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                LocalDate today = LocalDate.now();
                store.getOperationInfos().forEach(operationInfo -> {
                    if (operationInfo.getDayOfWeek().equals(today.minusDays(1).getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.plusDays(1).getDayOfWeek())) {
                        operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo));
                    }
                });
            }

            List<MenuListDto> menuList = new ArrayList<>();
            if (!store.getMenuList().isEmpty()) {
                store.getMenuList().forEach(menu -> {
                    if (menu.getMenuType().equals(MenuType.MAIN)) {
                        menuList.add(MenuListDto.toDto(menu));
                    }
                });
            }

            return GetByDrinkPriceDto.builder()
                    .id(store.getId())
                    .isCertified(store.getOwnerId() != null)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .isAlwaysOpen(store.getIsAlwaysOpen())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .mainDrinkDtos(mainDrinkDtos)
                    .operationInfoDtos(operationInfoDtos)
                    .storeRate(storeRate)
                    .postCount(store.getPostCount())
                    .storeLikeCount(store.getLikeCount())
                    .menuList(menuList)
                    .categories(store.getCategories())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MapMarkerDto {
        private Long storeId;
        private String storeName;
        private Double latitude;
        private Double longitude;

        public static MapMarkerDto toDto(StoreMarkerProjection projection) {
            return MapMarkerDto.builder()
                    .storeId(projection.getStoreId())
                    .storeName(projection.getStoreName())
                    .latitude(projection.getLatitude())
                    .longitude(projection.getLongitude())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetByDrinkTypePriceDto {
        private Long id;
        private Set<Category> categories;
        private String mainImgUrl;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
        private DrinkType drinkType;
        private Long drinkPrice;

        public static GetByDrinkTypePriceDto toDto(Store store, DrinkType drinkType) {

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                store.getOperationInfos().forEach(operationInfo -> operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo)));
            }

            Long drinkPrice = store.getMainDrinks().stream()
                    .filter(mainDrink -> drinkType.equals(mainDrink.getType()))
                    .map(MainDrink::getPrice)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            return GetByDrinkTypePriceDto.builder()
                    .id(store.getId())
                    .categories(store.getCategories())
                    .mainImgUrl(store.getMainImgUrl())
                    .operationInfoDtos(operationInfoDtos)
                    .drinkType(drinkType)
                    .drinkPrice(drinkPrice)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class GetMarkerByDrinkTypesPriceDto {
        private Long id;
        private Double latitude;
        private Double longitude;
        private DrinkType drinkType;
        private Long drinkPrice;
    }

    @Getter
    @Builder
    public static class GetLikedStoreDto {
        private Long id;
        private Boolean isCertified;
        private String mainImgUrl;
        private String name;
        private Boolean isAlwaysOpen;
        private CommonStore.LocationDto locationDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
        private Double storeRate;
        private Long postCount;

        public static GetLikedStoreDto toDto(Store store, Double storeRate) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = extractVisibleMainDrinks(store);

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                LocalDate today = LocalDate.now();
                store.getOperationInfos().forEach(operationInfo -> {
                    if (operationInfo.getDayOfWeek().equals(today.minusDays(1).getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.plusDays(1).getDayOfWeek())) {
                        operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo));
                    }
                });
            }

            return GetLikedStoreDto.builder()
                    .id(store.getId())
                    .isCertified(store.getOwnerId() != null)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .isAlwaysOpen(store.getIsAlwaysOpen())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .categories(store.getCategories())
                    .mainDrinkDtos(mainDrinkDtos)
                    .operationInfoDtos(operationInfoDtos)
                    .storeRate(storeRate)
                    .postCount(store.getPostCount())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetRecentStoreDto {
        private Long id;
        private Boolean isCertified;
        private String mainImgUrl;
        private String name;
        private Boolean isAlwaysOpen;
        private Boolean isDeleted;
        private CommonStore.LocationDto locationDto;
        private Set<Category> categories;
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
        private Double storeRate;
        private Long postCount;

        public static GetRecentStoreDto toDto(Store store, Double storeRate) {

            Set<CommonStore.OperationInfoDto> operationInfoDtos = new HashSet<>();
            if (!store.getOperationInfos().isEmpty()) {
                LocalDate today = LocalDate.now();
                store.getOperationInfos().forEach(operationInfo -> {
                    if (operationInfo.getDayOfWeek().equals(today.minusDays(1).getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.getDayOfWeek())
                            || operationInfo.getDayOfWeek().equals(today.plusDays(1).getDayOfWeek())) {
                        operationInfoDtos.add(CommonStore.OperationInfoDto.toDto(operationInfo));
                    }
                });
            }

            return GetRecentStoreDto.builder()
                    .id(store.getId())
                    .isCertified(store.getOwnerId() != null)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .isAlwaysOpen(store.getIsAlwaysOpen())
                    .isDeleted(store.getIsDeleted())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .categories(store.getCategories())
                    .operationInfoDtos(operationInfoDtos)
                    .storeRate(storeRate)
                    .postCount(store.getPostCount())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetMainBannerDto {
        private Category category;
        private List<MainBannerStoreDto> mainBannerStoreDtoList;

        public static GetMainBannerDto toDto(Category category, List<Store> storeList) {

            List<MainBannerStoreDto> storeDtoList = new ArrayList<>();
            if (!storeList.isEmpty()) {
                storeList.forEach(store -> {
                    storeDtoList.add(MainBannerStoreDto.toDto(store));
                });
            }

            return GetMainBannerDto.builder()
                    .category(category)
                    .mainBannerStoreDtoList(storeDtoList)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MainBannerStoreDto {
        private Long id;
        private Boolean isCertified;
        private String mainImgUrl;
        private String name;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;

        public static MainBannerStoreDto toDto(Store store) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = extractVisibleMainDrinks(store);

            return MainBannerStoreDto.builder()
                    .id(store.getId())

                    .isCertified(store.getOwnerId() != null)
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .mainDrinkDtos(mainDrinkDtos)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StoreSuggestionListDto {
        private Long storeSuggestionId;
        private LocalDateTime suggestedAt;
        private Long storeId;
        private String storeName;
        private CommonStore.LocationDto locationDto;
        private Long suggestedUserId;
        private String suggestedUserNickname;

        public static StoreSuggestionListDto toDto(StoreSuggestion storeSuggestion) {
            Auth suggestedUser = AuthResolver.resolveUser(storeSuggestion.getSuggesterId());
            return StoreSuggestionListDto.builder()
                    .storeSuggestionId(storeSuggestion.getId())
                    .suggestedAt(storeSuggestion.getSuggestedAt())
                    .storeId(storeSuggestion.getStore().getId())
                    .storeName(storeSuggestion.getStore().getName())
                    .locationDto(CommonStore.LocationDto.toDto(storeSuggestion.getStore().getLocation()))
                    .suggestedUserId(suggestedUser.getId())
                    .suggestedUserNickname(suggestedUser.getNickname())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OwnershipListDto {
        private Long id;
        private LocalDateTime requestedAt;
        private Boolean isVerifiedStore;
        private Long storeId;
        private String originalStoreName;
        private String receivedStoreName;
        private String requesterNickname;

        public static OwnershipListDto toDto(OwnershipRequest ownershipRequest) {
            return OwnershipListDto.builder()
                    .id(ownershipRequest.getId())
                    .requestedAt(ownershipRequest.getRequestedAt())
                    .isVerifiedStore(ownershipRequest.getStore().getOwnerId() != null)
                    .storeId(ownershipRequest.getStore().getId())
                    .originalStoreName(ownershipRequest.getStore().getName())
                    .receivedStoreName(ownershipRequest.getReceivedStoreName())
                    .requesterNickname(AuthResolver.resolveUser(ownershipRequest.getRequesterId()).getNickname())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetOwnershipListWithStoreSuggestionDto {
        private StoreSuggestionListDto storeSuggestionListDto;
        private OwnershipListDto ownershipListDto;
    }

    @Getter
    @Builder
    public static class OwnershipDto {
        private Long id;
        private LocalDateTime requestedAt;
        private Boolean isVerifiedStore;
        private Long storeId;
        private String originalStoreName;
        private String receivedStoreName;
        private String requesterNickname;
        private String requesterName;
        private String originalStoreContact;
        private String receivedStoreContact;
        private String receivedUserContact;
        private String businessRegistrationNumber;
        private List<RepresentativeInfo> representativeInfoList;
        private String openingDate;
        private String liquorReportNumber;
        private Location businessLocation;
        private String businessRegistrationCertificateImgUrl;
        private List<OwnershipListDto> ownershipList;

        public static OwnershipDto toDto(OwnershipRequest ownershipRequest, List<OwnershipListDto> ownershipListDto, AESUtil aesUtil) {
            Auth requester = AuthResolver.resolveUser(ownershipRequest.getRequesterId());
            return OwnershipDto.builder()
                    .id(ownershipRequest.getId())
                    .requestedAt(ownershipRequest.getRequestedAt())
                    .isVerifiedStore(ownershipRequest.getStore().getOwnerId() != null)
                    .storeId(ownershipRequest.getStore().getId())
                    .originalStoreName(ownershipRequest.getStore().getName())
                    .receivedStoreName(ownershipRequest.getReceivedStoreName())
                    .requesterNickname(requester.getNickname())
                    .requesterName(aesUtil.decrypt(requester.getPersonalInfo().getName()))
                    .originalStoreContact(ownershipRequest.getStore().getContact())
                    .receivedStoreContact(ownershipRequest.getReceivedStoreContact())
                    .receivedUserContact(ownershipRequest.getReceivedUserContact())
                    .businessRegistrationNumber(ownershipRequest.getBusinessRegistrationNumber())
                    .representativeInfoList(ownershipRequest.getRepresentativeInfoList())
                    .openingDate(ownershipRequest.getOpeningDate())
                    .liquorReportNumber(ownershipRequest.getLiquorReportNumber())
                    .businessLocation(ownershipRequest.getBusinessLocation())
                    .businessRegistrationCertificateImgUrl(ownershipRequest.getBusinessRegistrationCertificateImgUrl())
                    .ownershipList(ownershipListDto)
                    .build();
        }

    }

    @Getter
    @Builder
    public static class ManagedStoreListDto {
        private Long storeId;
        private String mainImgUrl;
        private String storeName;
        private LocalDateTime approvedAt;
        private Set<Category> categories;
        private Double storeRate;
        private Long postCount;
        private Long likeCount;
        private CommonStore.LocationDto locationDto;
        private Long normalReportCount;

        public static ManagedStoreListDto toDto(StoreManager storeManager, Store store, Double storeRate, Long normalReportCount) {
            return ManagedStoreListDto.builder()
                    .storeId(store.getId())
                    .storeName(store.getName())
                    .mainImgUrl(store.getMainImgUrl())
                    .approvedAt(storeManager.getApprovedAt())
                    .categories(store.getCategories())
                    .storeRate(storeRate)
                    .postCount(store.getPostCount())
                    .likeCount(store.getLikeCount())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .normalReportCount(normalReportCount)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class LikedStoreStatDto {
        private String ageGroup;
        private Gender gender;
        private long count;
    }

    @Getter
    @Builder
    public static class ShareStoreDto {
        private String shareLink;

        public static ShareStoreDto toDto(String shareLink) {
            return ShareStoreDto.builder()
                    .shareLink(shareLink)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class InviteStoreManagerDto {
        private String inviteLink;

        public static InviteStoreManagerDto toDto(String inviteLink) {
            return InviteStoreManagerDto.builder()
                    .inviteLink(inviteLink)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StoreManagerListDto {
        private Long storeManagerId;
        private Long userId;
        private LocalDateTime approvedAt;
        private Boolean isOwner;
        private String nickname;
        private String profileImgUrl;

        public static StoreManagerListDto toDto(StoreManager storeManager, Long ownerId, String nickname, String profileImgUrl) {
            return StoreManagerListDto.builder()
                    .storeManagerId(storeManager.getId())
                    .userId(storeManager.getUserId())
                    .approvedAt(storeManager.getApprovedAt())
                    .isOwner(Objects.equals(ownerId, storeManager.getUserId()))
                    .nickname(nickname)
                    .profileImgUrl(profileImgUrl)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ManagerInvitationPreviewDto {
        private String storeName;
        private String storeImage;

        public static ManagerInvitationPreviewDto toDto(ManagerInvitation invitation) {
            return ManagerInvitationPreviewDto.builder()
                    .storeName(invitation.getStoreName())
                    .storeImage(invitation.getStoreImage())
                    .build();
        }
    }

    private static Set<CommonStore.MainDrinkDto> extractVisibleMainDrinks(Store store) {
        return extractVisibleMainDrinks(store, null);
    }

    private static Set<CommonStore.MainDrinkDto> extractVisibleMainDrinks(Store store, DrinkType forcedDrinkType) {
        Set<CommonStore.MainDrinkDto> mainDrinkDtos = new HashSet<>();
        if (store.getMainDrinks() == null || store.getMainDrinks().isEmpty()) {
            return mainDrinkDtos;
        }
        store.getMainDrinks().stream()
                .filter(MainDrink::isVisible)
                .forEach(mainDrink -> mainDrinkDtos.add(CommonStore.MainDrinkDto.toDto(mainDrink)));
        if (forcedDrinkType != null) {
            boolean alreadyIncluded = mainDrinkDtos.stream()
                    .anyMatch(mainDrinkDto -> forcedDrinkType.equals(mainDrinkDto.getType()));
            if (!alreadyIncluded) {
                store.getMainDrinks().stream()
                        .filter(mainDrink -> forcedDrinkType.equals(mainDrink.getType()))
                        .findFirst()
                        .ifPresent(mainDrink -> mainDrinkDtos.add(CommonStore.MainDrinkDto.toDto(mainDrink)));
            }
        }
        return mainDrinkDtos;
    }
}
