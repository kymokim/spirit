package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.menu.entity.QMenu;
import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.QOperationInfo;
import com.kymokim.spirit.store.entity.QStore;
import com.kymokim.spirit.store.entity.Store;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


public class StoreRepositoryCustomImpl implements StoreRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    private NumberExpression<Double> distance;
    private QStore store;

    // 반경 내에 있는 가게 탐색
    private BooleanExpression radiusCondition(LocationCriteria criteria){
        distance = Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({4})) * sin(radians({5})))",
                criteria.getLatitude(), store.location.latitude, store.location.longitude, criteria.getLongitude(), criteria.getLatitude(), store.location.latitude);

        return distance.loe(criteria.getRadius());
    }

    // 현재 시간 기준 영업중인 가게 탐색
    private BooleanExpression openCondition(QOperationInfo operationInfo){
        return openCondition(operationInfo, LocalDateTime.now());
    }

    // 영업중인 가게 탐색
    private BooleanExpression openCondition(QOperationInfo operationInfo, LocalDateTime conditionTime){
        // 0. 항상 영업중인지(24시간)
        BooleanExpression isAlwaysOpen = store.isAlwaysOpen.isTrue();

        // 조건 시간 필드
        LocalTime currentTime = conditionTime.toLocalTime();;
        DayOfWeek today = conditionTime.getDayOfWeek();
        System.out.println(currentTime);
        System.out.println(today);

        // 영업 시간 필드
        TimePath<LocalTime> openTime = operationInfo.openTime;
        TimePath<LocalTime> closeTime = operationInfo.closeTime;
        System.out.println(openTime);
        System.out.println(closeTime);

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
                .and(openTime.before(currentTime))
                        // 마감 시간 > 현재 시간(자정 이후)
                        .or(closeTime.after(currentTime));
        // 0번이 참이거나, 1번과 2번과 3/4번(둘 중에 하나라도 참)이 참이면 true
        return isAlwaysOpen.or(matchDay
                .and(notClosedToday)
                .and(openDuringDay.or(openOverMidnight))
        );
    }

    // 검색어가 공백 제외하고 이름에 포함되어 있는 가게 탐색
    private BooleanExpression storeNameCondition(String searchKeyword){
        String formattedSearchKeyword = searchKeyword.trim().replaceAll("\\s+", "");
        return Expressions.stringTemplate("REPLACE({0}, ' ', '')", store.name).containsIgnoreCase(formattedSearchKeyword);
    }

    // 검색어가 공백 제외하고 이름에 포함되어 있는 메뉴 탐색
    private BooleanExpression menuNameCondition(QMenu menu, String searchKeyword){
        String formattedSearchKeyword = searchKeyword.trim().replaceAll("\\s+", "");
        return Expressions.stringTemplate("REPLACE({0}, ' ', '')", menu.name).containsIgnoreCase(formattedSearchKeyword);
    }

    // 카테고리에 해당하는 가게 탐색
    private BooleanExpression categoryCondition(String category){
        return store.categories.contains(Category.valueOf(category));
    }

    // 단체 가능 여부에 해당하는 가게 탐색
    private BooleanExpression isGroupAvailableCondition(Boolean isGroupAvailable) {
        return store.isGroupAvailable.eq(isGroupAvailable);
    }

    // 영업중인 가게가 먼저, 그렇지 않은 가게는 나중으로 정렬
    private OrderSpecifier<Integer> orderByIsOpen(QOperationInfo operationInfo){
        return new CaseBuilder().when(openCondition(operationInfo)).then(1).otherwise(0).desc();
    }

    // 좋아요 많은 순서대로 정렬
    private OrderSpecifier<Long> orderByLikeCount(){
        return store.likeCount.desc();
    }

    // 24시간 영업일 경우 우선으로 정렬
    private OrderSpecifier<Integer> orderByAlwaysOpen(){

        NumberExpression<Integer> alwaysOpenPriority = new CaseBuilder()
                .when(store.isAlwaysOpen.isTrue())
                .then(1)
                .otherwise(0);
        return alwaysOpenPriority.desc();
    }

    // 마감시간 늦는 순서대로 정렬
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

    // 가까운 순서대로 정렬
    private OrderSpecifier<Double> orderByDistance() {
        return distance.asc();
    }

    // 검색어가 가게명, 메뉴명에 포함되는 가게 리스트 반환
    @Override
    public Page<Store> findByNameAndMenu(LocationCriteria criteria, String searchKeyword, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        store = QStore.store;
        QMenu menu = QMenu.menu;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        JPQLQuery<Store> query = queryFactory.selectFrom(store)
                .leftJoin(store.menuList, menu)
                .leftJoin(store.operationInfos, operationInfo)
                .where(radiusCondition(criteria)
                        .and(storeNameCondition(searchKeyword)
                                .or(menuNameCondition(menu, searchKeyword))))
                .orderBy(orderByIsOpen(operationInfo))
                .orderBy(orderByLikeCount())
                .distinct();

        List<Store> storeList = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(storeList, pageable, query.fetchCount());
    }

    // 검색어가 가게명에 포함되는 가게 리스트 반환
    @Override
    public Page<Store> findByName(String searchKeyword, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        store = QStore.store;

        JPQLQuery<Store> query = queryFactory.selectFrom(store)
                .where(storeNameCondition(searchKeyword))
                .distinct();

        List<Store> storeList = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(storeList, pageable, query.fetchCount());
    }

    // 가까운 순 가게 리스트 반환
    @Override
    public Page<Store> findByDistance(LocationCriteria criteria, Pageable pageable){
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        JPQLQuery<Store> query = queryFactory.selectFrom(store)
                .leftJoin(store.operationInfos, operationInfo)
                .where(radiusCondition(criteria))
                .orderBy(orderByIsOpen(operationInfo))
                .orderBy(orderByDistance());

        List<Store> storeList = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(storeList, pageable, query.fetchCount());
    }

    // 카테고리 해당하는 가게 리스트 반환
    @Override
    public Page<Store> findByCategory(LocationCriteria criteria, String category, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        JPQLQuery<Store> query = queryFactory.selectFrom(store)
                .leftJoin(store.operationInfos, operationInfo)
                .where(radiusCondition(criteria)
                        .and(categoryCondition(category)))
                .orderBy(orderByIsOpen(operationInfo))
                .orderBy(orderByLikeCount());

        List<Store> storeList = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(storeList, pageable, query.fetchCount());
    }

    // 현재 영업중인 가게중, 영업시간 늦는 순서 가게 리스트 반환
    @Override
    public Page<Store> findByBusinessHours(LocationCriteria criteria, Pageable pageable){
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        JPQLQuery<Store> query = queryFactory.selectFrom(store)
                .leftJoin(store.operationInfos, operationInfo)
                .where(radiusCondition(criteria)
                        .and(openCondition(operationInfo)))
                .orderBy(orderByAlwaysOpen())
                .orderBy(orderByCloseTime(operationInfo))
                .orderBy(orderByLikeCount())
                .distinct();

        List<Store> storeList = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(storeList, pageable, query.fetchCount());
    }

    // 근처 가게 리스트 반환(지도용)
    @Override
    public List<Store> findByRadius(LocationCriteria criteria){
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        store = QStore.store;

        return queryFactory.selectFrom(store)
                .where(radiusCondition(criteria))
                .fetch();
    }

    @Override
    public Page<Store> findByMultipleCondition(LocationCriteria criteria, String category, Boolean isGroupAvailable, LocalDateTime conditionTime, Pageable pageable){
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        store = QStore.store;
        QOperationInfo operationInfo = QOperationInfo.operationInfo;

        JPQLQuery<Store> query = queryFactory.selectFrom(store)
                .leftJoin(store.operationInfos, operationInfo)
                .where(radiusCondition(criteria)
                        .and(categoryCondition(category))
                        .and(isGroupAvailableCondition(isGroupAvailable))
                        .and(openCondition(operationInfo, conditionTime)))
                .orderBy(orderByLikeCount());

        List<Store> storeList = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(storeList, pageable, query.fetchCount());
    }
}