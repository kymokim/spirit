package com.kymokim.spirit.event.dto;

import com.kymokim.spirit.event.entity.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ResponseEvent {

    @Builder
    @Getter
    public static class GetEventDto {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private Long storeId;
        private String eventImageUrl;

        public static GetEventDto toDto(Event event) {
            return GetEventDto.builder()
                    .id(event.getId())
                    .title(event.getTitle())
                    .content(event.getContent())
                    .startAt(event.getStartAt())
                    .endAt(event.getEndAt())
                    .storeId(event.getStore().getId())
                    .eventImageUrl(event.getEventImageUrl())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class EventListDto {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private Boolean isOngoing;
        private String eventImageUrl;

        public static EventListDto toDto(Event event, LocalDateTime current) {
            return EventListDto.builder()
                    .id(event.getId())
                    .title(event.getTitle())
                    .content(event.getContent())
                    .startAt(event.getStartAt())
                    .endAt(event.getEndAt())
                    .isOngoing(current.isAfter(event.getStartAt()) && current.isBefore(event.getEndAt()))
                    .eventImageUrl(event.getEventImageUrl())
                    .build();
        }
    }
}
