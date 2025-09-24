package com.kymokim.spirit.log.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.Gender;
import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.AESUtil;
import com.kymokim.spirit.log.dto.RequestLog;
import com.kymokim.spirit.log.dto.ResponseLog;
import com.kymokim.spirit.log.entity.OwnershipLog;
import com.kymokim.spirit.log.entity.StoreViewLog;
import com.kymokim.spirit.log.exception.LogErrorCode;
import com.kymokim.spirit.log.repository.OwnershipLogRepository;
import com.kymokim.spirit.log.repository.StoreViewLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@MainTransactional
public class LogService {
    private final StoreViewLogRepository storeViewLogRepository;
    private final OwnershipLogRepository ownershipLogRepository;
    private final AESUtil aesUtil;

    public void createStoreViewLog(Long storeId) {
        Auth user = AuthResolver.resolveUser();
        if (Objects.equals(user.getPersonalInfo(), null) || user.getPersonalInfo().getGender().equals(Gender.UNKNOWN)) {
            return;
        }
        StoreViewLog originStoreViewLog = storeViewLogRepository.getByUserIdAndStoreIdAndViewDate(
                user.getId(), storeId, LocalDate.now()
        );
        if (originStoreViewLog != null) {
            originStoreViewLog.updateViewTime();
            storeViewLogRepository.save(originStoreViewLog);
            return;
        }
        StoreViewLog storeViewLog = StoreViewLog.builder()
                .userId(user.getId())
                .storeId(storeId)
                .gender(user.getPersonalInfo().getGender())
                .birthYear(aesUtil.decrypt(user.getPersonalInfo().getBirthDate()).substring(0, 4))
                .build();

        storeViewLogRepository.save(storeViewLog);
    }

    public List<ResponseLog.StoreViewLogStatListDto> getStoreViewLogStats(Long storeId, String period, Gender gender, List<String> ageGroup, List<String> groupBy, String showBy) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = switch (period) {
            case "day" -> now;
            case "week" -> now.minusDays(6);
            case "month" -> now.minusDays(29);
            case "year" -> now.minusDays(364);
            default -> throw new CustomException(LogErrorCode.INVALID_PERIOD);
        };
        if (groupBy != null && !groupBy.isEmpty()) {
            Set<String> validFields = Set.of("ageGroup", "gender");
            for (String field : groupBy) {
                if (!validFields.contains(field)) {
                    throw new CustomException(LogErrorCode.INVALID_GROUP_BY);
                }
            }
        }

        Map<String, Integer> unitOrder = Map.of(
                "day", 1,
                "week", 2,
                "month", 3,
                "year", 4
        );

        Integer periodOrder = unitOrder.get(period);
        Integer showByOrder = unitOrder.get(showBy);
        if (showByOrder == null || showByOrder > periodOrder) {
            throw new CustomException(LogErrorCode.INVALID_SHOW_BY);
        }

        RequestLog.StoreViewLogStatFilter filter = new RequestLog.StoreViewLogStatFilter(storeId, startDate, now, gender, ageGroup, groupBy, showBy);
        return storeViewLogRepository.getStoreViewLogStats(filter);
    }

    public void createOwnershipLog(Long storeId) {
        ownershipLogRepository.save(OwnershipLog.builder().storeId(storeId).build());
    }

    public List<ResponseLog.OwnershipStatListDto> getOwnershipStats(String period, String showBy) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = switch (period) {
            case "day" -> now;
            case "week" -> now.minusDays(6);
            case "month" -> now.minusDays(29);
            case "year" -> now.minusDays(364);
            default -> throw new CustomException(LogErrorCode.INVALID_PERIOD);
        };

        Map<String, Integer> unitOrder = Map.of(
                "day", 1,
                "week", 2,
                "month", 3,
                "year", 4
        );

        Integer periodOrder = unitOrder.get(period);
        Integer showByOrder = unitOrder.get(showBy);
        if (showByOrder == null || showByOrder > periodOrder) {
            throw new CustomException(LogErrorCode.INVALID_SHOW_BY);
        }

        RequestLog.OwnershipLogStatFilter filter = new RequestLog.OwnershipLogStatFilter(startDate, now, showBy);
        return ownershipLogRepository.getOwnershipStats(filter);
    }
}
