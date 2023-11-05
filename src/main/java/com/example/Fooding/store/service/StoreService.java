package com.example.Fooding.store.service;

import com.example.Fooding.auth.repository.AuthRepository;
import com.example.Fooding.auth.security.JwtAuthToken;
import com.example.Fooding.auth.security.JwtAuthTokenProvider;
import com.example.Fooding.store.dto.RequestStore;
import com.example.Fooding.store.dto.ResponseStore;
import com.example.Fooding.store.entity.Store;
import com.example.Fooding.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class StoreService {
    private final StoreRepository storeRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;

    public void createStore(RequestStore.CreateStoreDto createStoreDto, Optional<String> token) {

        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long makerId = authRepository.findByEmail(email).getId();
        Store store = RequestStore.CreateStoreDto.toEntity(createStoreDto, makerId);
        storeRepository.save(store);
    }

    public List<ResponseStore.GetAllStoreDto> getAllStore() {
        List<Store> entityList = storeRepository.findAll();
        List<ResponseStore.GetAllStoreDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(store -> dtoList.add(ResponseStore.GetAllStoreDto.toDto(store)));
        return dtoList;
    }

    public ResponseStore.GetStoreDto getStore(Long id) {
        Store store = storeRepository.findById(id).get();
        return ResponseStore.GetStoreDto.toDto(store);
    }

    public void updateStore(RequestStore.UpdateStoreDto updateStoreDto) {
        Store originalStore = storeRepository.findById(updateStoreDto.getStoreId()).get();
        Store updatedStore = RequestStore.UpdateStoreDto.toEntity(originalStore, updateStoreDto);
        storeRepository.save(updatedStore);
    }

    //Delete permission exception handling required.
    public void deleteStore(Long storeId) {
        Store store = storeRepository.findById(storeId).get();
        storeRepository.delete(store);
    }
}
