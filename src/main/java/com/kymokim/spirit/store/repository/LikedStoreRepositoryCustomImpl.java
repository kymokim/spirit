package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.auth.entity.Gender;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.entity.QLikedStore;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class LikedStoreRepositoryCustomImpl implements LikedStoreRepositoryCustom {

    @PersistenceContext(unitName = "main")
    private EntityManager entityManager;

    private final QLikedStore likedStore = QLikedStore.likedStore;
    private record GroupKey(String ageGroup, Gender gender) {}

    @Override
    public List<ResponseStore.LikedStoreStatDto> getLikedStoreStats(RequestStore.LikedStoreStatFilter filter) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        BooleanBuilder where = buildWhereClause(filter);

        List<Tuple> tupleList = queryFactory
                .select(likedStore.birthYear, likedStore.gender)
                .from(likedStore)
                .where(where)
                .fetch();

        Map<GroupKey, Long> grouped = tupleList.stream()
                .collect(Collectors.groupingBy(
                        tuple -> new GroupKey(
                                filter.getGroupBy().contains("ageGroup") ? toAgeGroup(tuple.get(likedStore.birthYear)) : null,
                                filter.getGroupBy().contains("gender") ? tuple.get(likedStore.gender) : null
                        ),
                        Collectors.counting()
                ));

        return grouped.entrySet().stream()
                .map(e -> new ResponseStore.LikedStoreStatDto(
                        e.getKey().ageGroup(),
                        e.getKey().gender(),
                        e.getValue()
                ))
                .toList();
    }

    private BooleanBuilder buildWhereClause(RequestStore.LikedStoreStatFilter filter) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(likedStore.storeId.eq(filter.getStoreId()));

        if (filter.getGender() != null) {
            where.and(likedStore.gender.eq(filter.getGender()));
        }
        if (filter.getAgeGroups() != null && !filter.getAgeGroups().isEmpty()) {
            where.and(ageGroupIn(likedStore.birthYear, filter.getAgeGroups()));
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