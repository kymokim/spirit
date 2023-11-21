package com.example.Fooding.store.controller;

import com.example.Fooding.auth.security.JwtAuthTokenProvider;
import com.example.Fooding.common.dto.ResponseDto;
import com.example.Fooding.common.dto.ResponseMessage;
import com.example.Fooding.store.dto.RequestStore;
import com.example.Fooding.store.dto.ResponseStore;
import com.example.Fooding.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createStore(@RequestBody RequestStore.CreateStoreDto createStoreDto, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        storeService.createStore(createStoreDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/uploadImg")
    public ResponseEntity<ResponseDto> uploadStoreImg(@RequestPart(value = "file", required = false) MultipartFile file,
                                                      @RequestPart(value = "uploadImgDto") RequestStore.UploadImgDto dto){
        String url = storeService.uploadImg(file, dto.getStoreId());
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image uploaded successfully.")
                .data(url)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseDto> getAllStore() {
        List<ResponseStore.GetAllStoreDto> response = storeService.getAllStore();
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDto> getStore(@PathVariable("id") Long id) {
        ResponseStore.GetStoreDto response = storeService.getStore(id);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/getByCategory/{category}")
    public ResponseEntity<ResponseDto> getStoreByCategory(@PathVariable("category") String category) {
        List<ResponseStore.GetAllStoreDto> response = storeService.getStoreByCategory(category);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store list retrieved successfully.")
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
                .message("Store deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
