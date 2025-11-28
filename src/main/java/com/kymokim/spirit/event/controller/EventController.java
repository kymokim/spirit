package com.kymokim.spirit.event.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.event.dto.RequestEvent;
import com.kymokim.spirit.event.dto.ResponseEvent;
import com.kymokim.spirit.event.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Event API")
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createEvent(@RequestPart(value = "file", required = false) MultipartFile file,
                                                   @Valid @RequestPart(value = "createEventDto") RequestEvent.CreateEventDto createEventDto) {
        eventService.createEvent(file, createEventDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Event created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/update-image/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> updateEventImage(@RequestPart(value = "file", required = true) MultipartFile file,
                                                        @PathVariable("eventId") Long eventId) {
        eventService.updateEventImage(file, eventId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Event image updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete-image/{eventId}")
    public ResponseEntity<ResponseDto> deleteEventImage(@PathVariable("eventId") Long eventId) {
        eventService.deleteEventImage(eventId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Event image deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/{eventId}")
    public ResponseEntity<ResponseDto> getEvent(@PathVariable("eventId") Long eventId) {
        ResponseEvent.GetEventDto response = eventService.getEvent(eventId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Event retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/store/{storeId}")
    public ResponseEntity<ResponseDto> getEventByStore(@PathVariable("storeId") Long storeId) {
        List<ResponseEvent.EventListDto> response = eventService.getEventsByStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store event list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update/{eventId}")
    public ResponseEntity<ResponseDto> updateEvent(@PathVariable("eventId") Long eventId,
                                                   @Valid @RequestBody RequestEvent.UpdateEventDto updateEventDto) {
        ResponseEvent.GetEventDto response = eventService.updateEvent(eventId, updateEventDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Event updated successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<ResponseDto> deleteEvent(@PathVariable("eventId") Long eventId) {
        eventService.deleteEvent(eventId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Event deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
