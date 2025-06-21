package com.kymokim.spirit.drink.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.drink.dto.RequestDrink;
import com.kymokim.spirit.drink.dto.ResponseDrink;
import com.kymokim.spirit.drink.service.DrinkService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Drink API")
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drink")
public class DrinkController {
    @Autowired
    private final DrinkService drinkService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createDrink(@RequestPart(value = "file", required = false) MultipartFile file,
                                                  @RequestPart(value = "createDrinkDto") RequestDrink.CreateDrinkDto createDrinkDto) {
        drinkService.createDrink(file, createDrinkDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Drink created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/update-image/{drinkId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> updateDrinkImage(@RequestPart(value = "file", required = true) MultipartFile file,
                                                       @PathVariable("drinkId") Long drinkId){
        drinkService.updateImage(file, drinkId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("delete-image/{drinkId}")
    public ResponseEntity<ResponseDto> deleteImage(@PathVariable("drinkId") Long drinkId){
        drinkService.deleteImage(drinkId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/store/{storeId}")
    public ResponseEntity<ResponseDto> getByStore(@PathVariable("storeId") Long storeId) {
        List<ResponseDrink.DrinkListDto> response = drinkService.getByStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Drink retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/{drinkId}")
    public ResponseEntity<ResponseDto> getDrink(@PathVariable("drinkId") Long drinkId) {
        ResponseDrink.GetDrinkDto response = drinkService.getDrink(drinkId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Drink retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update/{drinkId}")
    public ResponseEntity<ResponseDto> updateDrink(@PathVariable("drinkId") Long drinkId, @RequestBody RequestDrink.UpdateDrinkDto updateDrinkDto) {
        drinkService.updateDrink(drinkId, updateDrinkDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Drink updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update/sort-order")
    public ResponseEntity<ResponseDto> updateDrinkSortOrder(@RequestBody RequestDrink.UpdateDrinkSortOrderDto updateDrinkSortOrderDto) {
        drinkService.updateDrinkSortOrder(updateDrinkSortOrderDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Drink sort order updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{drinkId}")
    public ResponseEntity<ResponseDto> deleteDrink(@PathVariable("drinkId") Long drinkId) {
        drinkService.deleteDrink(drinkId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Drink deleted successfully.")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
