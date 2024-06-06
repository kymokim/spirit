package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.dto.StoreSearchCriteria;
import com.kymokim.spirit.store.entity.QStore;
import com.kymokim.spirit.store.entity.Store;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class StoreRepositoryCustomImpl implements StoreRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    NumberExpression<Double> distance;

    public JPQLQuery<Store> findStores(StoreSearchCriteria criteria) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;

        distance = Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({4})) * sin(radians({5})))",
                criteria.getLatitude(), store.latitude, store.longitude, criteria.getLongitude(), criteria.getLatitude(), store.latitude);

        JPQLQuery<Store> query = queryFactory.selectFrom(store)
                .where(distance.loe(criteria.getRadius()));

        return query;
    }

    @Override
    public List<Store> findStoresByCategory(StoreSearchCriteria criteria, String category) {

        JPQLQuery<Store> query = findStores(criteria);
        QStore store = QStore.store;

        if (category != null && !category.isEmpty()) {
            query.where(store.firstCategory.eq(category).or(store.secondCategory.eq(category).or(store.thirdCategory.eq(category))));
        }

        return query.fetch();
    }

    @Override
    public List<Store> findStoresByDistance(StoreSearchCriteria criteria){
        JPQLQuery<Store> query = findStores(criteria);
        QStore store = QStore.store;

        query.orderBy(distance.asc()).select(store, distance.as("distance"));
        return query.fetch();
    }

}