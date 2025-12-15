package com.kymokim.spirit.common.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.common.dto.ResponseLocationDto;
import com.kymokim.spirit.common.service.LocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Location API")
@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    @Autowired
    private final LocationService locationService;

    @GetMapping("/get-address")
    public ResponseEntity<ResponseDto> getAddress(@RequestParam double latitude, @RequestParam double longitude) {
        ResponseLocationDto.GetAddressDto response = locationService.getAddress(latitude, longitude);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Address retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-coordinate")
    public ResponseEntity<ResponseDto> getCoordinate(@RequestParam String address) {
        ResponseLocationDto.GetCoordinateDto response = locationService.getCoordinate(address);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Coordinate retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-road")
    public ResponseEntity<ResponseDto> getRoadAddressAndCoordinate(@RequestParam double latitude, @RequestParam double longitude) {
        ResponseLocationDto.GetRoadAddressAndCoordinateDto response = locationService.getRoadAddressAndCoordinate(latitude, longitude);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Road address retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-init-road")
    public ResponseEntity<ResponseDto> getInitRoadAddressAndCoordinate() {
        ResponseLocationDto.GetRoadAddressAndCoordinateDto response = locationService.getInitRoadAddressAndCoordinate();
        ResponseDto responseDto = ResponseDto.builder()
                .message("Init road address retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
