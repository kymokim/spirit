package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.dto.StoreSearchCriteria;
import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.QStore;
import com.kymokim.spirit.store.entity.Store;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class StoreRepositoryCustomImpl implements StoreRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    private NumberExpression<Double> distance;
    private QStore store;

    // Haversine 거리 계산
    public NumberExpression<Double> calculateHaversine(StoreSearchCriteria criteria, QStore store) {
        return Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({4})) * sin(radians({5})))",
                criteria.getLatitude(), store.location.latitude, store.location.longitude, criteria.getLongitude(), criteria.getLatitude(), store.location.latitude);
    }

    // 반경 내 가게 조회 쿼리
    public JPQLQuery<Store> filterByRadius(StoreSearchCriteria criteria){
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        store = QStore.store;
        distance = calculateHaversine(criteria, store);
        return queryFactory.selectFrom(store).where(distance.loe(criteria.getRadius()));
    }

    // 가까운 순 가게 리스트 반환
    @Override
    public List<Store> findStoresOrderByDistance(StoreSearchCriteria criteria){
        JPQLQuery<Store> storeJPQLQuery = filterByRadius(criteria);
        return storeJPQLQuery.orderBy(distance.asc()).fetch();
    }

    // 카테고리 해당하는 가게 리스트 반환
    @Override
    public List<Store> findStoresByCategory(StoreSearchCriteria criteria, String category) {
        JPQLQuery<Store> storeJPQLQuery = filterByRadius(criteria);
        return storeJPQLQuery.where(store.categories.contains(Category.valueOf(category))).fetch();
    }

    @Override
    public List<Store> findStoresByName(StoreSearchCriteria criteria, String searchKeyword) {
        JPQLQuery<Store> query = filterByRadius(criteria);
        // 입력값에서 공백 제거
        String formattedSearchKeyword = searchKeyword.replaceAll("\\s+", "");
        // 데이터베이스 값 공백 제거 후 검색
        return query.where(Expressions.stringTemplate(
                        "REPLACE({0}, ' ', '')", store.name
                ).containsIgnoreCase(formattedSearchKeyword)
        ).fetch();
    }

    // 근처 가게 리스트 반환
    @Override
    public List<Store> findNearByStores(StoreSearchCriteria criteria){
        return filterByRadius(criteria).fetch();
    }
}