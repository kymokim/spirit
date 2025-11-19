package com.kymokim.spirit.event.dto;

import com.kymokim.spirit.event.entity.Event;
import com.kymokim.spirit.store.entity.Store;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class RequestEvent {

    @Data
    @Builder
    public static class CreateEventDto {
        @NotEmpty
        private String title;
        @NotEmpty
        private String content;
        @NotNull
        private LocalDateTime startAt;
        @NotNull
        private LocalDateTime endAt;
        @NotNull
        private Long storeId;

        public Event toEntity(Store store, Long creatorId) {
            return Event.builder()
                    .title(this.title)
                    .content(this.content)
                    .startAt(this.startAt)
                    .endAt(this.endAt)
                    .store(store)
                    .creatorId(creatorId)
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateEventDto {
        @NotEmpty
        private String title;
        @NotEmpty
        private String content;
        @NotNull
        private LocalDateTime startAt;
        @NotNull
        private LocalDateTime endAt;
    }
}
