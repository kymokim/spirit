package com.kymokim.spirit.event.service;

import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.common.service.TransactionRetryUtil;
import com.kymokim.spirit.event.dto.RequestEvent;
import com.kymokim.spirit.event.dto.ResponseEvent;
import com.kymokim.spirit.event.entity.Event;
import com.kymokim.spirit.event.exception.EventErrorCode;
import com.kymokim.spirit.event.repository.EventRepository;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.StoreRepository;
import com.kymokim.spirit.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@MainTransactional
public class EventService {
    private final EventRepository eventRepository;
    private final StoreRepository storeRepository;
    private final StoreService storeService;
    private final S3Service s3Service;

    private Store resolveStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private Event resolveEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_NOT_FOUND));
    }

    private void validatePeriod(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt.isEqual(endAt) || startAt.isAfter(endAt)) {
            throw new CustomException(EventErrorCode.INVALID_EVENT_PERIOD);
        }
    }

    public void createEvent(MultipartFile file, RequestEvent.CreateEventDto createEventDto) {
        Store store = resolveStore(createEventDto.getStoreId());
        storeService.validateStoreAccess(store.getId());
        validatePeriod(createEventDto.getStartAt(), createEventDto.getEndAt());

        Event event = createEventDto.toEntity(store, AuthResolver.resolveUserId());
        eventRepository.save(event);

        if (file != null) {
            String imageUrl = s3Service.upload(file, "event/" + event.getId());
            event.setEventImageUrl(imageUrl);
        }

        store.addEventList(event);
        storeRepository.save(store);
    }

    @MainTransactional(readOnly = true)
    public ResponseEvent.GetEventDto getEvent(Long eventId) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Event event = resolveEvent(eventId);
            return ResponseEvent.GetEventDto.toDto(event);
        }, 3);
    }

    @MainTransactional(readOnly = true)
    public List<ResponseEvent.EventListDto> getEventsByStore(Long storeId) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Store store = resolveStore(storeId);
            List<ResponseEvent.EventListDto> dtoList = new ArrayList<>();
            if (!store.getEventList().isEmpty()) {
                LocalDateTime current = LocalDateTime.now();
                store.getEventList().forEach(event -> dtoList.add(ResponseEvent.EventListDto.toDto(event, current)));
            }
            return dtoList;
        }, 3);
    }

    public ResponseEvent.GetEventDto updateEvent(Long eventId, RequestEvent.UpdateEventDto updateEventDto) {
        Event event = resolveEvent(eventId);
        storeService.validateStoreAccess(event.getStore().getId());
        validatePeriod(updateEventDto.getStartAt(), updateEventDto.getEndAt());

        event.update(updateEventDto.getTitle(), updateEventDto.getContent(), updateEventDto.getStartAt(), updateEventDto.getEndAt(), AuthResolver.resolveUserId());

        return ResponseEvent.GetEventDto.toDto(eventRepository.save(event));
    }

    public void updateEventImage(MultipartFile file, Long eventId) {
        Event event = resolveEvent(eventId);
        storeService.validateStoreAccess(event.getStore().getId());
        if (file == null) {
            throw new CustomException(EventErrorCode.EVENT_IMG_FILE_EMPTY);
        }

        String imageUrl;
        if (event.getEventImageUrl() == null) {
            imageUrl = s3Service.upload(file, "event/" + event.getId());
        } else {
            imageUrl = s3Service.update(file, "event/" + event.getId(), event.getEventImageUrl());
        }
        event.setEventImageUrl(imageUrl);
        event.getHistoryInfo().update(AuthResolver.resolveUserId());
        eventRepository.save(event);
    }

    public void deleteEventImage(Long eventId) {
        Event event = resolveEvent(eventId);
        storeService.validateStoreAccess(event.getStore().getId());
        if (event.getEventImageUrl() == null || event.getEventImageUrl().isEmpty()) {
            throw new CustomException(EventErrorCode.EVENT_ORIGIN_IMG_URL_EMPTY);
        }
        s3Service.deleteFile(event.getEventImageUrl());
        event.setEventImageUrl(null);
        eventRepository.save(event);
    }

    public void deleteEvent(Long eventId) {
        Event event = resolveEvent(eventId);
        Store store = event.getStore();
        storeService.validateStoreAccess(store.getId());
        if (event.getEventImageUrl() != null && !event.getEventImageUrl().isEmpty()) {
            s3Service.deleteFile(event.getEventImageUrl());
        }
        store.removeEventList(event);
        eventRepository.delete(event);
        storeRepository.save(store);
    }
}
