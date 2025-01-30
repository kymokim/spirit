package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.menu.entity.QMenu;
import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.entity.Category;
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

    // 영업중인 가게 탐색
    private BooleanExpression openCondition(){
        // 현재 시간 필드
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        DayOfWeek today = now.getDayOfWeek();
        // 영업 시간 필드
        TimePath<LocalTime> openTime = store.businessHours.openTime;
        TimePath<LocalTime> closeTime = store.businessHours.closeTime;
        // 1. 휴무일 여부 판단
        BooleanExpression notClosedToday = store.closedDays.contains(today).not();
        // 2. 정상 영업 시간인 경우 판단
        BooleanExpression openDuringDay = openTime.before(currentTime).and(closeTime.after(currentTime));
        // 3. 자정을 넘어가는 영업 시간인 경우 판단
        BooleanExpression openOverMidnight = openTime.after(closeTime)
                .and(openTime.before(currentTime).or(closeTime.after(currentTime)));
        // 1번이 참이고, 2번 또는 3번이 참이면 true
        return notClosedToday.and(openDuringDay.or(openOverMidnight));
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

    // 영업중인 가게가 먼저, 그렇지 않은 가게는 나중으로 정렬
    private OrderSpecifier<Integer> orderByIsOpen(){
        return new CaseBuilder().when(openCondition()).then(1).otherwise(0).desc();
    }

    // 좋아요 많은 순서대로 정렬
    private OrderSpecifier<Long> orderByLikeCount(){
        return store.likeCount.desc();
    }

    // 마감시간 늦는 순서대로 정렬
    private OrderSpecifier<Integer> orderByCloseTime() {
        TimePath<LocalTime> closeTime = store.businessHours.closeTime;

        // 자정 전후 시간 조정 로직
        NumberExpression<Integer> adjustedCloseTime = new CaseBuilder()
                .when(closeTime.before(LocalTime.MIDNIGHT)) // 자정 이전 마감 시간은 -24시간 처리
                .then(closeTime.hour().add(-24))
                .otherwise(closeTime.hour()); // 자정 이후 마감 시간은 그대로 유지

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

        JPQLQuery<Store> query = queryFactory.selectFrom(store)
                .leftJoin(store.menuList, menu)
                .where(radiusCondition(criteria)
                        .and(storeNameCondition(searchKeyword)
                                .or(menuNameCondition(menu, searchKeyword))))
                .orderBy(orderByIsOpen())
                .orderBy(orderByLikeCount())
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

        JPQLQuery<Store> query = queryFactory.selectFrom(store)
                .where(radiusCondition(criteria))
                .orderBy(orderByIsOpen())
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

        JPQLQuery<Store> query = queryFactory.selectFrom(store)
                .where(radiusCondition(criteria)
                        .and(categoryCondition(category)))
                .orderBy(orderByIsOpen())
                .orderBy(orderByLikeCount());

        List<Store> storeList = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(storeList, pageable, query.fetchCount());
    }

    @Override
    public Page<Store> findByBusinessHours(LocationCriteria criteria, Pageable pageable){
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        store = QStore.store;

        JPQLQuery<Store> query = queryFactory.selectFrom(store)
                .where(radiusCondition(criteria)
                        .and(openCondition()))
                .orderBy(orderByCloseTime())
                .orderBy(orderByLikeCount());

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
}