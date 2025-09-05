package com.kymokim.spirit.log.repository;

import com.kymokim.spirit.log.dto.RequestLog;
import com.kymokim.spirit.log.dto.ResponseLog;
import com.kymokim.spirit.log.entity.OwnershipLog;
import com.kymokim.spirit.log.entity.QOwnershipLog;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OwnershipLogRepositoryCustomImpl implements OwnershipLogRepositoryCustom {

    @PersistenceContext(unitName = "main")
    private EntityManager entityManager;

    @Override
    public List<ResponseLog.OwnershipStatListDto> getOwnershipStats(RequestLog.OwnershipLogStatFilter filter) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QOwnershipLog ownershipLog = QOwnershipLog.ownershipLog;

        BooleanBuilder where = new BooleanBuilder();
        LocalDateTime start = filter.getStartDate().atStartOfDay();
        LocalDateTime end = filter.getEndDate().atTime(23, 59, 59, 999_999_999);
        where.and(ownershipLog.approvedDate.between(filter.getStartDate(), filter.getEndDate()));

        List<OwnershipLog> list = queryFactory
                .select(ownershipLog)
                .from(ownershipLog)
                .where(where)
                .fetch();

        Function<OwnershipLog, LocalDate> dateGrouper = log -> {
            LocalDate date = log.getApprovedDate();
            return switch (filter.getShowBy()) {
                case "day" -> date;
                case "week" -> date.with(DayOfWeek.MONDAY);
                case "month" -> date.withDayOfMonth(1);
                default -> date;
            };
        };

        Map<LocalDate, Long> grouped = list.stream()
                .collect(Collectors.groupingBy(dateGrouper, Collectors.counting()));

        return grouped.entrySet().stream()
                .map(e -> new ResponseLog.OwnershipStatListDto(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(ResponseLog.OwnershipStatListDto::getDate))
                .toList();
    }
}

