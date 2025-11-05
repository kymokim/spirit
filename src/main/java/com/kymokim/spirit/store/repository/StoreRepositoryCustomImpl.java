package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.drink.entity.DrinkType;
import com.kymokim.spirit.log.entity.QStoreViewLog;
import com.kymokim.spirit.menu.entity.QMenu;
import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.dto.FacilitiesCondition;
import com.kymokim.spirit.store.dto.QueryStore;
import com.kymokim.spirit.store.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class StoreRepositoryCustomImpl implements StoreRepositoryCustom {
    @PersistenceContext(unitName = "main")
    private EntityManager entityManager;

    // ===== Expressions =====

    // 하버사인 거리 계산식
    private NumberExpression<Double> distanceExpression(QStore store, LocationCriteria criteria) {
        return Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({4})) * sin(radians({5})))",
                criteria.getLatitude(), store.location.latitude, store.location.longitude, criteria.getLongitude(), criteria.getLatitude(), store.location.latitude);
    }

    // 조회 점수 계산식, 30일 이내 조회 집계, 7일 이내는 가중치 1.0, 8일차부터 0.85 ^ (D-6) 감쇠, D = DATEDIFF(CURDATE(), view_date)
    private NumberExpression<Double> viewScoreExpression(QStore store, QStoreViewLog storeViewLog) {
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

    // 좋아요 점수 계산식, log(1 + likeCount)
    private NumberExpression<Double> likeScoreExpression(QStore store) {
        return Expressions.numberTemplate(Double.class, "LOG(1 + {0})", store.likeCount);
    }

    // 매장 전체 평균 평점 계산식, 평점 없을 시 기본 값 3.5로 계산
    private NumberExpression<Double> globalAverageRateExpression(QStore store) {
        return Expressions.numberTemplate(
                Double.class,
                "COALESCE(SUM({0})/NULLIF(SUM({1}),0), {2})",
                store.totalRate, store.reviewCount, 3.5);
    }

    // 베이지안 평균 평점 계산식
    private NumberExpression<Double> bayesianAverageRateExpression(QStore store, double globalAverageRate) {
        // 매장 평균 평점 = totalRate / reviewCount (0 나눗셈 방지)
        NumberExpression<Double> averageRate =
                Expressions.numberTemplate(Double.class,
                        "COALESCE({0} / NULLIF({1}, 0), 0.0)", store.totalRate, store.reviewCount);

        // 베이지안 평균 평점 계산(샘플 가중치 = 5)
        int priorSampleWeight = 5;
        return Expressions.numberTemplate(Double.class,
                "((({0} / ({0} + {1})) * {2}) + (({1} / ({0} + {1})) * {3}))",
                store.reviewCount, priorSampleWeight, averageRate, Expressions.constant(globalAverageRate));
    }

    // ===== Conditions =====

    // 삭제 여부에 해당하는 가게 조건식
    private BooleanExpression isDeletedCondition(QStore store) {
        return store.isDeleted.eq(false);
    }

    // 반경 내에 있는 가게 탐색 조건식
    private BooleanExpression radiusCondition(QStore store, LocationCriteria criteria) {
        return distanceExpression(store, criteria).loe(criteria.getRadius());
    }

    // 현재 시간 기준 영업중인 가게 탐색 조건식
    private BooleanExpression openCondition(QStore store, QOperationInfo operationInfo) {
        return openCondition(store, operationInfo, LocalDateTime.now());
    }

    // 영업중인 가게 탐색 조건식
    private BooleanExpression openCondition(QStore store, QOperationInfo operationInfo, LocalDateTime conditionTime) {
        // 0. 항상 영업중인지(24시간)
        BooleanExpression isAlwaysOpen = store.isAlwaysOpen.isTrue();

        // 조건 시간 필드
        LocalTime currentTime = conditionTime.toLocalTime();
        DayOfWeek today;
        if (currentTime.isBefore(LocalTime.of(9, 0))) {
            today = conditionTime.minusDays(1).getDayOfWeek();
        } else {
            today = conditionTime.getDayOfWeek();
        }

        // 영업 시간 필드
        TimePath<LocalTime> openTime = operationInfo.openTime;
        TimePath<LocalTime> closeTime = operationInfo.closeTime;

        // 1. 오늘에 해당하는 요일 존재 여부 판단(요일 맞추기)
        BooleanExpression matchDay = operationInfo.dayOfWeek.eq(today);
        // 2. 휴무일 여부 판단
        BooleanExpression notClosedToday = operationInfo.isClosed.isFalse();
        // 3. 정상 영업 시간인 경우, 오픈 시간 < 현재 시간이고
        BooleanExpression openDuringDay = openTime.before(currentTime)
                // 현재 시간 < 마감 시간이면 영업중
                .and(closeTime.after(currentTime));
        // 4. 자정을 넘어가는 영업 시간인 경우(시작 시간 > 마감 시간)
        BooleanExpression openOverMidnight = openTime.after(closeTime)
                // 오픈 시간 < 현재 시간(자정 이전)
                .and(openTime.before(currentTime)
                        // 마감 시간 > 현재 시간(자정 이후)
                        .or(closeTime.after(currentTime)));
        // 0번이 참이거나, 1번과 2번과 3/4번(둘 중에 하나라도 참)이 참이면 true
        return isAlwaysOpen.or(matchDay
                .and(notClosedToday)
                .and(openDuringDay.or(openOverMidnight))
        );
    }

    // 검색어가 공백 제외하고 이름에 포함되어 있는 가게 탐색 조건식
    private BooleanExpression storeNameCondition(QStore store, String searchKeyword) {
        String formattedSearchKeyword = searchKeyword.trim().replaceAll("\\s+", "");
        return Expressions.stringTemplate("REPLACE({0}, ' ', '')", store.name).containsIgnoreCase(formattedSearchKeyword);
    }

    // 검색어가 공백 제외하고 이름에 포함되어 있는 메뉴 탐색 조건식
    private BooleanExpression menuNameCondition(QMenu menu, String searchKeyword) {
        String formattedSearchKeyword = searchKeyword.trim().replaceAll("\\s+", "");
        return Expressions.stringTemplate("REPLACE({0}, ' ', '')", menu.name).containsIgnoreCase(formattedSearchKeyword);
    }

    // 카테고리에 해당하는 가게 탐색 조건식
    private BooleanExpression categoryCondition(QStore store, String category) {
        return store.categories.contains(Category.valueOf(category));
    }

    // 편의시설 조건 동적 적용
    private void applyFacilitiesCondition(BooleanBuilder conditionBuilder, QStore store, FacilitiesCondition facilitiesCondition) {
        if (facilitiesCondition == null) {
            return;
        }
        if (facilitiesCondition.getHasScreen() != null) {
            conditionBuilder.and(store.facilitiesInfo.hasScreen.eq(facilitiesCondition.getHasScreen()));
        }
        if (facilitiesCondition.getHasRoom() != null) {
            conditionBuilder.and(store.facilitiesInfo.hasRoom.eq(facilitiesCondition.getHasRoom()));
        }
        if (facilitiesCondition.getHasOutdoor() != null) {
            conditionBuilder.and(store.facilitiesInfo.hasOutdoor.eq(facilitiesCondition.getHasOutdoor()));
        }
        if (facilitiesCondition.getIsGroupAvailable() != null) {
            conditionBuilder.and(store.facilitiesInfo.isGroupAvailable.eq(facilitiesCondition.getIsGroupAvailable()));
        }
        if (facilitiesCondition.getIsParkingAvailable() != null) {
            conditionBuilder.and(store.facilitiesInfo.isParkingAvailable.eq(facilitiesCondition.getIsParkingAvailable()));
        }
        if (facilitiesCondition.getIsCorkageAvailable() != null) {
            conditionBuilder.and(store.facilitiesInfo.isCorkageAvailable.eq(facilitiesCondition.getIsCorkageAvailable()));
        }
    }

    // 대표 이미지 보유 가게 탐색 조건식
    private BooleanExpression hasMainImageCondition(QStore store) {
        return store.mainImgUrl.isNotNull().and(store.mainImgUrl.isNotEmpty());
    }

    // ===== Orderings =====

    // 영업중인 가게가 먼저, 그렇지 않은 가게는 나중순 정렬식
    private OrderSpecifier<Integer> orderByIsOpen(QStore store, QOperationInfo operationInfo) {
        return new CaseBuilder().when(openCondition(store, operationInfo)).then(1).otherwise(0).desc();
    }

    // 좋아요 많은 순 정렬식
    private OrderSpecifier<Long> orderByLikeCount(QStore store) {
        return store.likeCount.desc();
    }

    // 24시간 영업일 경우 우선 정렬식
    private OrderSpecifier<Integer> orderByAlwaysOpen(QStore store) {

        NumberExpression<Integer> alwaysOpenPriority = new CaseBuilder()
                .when(store.isAlwaysOpen.isTrue())
                .then(1)
                .otherwise(0);
        return alwaysOpenPriority.desc();
    }

    // 마감시간 늦는 순 정렬식
    private OrderSpecifier<Integer> orderByCloseTime(QOperationInfo operationInfo) {
        TimePath<LocalTime> openTime = operationInfo.openTime;
        TimePath<LocalTime> closeTime = operationInfo.closeTime;

        // 자정 이후 마감인 경우 시간 조정 로직
        NumberExpression<Integer> adjustedCloseTime = new CaseBuilder()
                // 오픈 시간 > 마감 시간인 경우(자정 이후 마감)
                .when(openTime.after(closeTime))
                // 마감 시간 + 24시간
                .then(closeTime.hour().add(24).multiply(60).add(closeTime.minute()))
                // 그렇지 않은 경우(자정 이전 마감) 마감시간 그대로 유지
                .otherwise(closeTime.hour().multiply(60).add(closeTime.minute()));

        return adjustedCloseTime.desc(); // 늦은 시간순으로 정렬
    }

    // 가까운 순 정렬식
    private OrderSpecifier<Double> orderByDistance(QStore store, LocationCriteria criteria) {
        return distanceExpression(store, criteria).asc();
    }

    // 인증된 가게 우선 정렬식
    private OrderSpecifier<Integer> orderByIsCertified(QStore store) {
        NumberExpression<Integer> isCertifiedPriority = new CaseBuilder()
                .when(store.ownerId.isNotNull())
                .then(1)
                .otherwise(0);
        return isCertifiedPriority.desc();
    }

    // 인기 점수 기반 정렬식
    private OrderSpecifier<Double> orderByPopularScore(QStore store, QStoreViewLog storeViewLog, Double globalAverageRate) {
        double weightView = 1.0, weightLike = 0.7, weightRate = 1.2;  // 가중치 값
        NumberExpression<Double> score =
                Expressions.numberTemplate(Double.class,
                        "({0}*LOG(1+{1}) + {2}*{3} + {4}*{5})",
                        weightView, viewScoreExpression(store, storeViewLog), // 조회
                        weightLike, likeScoreExpression(store), // 좋아요
                        weightRate, bayesianAverageRateExpression(store, globalAverageRate) // 평점(베이지안)
                );
        return score.desc();
    }

    // ===== Queries =====

    // 검색어가 가게명, 메뉴명에 포함되는 가게 리스트 반환
    @Override
    public Page<Store> findByNameAndMenu(LocationCriteria criteria, String searchKeyword, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QMenu menu = QMenu.menu;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        List<Store> content = queryFactory.selectFrom(store)
                .leftJoin(store.menuList, menu)
                .leftJoin(store.operationInfos, operationInfo)
                .where(isDeletedCondition(store)
                        .and(radiusCondition(store, criteria))
                        .and(storeNameCondition(store, searchKeyword)
                                .or(menuNameCondition(menu, searchKeyword))))
                .orderBy(orderByIsOpen(store, operationInfo))
                .orderBy(orderByIsCertified(store))
                .orderBy(orderByLikeCount(store))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(store.id.countDistinct())
                .from(store)
                .leftJoin(store.menuList, menu)
                .where(isDeletedCondition(store)
                        .and(radiusCondition(store, criteria))
                        .and(storeNameCondition(store, searchKeyword)
                                .or(menuNameCondition(menu, searchKeyword))))
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    // 검색어가 가게명에 포함되는 가게 리스트 반환
    @Override
    public Page<Store> findByName(String searchKeyword, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;

        List<Store> content = queryFactory.selectFrom(store)
                .where(isDeletedCondition(store)
                        .and(storeNameCondition(store, searchKeyword)))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(store.id.countDistinct())
                .from(store)
                .where(isDeletedCondition(store)
                        .and(storeNameCondition(store, searchKeyword)))
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    // 가까운 순 가게 리스트 반환
    @Override
    public Page<Store> findByDistance(LocationCriteria criteria, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        List<Store> content = queryFactory.selectFrom(store)
                .leftJoin(store.operationInfos, operationInfo)
                .where(isDeletedCondition(store)
                        .and(radiusCondition(store, criteria)))
                .orderBy(orderByIsOpen(store, operationInfo))
                .orderBy(orderByDistance(store, criteria))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(store.id.countDistinct())
                .from(store)
                .where(isDeletedCondition(store)
                        .and(radiusCondition(store, criteria)))
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    // 카테고리에 해당하는 가게 리스트 반환 + 동적 주종 필터
    @Override
    public Page<Store> findByCategory(LocationCriteria criteria, String category, DrinkType drinkType, Sort.Direction priceOrder, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;
        QMainDrink mainDrink = QMainDrink.mainDrink;

        BooleanBuilder conditionBuilder = new BooleanBuilder();
        conditionBuilder.and(isDeletedCondition(store));
        conditionBuilder.and(radiusCondition(store, criteria));
        conditionBuilder.and(categoryCondition(store, category));
        if (drinkType != null) {
            conditionBuilder.and(store.mainDrinks.any().type.eq(drinkType));
        }

        JPQLQuery<Store> query = queryFactory.selectFrom(store);
        JPQLQuery<Long> countQuery = queryFactory
                .select(store.id.countDistinct())
                .from(store);

        if (drinkType != null) {
            query.leftJoin(store.mainDrinks, mainDrink);
            countQuery.leftJoin(store.mainDrinks, mainDrink);
            conditionBuilder.and(mainDrink.type.eq(drinkType));
            if (priceOrder != null) {
                query.orderBy(priceOrder.isAscending() ? mainDrink.price.asc() : mainDrink.price.desc());
            }
        }

        List<Store> content = query.leftJoin(store.operationInfos, operationInfo)
                .where(conditionBuilder)
                .orderBy(orderByIsOpen(store, operationInfo))
                .orderBy(orderByIsCertified(store))
                .orderBy(orderByLikeCount(store))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = countQuery
                .where(conditionBuilder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    // 현재 영업중인 가게중, 영업시간 늦는 순서 가게 리스트 반환
    @Override
    public Page<Store> findByBusinessHours(LocationCriteria criteria, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        List<Store> content = queryFactory.selectFrom(store)
                .leftJoin(store.operationInfos, operationInfo)
                .where(isDeletedCondition(store)
                        .and(radiusCondition(store, criteria))
                        .and(openCondition(store, operationInfo)))
                .orderBy(orderByAlwaysOpen(store))
                .orderBy(orderByCloseTime(operationInfo))
                .orderBy(orderByIsCertified(store))
                .orderBy(orderByLikeCount(store))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(store.id.countDistinct())
                .from(store)
                .leftJoin(store.operationInfos, operationInfo)
                .where(isDeletedCondition(store)
                        .and(radiusCondition(store, criteria))
                        .and(openCondition(store, operationInfo)))
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    // 근처 가게 리스트 반환(지도용)
    @Override
    public List<Store> findByRadius(LocationCriteria criteria) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;

        return queryFactory.selectFrom(store)
                .where(isDeletedCondition(store)
                        .and(radiusCondition(store, criteria)))
                .fetch();
    }

    @Override
    public Page<Store> findByMultipleCondition(LocationCriteria criteria, String category, FacilitiesCondition facilitiesCondition, LocalDateTime conditionTime, DrinkType drinkType, Set<Mood> moods, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;
        QMainDrink mainDrink = QMainDrink.mainDrink;

        BooleanBuilder conditionBuilder = new BooleanBuilder();
        conditionBuilder.and(isDeletedCondition(store));
        conditionBuilder.and(radiusCondition(store, criteria));

        JPQLQuery<Store> query = queryFactory.selectFrom(store);
        JPQLQuery<Long> countQuery = queryFactory
                .select(store.id.countDistinct())
                .from(store);

        if (category != null) {
            conditionBuilder.and(categoryCondition(store, category));
        }
        applyFacilitiesCondition(conditionBuilder, store, facilitiesCondition);
        if (conditionTime != null) {
            query.leftJoin(store.operationInfos, operationInfo);
            countQuery.leftJoin(store.operationInfos, operationInfo);
            conditionBuilder.and(openCondition(store, operationInfo, conditionTime));
        }
        if (drinkType != null) {
            query.leftJoin(store.mainDrinks, mainDrink);
            countQuery.leftJoin(store.mainDrinks, mainDrink);
            conditionBuilder.and(store.mainDrinks.any().type.eq(drinkType));
        }
        if (moods != null && !moods.isEmpty()) {
            conditionBuilder.and(store.moods.any().in(moods));
        }

        List<Store> content = query
                .where(conditionBuilder)
                .orderBy(orderByIsCertified(store))
                .orderBy(orderByLikeCount(store))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = countQuery
                .where(conditionBuilder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    // 랜덤 카테고리 선정 후 10개 반환, 반경 내 없으면 전체에서 반환
    @Override
    public QueryStore.CategoryStoreListGroup findByRadiusAndCategory(LocationCriteria criteria) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        // 반경 내 술집 10개 이상 존재 여부
        int threshold = 10;
        boolean hasEnoughStoresNearby = queryFactory
                .select(store.id)
                .from(store)
                .where(isDeletedCondition(store)
                        .and(radiusCondition(store, criteria))
                        .and(hasMainImageCondition(store)))
                .limit(threshold)
                .distinct()
                .fetch()
                .size() >= threshold;

        // 카테고리 랜덤 순회
        List<Category> categories = Arrays.asList(Category.values());
        Collections.shuffle(categories);

        for (Category category : categories) {
            JPQLQuery<Store> query = queryFactory.selectFrom(store)
                    .leftJoin(store.operationInfos, operationInfo)
                    .where(isDeletedCondition(store)
                            // 반경 안에 가게가 있으면 반경 내에서, 없으면 전체에서 조회
                            .and(hasEnoughStoresNearby ? radiusCondition(store, criteria) : null)
                            .and(hasMainImageCondition(store))
                            .and(store.categories.contains(category)))
                    .orderBy(orderByIsOpen(store, operationInfo))
                    .orderBy(orderByIsCertified(store))
                    .orderBy(orderByLikeCount(store))
                    .limit(10);

            List<Store> result = query.fetch();
            // 결과가 있으면 바로 반환
            if (!result.isEmpty()) {
                return new QueryStore.CategoryStoreListGroup(category, result);
            }
        }
        return null;
    }

    // 인기 매장 조회, 점수 기반 계산
    @Override
    public Page<Store> findPopularStore(LocationCriteria criteria, DrinkType drinkType, Sort.Direction priceOrder, Pageable pageable) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;
        QStoreViewLog storeViewLog = QStoreViewLog.storeViewLog;
        QMainDrink mainDrink = QMainDrink.mainDrink;

        // 반경 내 술집 10개 이상 존재 여부
        int threshold = 10;
        boolean hasEnoughStoresNearby = queryFactory
                .select(store.id)
                .from(store)
                .where(isDeletedCondition(store)
                        .and(radiusCondition(store, criteria)))
                .distinct()
                .limit(threshold)
                .fetch()
                .size() >= threshold;

        BooleanBuilder where = new BooleanBuilder();
        where.and(isDeletedCondition(store));
        where.and(hasEnoughStoresNearby ? radiusCondition(store, criteria) : null);

        JPQLQuery<Store> query = queryFactory.selectFrom(store);
        JPQLQuery<Long> countQuery = queryFactory
                .select(store.id.countDistinct())
                .from(store);

        if (drinkType != null) {
            query.leftJoin(store.mainDrinks, mainDrink);
            countQuery.leftJoin(store.mainDrinks, mainDrink);
            where.and(mainDrink.type.eq(drinkType));
            if (priceOrder != null) {
                query.orderBy(priceOrder.isAscending() ? mainDrink.price.asc() : mainDrink.price.desc());
            }
        }

        Double globalAverageRate = queryFactory.select(globalAverageRateExpression(store))
                .from(store)
                .where(store.reviewCount.gt(0))
                .fetchOne();
        if (globalAverageRate == null) {
            globalAverageRate = 3.5;
        }

        List<Store> content = query
                .where(where)
                .orderBy(orderByPopularScore(store, storeViewLog, globalAverageRate))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = countQuery
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }
}
