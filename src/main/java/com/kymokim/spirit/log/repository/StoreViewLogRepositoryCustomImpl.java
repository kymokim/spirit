package com.kymokim.spirit.log.repository;

import com.kymokim.spirit.auth.entity.Gender;
import com.kymokim.spirit.log.dto.RequestLog;
import com.kymokim.spirit.log.dto.ResponseLog;
import com.kymokim.spirit.log.entity.QStoreViewLog;
import com.kymokim.spirit.log.entity.StoreViewLog;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class StoreViewLogRepositoryCustomImpl implements StoreViewLogRepositoryCustom {

    @PersistenceContext(unitName = "main")
    private EntityManager entityManager;

    private final QStoreViewLog storeViewLog = QStoreViewLog.storeViewLog;

    private record GroupKey(String ageGroup, Gender gender) {
    }

    @Override
    public Page<Long> findViewedStoreIds(Long userId, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStoreViewLog svl = QStoreViewLog.storeViewLog;

        List<Long> content = queryFactory
                .select(svl.storeId)
                .from(svl)
                .where(svl.userId.eq(userId))
                .groupBy(svl.storeId)
                .orderBy(svl.viewDateTime.max().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(svl.storeId.countDistinct())
                .from(svl)
                .where(svl.userId.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    @Override
    public List<ResponseLog.StoreViewLogStatListDto> getStoreViewLogStats(RequestLog.StoreViewLogStatFilter filter) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        BooleanBuilder where = buildWhereClause(filter);

        List<StoreViewLog> storeViewLogList = queryFactory
                .select(storeViewLog)
                .from(storeViewLog)
                .where(where)
                .fetch();

        Function<StoreViewLog, LocalDate> dateGrouper = log -> {
            LocalDate date = log.getViewDate();
            return switch (filter.getShowBy()) {
                case "day" -> date;
                case "week" -> date.with(DayOfWeek.MONDAY);
                case "month" -> date.withDayOfMonth(1);
                default -> date;
            };
        };

        Map<LocalDate, Map<GroupKey, Long>> grouped = storeViewLogList.stream()
                .collect(Collectors.groupingBy(
                        dateGrouper,
                        Collectors.groupingBy(
                                log -> new GroupKey(
                                        filter.getGroupBy().contains("ageGroup") ? toAgeGroup(log.getBirthYear()) : null,
                                        filter.getGroupBy().contains("gender") ? log.getGender() : null
                                ),
                                Collectors.counting()
                        )
                ));

        return grouped.entrySet().stream()
                .map(entry -> new ResponseLog.StoreViewLogStatListDto(
                        entry.getKey(),
                        entry.getValue().entrySet().stream()
                                .map(e -> new ResponseLog.StoreViewLogStatDto(
                                        e.getKey().ageGroup(),
                                        e.getKey().gender(),
                                        e.getValue()
                                )).toList()
                )).sorted(Comparator.comparing(ResponseLog.StoreViewLogStatListDto::getDate))
                .toList();
    }

    private BooleanBuilder buildWhereClause(RequestLog.StoreViewLogStatFilter filter) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(storeViewLog.storeId.eq(filter.getStoreId()));
        where.and(storeViewLog.viewDate.between(filter.getStartDate(), filter.getEndDate()));

        if (filter.getGender() != null) {
            where.and(storeViewLog.gender.eq(filter.getGender()));
        }
        if (filter.getAgeGroups() != null && !filter.getAgeGroups().isEmpty()) {
            where.and(ageGroupIn(storeViewLog.birthYear, filter.getAgeGroups()));
        }

        return where;
    }

    private BooleanExpression ageGroupIn(StringPath birthYearPath, List<String> targetGroups) {
        Pattern pattern = Pattern.compile("^[1-9]0s$");
        Set<String> allowedYears = new HashSet<>();

        for (String group : targetGroups) {
            if (!pattern.matcher(group).matches()) continue;

            int base = Integer.parseInt(group.replace("s", ""));
            int startYear = LocalDate.now().getYear() - base - 9 + 1;
            int endYear = LocalDate.now().getYear() - base + 1;

            IntStream.rangeClosed(startYear, endYear)
                    .mapToObj(String::valueOf)
                    .forEach(allowedYears::add);
        }

        return birthYearPath.in(allowedYears);
    }

    private String toAgeGroup(String birthYear) {
        if (birthYear == null || birthYear.length() != 4) return null;
        try {
            int age = LocalDate.now().getYear() - Integer.parseInt(birthYear) + 1;
            return (age / 10) * 10 + "s";
        } catch (NumberFormatException e) {
            return null;
        }
    }
}