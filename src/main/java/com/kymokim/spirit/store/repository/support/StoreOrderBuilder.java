package com.kymokim.spirit.store.repository.support;

import com.kymokim.spirit.log.entity.QStoreViewLog;
import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.entity.QMainDrink;
import com.kymokim.spirit.store.entity.QOperationInfo;
import com.kymokim.spirit.store.entity.QStore;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.TimePath;
import org.springframework.data.domain.Sort;

import java.time.LocalTime;

import static com.kymokim.spirit.store.repository.support.StoreExpressions.*;
import static com.kymokim.spirit.store.repository.support.StorePredicates.openNow;

public final class StoreOrderBuilder {

    private StoreOrderBuilder() {
    }

    public static OrderSpecifier<Integer> byIsOpen(QStore store, QOperationInfo operationInfo) {
        return new CaseBuilder().when(openNow(store, operationInfo)).then(1).otherwise(0).desc();
    }

    public static OrderSpecifier<Long> byLikeCount(QStore store) {
        return store.likeCount.desc();
    }

    public static OrderSpecifier<Integer> byAlwaysOpen(QStore store) {
        NumberExpression<Integer> alwaysOpenPriority = new CaseBuilder()
                .when(store.isAlwaysOpen.isTrue())
                .then(1)
                .otherwise(0);
        return alwaysOpenPriority.desc();
    }

    public static OrderSpecifier<Integer> byCloseTime(QOperationInfo operationInfo) {
        TimePath<LocalTime> openTime = operationInfo.openTime;
        TimePath<LocalTime> closeTime = operationInfo.closeTime;

        NumberExpression<Integer> adjustedCloseTime = new CaseBuilder()
                .when(openTime.after(closeTime))
                .then(closeTime.hour().add(24).multiply(60).add(closeTime.minute()))
                .otherwise(closeTime.hour().multiply(60).add(closeTime.minute()));

        return adjustedCloseTime.desc();
    }

    public static OrderSpecifier<Double> byDistance(QStore store, LocationCriteria criteria) {
        return distance(store, criteria).asc();
    }

    public static OrderSpecifier<Integer> byIsCertified(QStore store) {
        NumberExpression<Integer> isCertifiedPriority = new CaseBuilder()
                .when(store.ownerId.isNotNull())
                .then(1)
                .otherwise(0);
        return isCertifiedPriority.desc();
    }

    public static OrderSpecifier<?> byPriceOrder(QMainDrink mainDrink, Sort.Direction direction) {
        return direction.isAscending()
                ? mainDrink.price.asc().nullsLast()
                : mainDrink.price.desc().nullsLast();
    }

    public static OrderSpecifier<Double> byWeightedDistanceAndRate(QStore store, LocationCriteria criteria, double globalAverageRate) {
        double distanceWeight = 0.1;
        double rateWeight = 0.9;
        NumberExpression<Double> rateScore = bayesianAverageRate(store, globalAverageRate).multiply(rateWeight);
        if (criteria == null) {
            return rateScore.desc();
        }

        NumberExpression<Double> distanceScore = com.querydsl.core.types.dsl.Expressions.numberTemplate(Double.class,
                "1 / (1 + {0})", distance(store, criteria)).multiply(distanceWeight);

        NumberExpression<Double> weightedScore = distanceScore.add(rateScore);
        return weightedScore.desc();
    }

    public static OrderSpecifier<Double> byPopularScore(QStore store, QStoreViewLog storeViewLog, Double globalAverageRate) {
        double weightView = 0.2;
        double weightLike = 0.3;
        double weightRate = 0.5;
        NumberExpression<Double> score = com.querydsl.core.types.dsl.Expressions.numberTemplate(Double.class,
                "({0}*LOG(1+{1}) + {2}*{3} + {4}*{5})",
                weightView, viewScore(store, storeViewLog),
                weightLike, likeScore(store),
                weightRate, bayesianAverageRate(store, globalAverageRate));
        return score.desc();
    }
}
