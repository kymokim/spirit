package com.kymokim.spirit.store.repository.support;

import com.kymokim.spirit.log.entity.QStoreViewLog;
import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.entity.QStore;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;

import java.time.LocalDate;

public final class StoreExpressions {

    private StoreExpressions() {
    }

    public static NumberExpression<Double> distance(QStore store, LocationCriteria criteria) {
        return Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({4})) * sin(radians({5})))",
                criteria.getLatitude(), store.location.latitude, store.location.longitude, criteria.getLongitude(), criteria.getLatitude(), store.location.latitude);
    }

    public static NumberExpression<Double> viewScore(QStore store, QStoreViewLog storeViewLog) {
        int nonDecayDate = 6;
        double decayFactor = 0.85;
        int dateRange = 30;

        NumberExpression<Integer> dayDiff = Expressions.numberTemplate(Integer.class,
                "datediff(current_date, {0})", storeViewLog.viewDate);

        NumberExpression<Double> weight = new CaseBuilder()
                .when(dayDiff.loe(nonDecayDate)).then(1.0)
                .otherwise(Expressions.numberTemplate(Double.class,
                        "power({0}, {1})", decayFactor, dayDiff.subtract(nonDecayDate)));

        Expression<Double> subQuery = JPAExpressions
                .select(weight.sum().coalesce(0.0))
                .from(storeViewLog)
                .where(storeViewLog.storeId.eq(store.id)
                        .and(storeViewLog.viewDate.goe(LocalDate.now().minusDays(dateRange))));

        return Expressions.numberTemplate(Double.class, "({0})", subQuery);
    }

    public static NumberExpression<Double> likeScore(QStore store) {
        return Expressions.numberTemplate(Double.class, "LOG(1 + {0})", store.likeCount);
    }

    public static NumberExpression<Double> averageRate(QStore store) {
        return Expressions.numberTemplate(Double.class,
                "COALESCE({0} / NULLIF({1}, 0), 0.0)", store.totalRate, store.reviewCount);
    }

    public static NumberExpression<Double> globalAverageRateExpression(QStore store) {
        return Expressions.numberTemplate(
                Double.class,
                "COALESCE(SUM({0})/NULLIF(SUM({1}),0), {2})",
                store.totalRate, store.reviewCount, 3.5);
    }

    public static NumberExpression<Double> bayesianAverageRate(QStore store, double globalAverageRate) {
        NumberExpression<Double> averageRate = averageRate(store);
        int priorSampleWeight = 5;
        return Expressions.numberTemplate(Double.class,
                "((({0} / ({0} + {1})) * {2}) + (({1} / ({0} + {1})) * {3}))",
                store.reviewCount, priorSampleWeight, averageRate, Expressions.constant(globalAverageRate));
    }
}
