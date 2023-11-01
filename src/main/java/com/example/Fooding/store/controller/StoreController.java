package com.example.Fooding.store.controller;

import com.example.Fooding.common.dto.ResponseDto;
import com.example.Fooding.store.dto.RequestStore;
import com.example.Fooding.store.dto.ResponseStore;
import com.example.Fooding.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createStore(@RequestBody RequestStore.CreateStoreDto createStoreDto){

        storeService.createStore(createStoreDto);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Store created successfully.")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseDto> getStore() {
        List<ResponseStore.GetStoreDto> response = storeService.getStore();

        ResponseDto responseDto = ResponseDto.builder()
                .message("success")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDto> getReadStore(@PathVariable("id") Long id) {
        ResponseStore.GetReadStoreDto response = storeService.getReadStore(id);

        ResponseDto responseDto = ResponseDto.builder()
                .message("success")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }



    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateStore(@RequestBody RequestStore.UpdateStoreDto updateStoreDto){

        storeService.updateStore(updateStoreDto);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Store updated successfully.")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{storeId}")
    public ResponseEntity<ResponseDto> deleteStore(@PathVariable("storeId") Long storeId){

        storeService.deleteStore(storeId);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu deleted successfully.")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
