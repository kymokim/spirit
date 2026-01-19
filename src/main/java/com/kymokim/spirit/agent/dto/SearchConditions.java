package com.kymokim.spirit.agent.dto;

import com.kymokim.spirit.drink.entity.DrinkType;
import com.kymokim.spirit.store.dto.FacilitiesCondition;
import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.Mood;
import lombok.*;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchConditions {
    // 카테고리
    private Set<Category> categories;
    // 분위기
    private Set<Mood> moods;
    // 방문 예정 시간
    private LocalDateTime arrivalTime;
    // 편의시설 여부
    private Boolean hasScreen;
    private Boolean hasRoom;
    private Boolean hasOutdoor;
    private Boolean isGroupAvailable;
    private Boolean isParkingAvailable;
    private Boolean isCorkageAvailable;
    // 주종
    private DrinkType drinkType;
    // 주종 가격 정렬 방식
    private Sort.Direction drinkPriceOrder;
    // 검색 단어(검색 대상: 매장 이름, 매장 메뉴 이름)
    private String searchKeyword;
    // 위치 정보
    private Double latitude;
    private Double longitude;
    private Double radius;

    public FacilitiesCondition toFacilitiesCondition() {
        return FacilitiesCondition.builder()
                .hasScreen(this.hasScreen)
                .hasRoom(this.hasRoom)
                .hasOutdoor(this.hasOutdoor)
                .isGroupAvailable(this.isGroupAvailable)
                .isParkingAvailable(this.isParkingAvailable)
                .isCorkageAvailable(this.isCorkageAvailable)
                .build();
    }

    public LocationCriteria toLocationCriteria() {
        return LocationCriteria.builder()
                .latitude(this.latitude)
                .longitude(this.longitude)
                .radius(this.radius)
                .build();
    }
}
