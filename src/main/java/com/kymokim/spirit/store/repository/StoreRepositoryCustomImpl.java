package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.dto.StoreSearchCriteria;
import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.QStore;
import com.kymokim.spirit.store.entity.Store;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

public class StoreRepositoryCustomImpl implements StoreRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    NumberExpression<Double> distance;

    public NumberExpression<Double> calculateHaversine(StoreSearchCriteria criteria, QStore store) {

        return Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({4})) * sin(radians({5})))",
                criteria.getLatitude(), store.latitude, store.longitude, criteria.getLongitude(), criteria.getLatitude(), store.latitude);
    }

    @Override
    public List<Store> findStoresByCategory(StoreSearchCriteria criteria, String category) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        distance = calculateHaversine(criteria, store);

        JPQLQuery<Store> query = queryFactory.selectFrom(store)
                .where(distance.loe(criteria.getRadius()));

        if (category != null) {
            query.where(store.categories.contains(Category.valueOf(category)));
        }

        return query.fetch();
    }

    @Override
    public List<AbstractMap.SimpleEntry<Store,Double>> findStoresByDistance(StoreSearchCriteria criteria){
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        distance = calculateHaversine(criteria, store);

        JPQLQuery<Tuple> query = queryFactory.select(store, distance)
                .from(store)
                .where(distance.loe(criteria.getRadius()));

        return query.orderBy(distance.asc())
                .fetch()
                .stream()
                .map(tuple -> new AbstractMap.SimpleEntry<>(
                        (Store) tuple.get(0, Store.class),
                        (Double) tuple.get(1, Double.class)))
                .collect(Collectors.toList());
    }
}