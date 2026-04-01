package com.kymokim.spirit.store.repository.support;

import com.kymokim.spirit.store.dto.FacilitiesCondition;
import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.drink.entity.DrinkType;
import com.kymokim.spirit.store.entity.Mood;
import com.kymokim.spirit.menu.entity.QMenu;
import com.kymokim.spirit.store.entity.QOperationInfo;
import com.kymokim.spirit.store.entity.QStore;
import com.kymokim.spirit.store.entity.QMainDrink;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.TimePath;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static com.kymokim.spirit.store.repository.support.StoreExpressions.distance;

public final class StorePredicates {

    private StorePredicates() {
    }

    public static BooleanExpression isActiveStore(QStore store) {
        return store.isDeleted.eq(false);
    }

    public static BooleanExpression withinRadius(QStore store, LocationCriteria criteria) {
        double lat = criteria.getLatitude();
        double lon = criteria.getLongitude();
        double radius = criteria.getRadius();

        double latDelta = radius / 111.0;
        double lonDelta = radius / (111.0 * Math.cos(Math.toRadians(lat)));

        BooleanExpression latBound = store.location.latitude.between(lat - latDelta, lat + latDelta);
        BooleanExpression lonBound = store.location.longitude.between(lon - lonDelta, lon + lonDelta);

        return latBound.and(lonBound).and(distance(store, criteria).loe(radius));
    }

    public static BooleanExpression openNow(QStore store, QOperationInfo operationInfo) {
        return openAt(store, operationInfo, LocalDateTime.now());
    }

    public static BooleanExpression openAt(QStore store, QOperationInfo operationInfo, LocalDateTime conditionTime) {
        BooleanExpression isAlwaysOpen = store.isAlwaysOpen.isTrue();

        LocalTime currentTime = conditionTime.toLocalTime();
        DayOfWeek today = currentTime.isBefore(LocalTime.of(9, 0))
                ? conditionTime.minusDays(1).getDayOfWeek()
                : conditionTime.getDayOfWeek();

        TimePath<LocalTime> openTime = operationInfo.openTime;
        TimePath<LocalTime> closeTime = operationInfo.closeTime;

        BooleanExpression matchDay = operationInfo.dayOfWeek.eq(today);
        BooleanExpression notClosedToday = operationInfo.isClosed.isFalse();
        BooleanExpression openDuringDay = openTime.before(currentTime)
                .and(closeTime.after(currentTime));
        BooleanExpression openOverMidnight = openTime.after(closeTime)
                .and(openTime.before(currentTime)
                        .or(closeTime.after(currentTime)));

        return isAlwaysOpen.or(matchDay
                .and(notClosedToday)
                .and(openDuringDay.or(openOverMidnight))
        );
    }

    public static BooleanExpression storeNameContains(QStore store, String searchKeyword) {
        String formatted = formatKeyword(searchKeyword);
        return Expressions.stringTemplate("REPLACE({0}, ' ', '')", store.name)
                .containsIgnoreCase(formatted);
    }

    public static BooleanExpression menuNameContains(QMenu menu, String searchKeyword) {
        String formatted = formatKeyword(searchKeyword);
        return Expressions.stringTemplate("REPLACE({0}, ' ', '')", menu.name)
                .containsIgnoreCase(formatted);
    }

    public static BooleanExpression categoryEquals(QStore store, String category) {
        return store.categories.contains(Category.valueOf(category));
    }

    public static BooleanExpression categoriesIn(QStore store, Set<Category> categories) {
        return store.categories.any().in(categories);
    }

    public static BooleanExpression drinkTypeEquals(QMainDrink mainDrink, DrinkType drinkType) {
        return mainDrink.type.eq(drinkType);
    }

    public static BooleanExpression moodsIn(QStore store, Set<Mood> moods) {
        return store.moods.any().in(moods);
    }

    public static BooleanExpression hasMainImage(QStore store) {
        return store.mainImgUrl.isNotNull().and(store.mainImgUrl.isNotEmpty());
    }

    public static void applyFacilities(BooleanBuilder conditions, QStore store, FacilitiesCondition facilitiesCondition) {
        if (facilitiesCondition.getHasScreen() != null) {
            conditions.and(store.facilitiesInfo.hasScreen.eq(facilitiesCondition.getHasScreen()));
        }
        if (facilitiesCondition.getHasRoom() != null) {
            conditions.and(store.facilitiesInfo.hasRoom.eq(facilitiesCondition.getHasRoom()));
        }
        if (facilitiesCondition.getHasOutdoor() != null) {
            conditions.and(store.facilitiesInfo.hasOutdoor.eq(facilitiesCondition.getHasOutdoor()));
        }
        if (facilitiesCondition.getIsGroupAvailable() != null) {
            conditions.and(store.facilitiesInfo.isGroupAvailable.eq(facilitiesCondition.getIsGroupAvailable()));
        }
        if (facilitiesCondition.getIsParkingAvailable() != null) {
            conditions.and(store.facilitiesInfo.isParkingAvailable.eq(facilitiesCondition.getIsParkingAvailable()));
        }
        if (facilitiesCondition.getIsCorkageAvailable() != null) {
            conditions.and(store.facilitiesInfo.isCorkageAvailable.eq(facilitiesCondition.getIsCorkageAvailable()));
        }
    }

    private static String formatKeyword(String searchKeyword) {
        return searchKeyword.trim().replaceAll("\\s+", "");
    }
}
