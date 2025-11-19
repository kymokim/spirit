package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.drink.entity.DrinkType;
import com.kymokim.spirit.log.entity.QStoreViewLog;
import com.kymokim.spirit.menu.entity.QMenu;
import com.kymokim.spirit.store.dto.FacilitiesCondition;
import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.dto.QueryStore;
import com.kymokim.spirit.store.entity.*;
import com.kymokim.spirit.store.repository.dto.StoreMarkerProjection;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.kymokim.spirit.store.repository.support.StoreExpressions.globalAverageRateExpression;
import static com.kymokim.spirit.store.repository.support.StoreOrderBuilder.*;
import static com.kymokim.spirit.store.repository.support.StorePredicates.*;

public class StoreRepositoryCustomImpl implements StoreRepositoryCustom {

    @PersistenceContext(unitName = "main")
    private EntityManager entityManager;

    private BooleanBuilder baseActiveRadiusCondition(QStore store, LocationCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(isActiveStore(store));
        builder.and(withinRadius(store, criteria));
        return builder;
    }

    private long fetchTotal(JPQLQuery<Long> countQuery, BooleanBuilder conditions) {
        Long total = countQuery.where(conditions).fetchOne();
        return total == null ? 0L : total;
    }

    // 검색어가 가게명, 메뉴명에 포함되는 가게 리스트 반환
    @Override
    public Page<Store> findByNameAndMenu(LocationCriteria criteria, String searchKeyword, Pageable pageable) {
        // query
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QMenu menu = QMenu.menu;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        // selectFrom
        JPQLQuery<Store> query = queryFactory.selectFrom(store);
        JPQLQuery<Long> countQuery = queryFactory.select(store.id.countDistinct()).from(store);

        // where
        BooleanBuilder conditions = baseActiveRadiusCondition(store, criteria);
        query.leftJoin(store.menuList, menu);
        countQuery.leftJoin(store.menuList, menu);
        conditions.and(storeNameContains(store, searchKeyword)
                .or(menuNameContains(menu, searchKeyword)));

        // orderBy
        query.leftJoin(store.operationInfos, operationInfo);
        query.orderBy(byIsOpen(store, operationInfo),
                byIsCertified(store),
                byLikeCount(store));

        // fetch
        List<Store> content = query
                .where(conditions)
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = fetchTotal(countQuery, conditions);
        return new PageImpl<>(content, pageable, total);
    }

    // 검색어가 가게명에 포함되는 가게 리스트 반환
    @Override
    public Page<Store> findByName(String searchKeyword, Pageable pageable) {
        // query
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;

        // selectFrom
        JPQLQuery<Store> query = queryFactory.selectFrom(store);
        JPQLQuery<Long> countQuery = queryFactory.select(store.id.count()).from(store);

        // where
        BooleanBuilder conditions = new BooleanBuilder();
        conditions.and(isActiveStore(store));
        conditions.and(storeNameContains(store, searchKeyword));

        // fetch
        List<Store> content = query
                .where(conditions)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = fetchTotal(countQuery, conditions);
        return new PageImpl<>(content, pageable, total);
    }

    // 가까운 순 가게 리스트 반환
    @Override
    public Page<Store> findByDistance(LocationCriteria criteria, Pageable pageable) {
        // query
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        // selectFrom
        JPQLQuery<Store> query = queryFactory.selectFrom(store);
        JPQLQuery<Long> countQuery = queryFactory.select(store.id.countDistinct()).from(store);

        // where
        BooleanBuilder conditions = baseActiveRadiusCondition(store, criteria);

        // orderBy
        query.leftJoin(store.operationInfos, operationInfo);
        query.orderBy(byIsOpen(store, operationInfo),
                byDistance(store, criteria));

        // fetch
        List<Store> content = query
                .where(conditions)
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = fetchTotal(countQuery, conditions);
        return new PageImpl<>(content, pageable, total);
    }

    // 카테고리에 해당하는 가게 리스트 반환 + 동적 주종 필터
    @Override
    public Page<Store> findByCategory(LocationCriteria criteria, String category, DrinkType drinkType, Sort.Direction priceOrder, Pageable pageable) {
        // query
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;
        QMainDrink mainDrink = QMainDrink.mainDrink;

        // selectFrom
        JPQLQuery<Store> query = queryFactory.selectFrom(store);
        JPQLQuery<Long> countQuery = queryFactory.select(store.id.countDistinct()).from(store);

        // where
        BooleanBuilder conditions = baseActiveRadiusCondition(store, criteria);
        conditions.and(categoryEquals(store, category));
        if (drinkType != null) {
            query.leftJoin(store.mainDrinks, mainDrink);
            countQuery.leftJoin(store.mainDrinks, mainDrink);
            conditions.and(drinkTypeEquals(mainDrink, drinkType));
        }

        // orderBy
        if (drinkType != null && priceOrder != null) {
            query.orderBy(byPriceOrder(mainDrink, priceOrder));
        }
        query.leftJoin(store.operationInfos, operationInfo);
        query.orderBy(byIsOpen(store, operationInfo),
                byIsCertified(store),
                byLikeCount(store));

        //fetch
        List<Store> content = query
                .where(conditions)
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = fetchTotal(countQuery, conditions);
        return new PageImpl<>(content, pageable, total);
    }

    // 현재 영업중인 가게중, 영업시간 늦는 순서 가게 리스트 반환
    @Override
    public Page<Store> findByBusinessHours(LocationCriteria criteria, Pageable pageable) {
        // query
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        // selectFrom
        JPQLQuery<Store> query = queryFactory.selectFrom(store);
        JPQLQuery<Long> countQuery = queryFactory.select(store.id.countDistinct()).from(store);

        // where
        BooleanBuilder conditions = baseActiveRadiusCondition(store, criteria);
        query.leftJoin(store.operationInfos, operationInfo);
        countQuery.leftJoin(store.operationInfos, operationInfo);
        conditions.and(openNow(store, operationInfo));

        // orderBy
        query.orderBy(byAlwaysOpen(store),
                byCloseTime(operationInfo),
                byIsCertified(store),
                byLikeCount(store));

        // fetch
        List<Store> content = query
                .where(conditions)
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = fetchTotal(countQuery, conditions);
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Store> findByMultipleCondition(LocationCriteria criteria, String category, String searchKeyword, FacilitiesCondition facilitiesCondition,
                                               LocalDateTime conditionTime, DrinkType drinkType, Set<Mood> moods, Sort.Direction priceOrder, Pageable pageable) {
        // query
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;
        QMenu menu = QMenu.menu;
        QMainDrink mainDrink = QMainDrink.mainDrink;

        // selectFrom
        JPQLQuery<Store> query = queryFactory.selectFrom(store);
        JPQLQuery<Long> countQuery = queryFactory.select(store.id.countDistinct()).from(store);

        // where
        BooleanBuilder conditions = baseActiveRadiusCondition(store, criteria);
        if (category != null) {
            conditions.and(categoryEquals(store, category));
        }
        if (searchKeyword != null && !searchKeyword.isBlank()) {
            query.leftJoin(store.menuList, menu);
            countQuery.leftJoin(store.menuList, menu);
            conditions.and(storeNameContains(store, searchKeyword)
                    .or(menuNameContains(menu, searchKeyword)));
        }
        if (facilitiesCondition != null) {
            applyFacilities(conditions, store, facilitiesCondition);
        }
        if (conditionTime != null) {
            query.leftJoin(store.operationInfos, operationInfo);
            countQuery.leftJoin(store.operationInfos, operationInfo);
            conditions.and(openAt(store, operationInfo, conditionTime));
        }
        if (drinkType != null) {
            query.leftJoin(store.mainDrinks, mainDrink);
            countQuery.leftJoin(store.mainDrinks, mainDrink);
            conditions.and(drinkTypeEquals(mainDrink, drinkType));
        }
        if (moods != null && !moods.isEmpty()) {
            conditions.and(moodsIn(store, moods));
        }

        // orderBy
        if (drinkType != null && priceOrder != null) {
            query.orderBy(byPriceOrder(mainDrink, priceOrder));
        }
        double globalAverageRate = fetchGlobalAverageRate(queryFactory);
        query.orderBy(byIsCertified(store),
                byWeightedDistanceAndRate(store, criteria, globalAverageRate));

        // fetch
        List<Store> content = query
                .where(conditions)
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = fetchTotal(countQuery, conditions);
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<StoreMarkerProjection> findMarkersByMultipleCondition(LocationCriteria criteria, String category, String searchKeyword, FacilitiesCondition facilitiesCondition,
                                                                      LocalDateTime conditionTime, DrinkType drinkType, Set<Mood> moods) {
        // query
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;
        QMenu menu = QMenu.menu;
        QMainDrink mainDrink = QMainDrink.mainDrink;

        // selectFrom
        JPQLQuery<StoreMarkerProjection> query = queryFactory
                .select(Projections.constructor(
                        StoreMarkerProjection.class,
                        store.id,
                        store.location.latitude,
                        store.location.longitude,
                        store.name
                ))
                .from(store);

        // where
        BooleanBuilder conditions = baseActiveRadiusCondition(store, criteria);
        if (category != null) {
            conditions.and(categoryEquals(store, category));
        }
        if (searchKeyword != null && !searchKeyword.isBlank()) {
            query.leftJoin(store.menuList, menu);
            conditions.and(storeNameContains(store, searchKeyword)
                    .or(menuNameContains(menu, searchKeyword)));
        }
        if (facilitiesCondition != null) {
            applyFacilities(conditions, store, facilitiesCondition);
        }
        if (conditionTime != null) {
            query.leftJoin(store.operationInfos, operationInfo);
            conditions.and(openAt(store, operationInfo, conditionTime));
        }
        if (drinkType != null) {
            query.leftJoin(store.mainDrinks, mainDrink);
            conditions.and(drinkTypeEquals(mainDrink, drinkType));
        }
        if (moods != null && !moods.isEmpty()) {
            conditions.and(moodsIn(store, moods));
        }

        // fetch
        List<StoreMarkerProjection> markerProjectionList = query
                .where(conditions)
                .distinct()
                .fetch();
        return markerProjectionList;
    }

    // 랜덤 카테고리 선정 후 10개 반환, 반경 내 없으면 전체에서 반환
    @Override
    public QueryStore.CategoryStoreListGroup findByRadiusAndCategory(LocationCriteria criteria) {
        // query
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        // loop
        List<Category> categories = Arrays.asList(Category.values());
        Collections.shuffle(categories);
        for (Category category : categories) {
            // selectFrom
            JPQLQuery<Store> query = queryFactory.selectFrom(store);

            // where
            BooleanBuilder conditions = new BooleanBuilder();
            conditions.and(isActiveStore(store));
            conditions.and(hasEnoughStoresNearby(queryFactory, criteria) ? withinRadius(store, criteria) : null);
            conditions.and(hasMainImage(store));
            conditions.and(store.categories.contains(category));

            // orderBy
            query.leftJoin(store.operationInfos, operationInfo);
            query.orderBy(byIsOpen(store, operationInfo),
                    byIsCertified(store),
                    byLikeCount(store));

            // fetch
            List<Store> content = query
                    .where(conditions)
                    .distinct()
                    .limit(10)
                    .fetch();
            if (!content.isEmpty()) {
                return new QueryStore.CategoryStoreListGroup(category, content);
            }
        }
        return null;
    }

    // 인기 매장 조회, 점수 기반 계산
    @Override
    public Page<Store> findPopularStore(LocationCriteria criteria, DrinkType drinkType, Sort.Direction priceOrder, Pageable pageable) {
        // query
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QStoreViewLog storeViewLog = QStoreViewLog.storeViewLog;
        QMainDrink mainDrink = QMainDrink.mainDrink;

        // selectFrom
        JPQLQuery<Store> query = queryFactory.selectFrom(store);
        JPQLQuery<Long> countQuery = queryFactory.select(store.id.countDistinct()).from(store);

        // where
        BooleanBuilder conditions = new BooleanBuilder();
        conditions.and(isActiveStore(store));
        conditions.and(hasEnoughStoresNearby(queryFactory, criteria) ? withinRadius(store, criteria) : null);

        if (drinkType != null) {
            query.leftJoin(store.mainDrinks, mainDrink);
            countQuery.leftJoin(store.mainDrinks, mainDrink);
            conditions.and(drinkTypeEquals(mainDrink, drinkType));
        }

        // orderBy
        if (drinkType != null && priceOrder != null) {
            query.orderBy(byPriceOrder(mainDrink, priceOrder));
        }
        double globalAverageRate = fetchGlobalAverageRate(queryFactory);
        query.orderBy(byPopularScore(store, storeViewLog, globalAverageRate));

        // fetch
        List<Store> content = query
                .where(conditions)
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = fetchTotal(countQuery, conditions);
        return new PageImpl<>(content, pageable, total);
    }

    private double fetchGlobalAverageRate(JPAQueryFactory queryFactory) {
        // query
        QStore store = QStore.store;

        // selectFrom
        JPQLQuery<Double> query = queryFactory.select(globalAverageRateExpression(store)).from(store);

        // where
        BooleanBuilder conditions = new BooleanBuilder();
        conditions.and(store.reviewCount.gt(0));

        // fetch
        double defaultGlobalRate = 3.5;
        Double globalAverageRate = query
                .where(conditions)
                .fetchOne();
        return globalAverageRate == null ? defaultGlobalRate : globalAverageRate;
    }

    private boolean hasEnoughStoresNearby(JPAQueryFactory queryFactory, LocationCriteria criteria) {
        // query
        QStore store = QStore.store;

        // selectFrom
        JPQLQuery<Long> query = queryFactory.select(store.id).from(store);

        // where
        BooleanBuilder conditions = baseActiveRadiusCondition(store, criteria);
        conditions.and(hasMainImage(store));

        // fetch
        int threshold = 10;
        List<Long> idList = query
                .where(conditions)
                .distinct()
                .limit(threshold)
                .fetch();
        return idList.size() >= threshold;
    }
}
