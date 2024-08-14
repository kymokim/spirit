package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.dto.StoreSearchCriteria;
import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.QStore;
import com.kymokim.spirit.store.entity.Store;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Set;

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
        System.out.println(category);
        JPQLQuery<Store> query = findStores(criteria);
        QStore store = QStore.store;

        if (category != null) {
            query.where(store.categories.contains(Category.valueOf(category)));
        }

        return query.fetch();
    }

    @Override
    public List<Store> findStoresByDistance(StoreSearchCriteria criteria){
        JPQLQuery<Store> query = findStores(criteria);
        QStore store = QStore.store;

        query.orderBy(distance.asc());
        return query.fetch();
    }

}