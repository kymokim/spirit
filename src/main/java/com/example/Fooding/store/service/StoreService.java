package com.example.Fooding.store.service;

import com.example.Fooding.store.dto.RequestStore;
import com.example.Fooding.store.dto.ResponseStore;
import com.example.Fooding.store.entity.Store;
import com.example.Fooding.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class StoreService {
    private final StoreRepository storeRepository;

    // 가게 생성
    public void createStore(RequestStore.CreateStoreDto createStoreDto) {
        Store store = RequestStore.CreateStoreDto.toEntity(createStoreDto);
        storeRepository.save(store);
    }

    public List<ResponseStore.GetStoreDto> getStore() {
        List<Store> tasks = storeRepository.findAll();
        List<ResponseStore.GetStoreDto> list = new ArrayList<>();
        tasks.stream().forEach(task -> list.add(ResponseStore.GetStoreDto.toDto(task)));
        return list;
    }


    public ResponseStore.GetReadStoreDto getReadStore(Long id) {
        Store store = storeRepository.findById(id).get();
        return ResponseStore.GetReadStoreDto.toDto(store);
    }



    // 가게 수정(수정은 response를 안 받나??)
    public void updateStore(RequestStore.UpdateStoreDto updateStoreDto) {
        Store originalStore = storeRepository.findById(updateStoreDto.getStoreId()).get();
        Store updatedStore = RequestStore.UpdateStoreDto.toEntity(originalStore, updateStoreDto);
        storeRepository.save(updatedStore);
    }

    public void deleteStore(Long storeId) {
        Store store = storeRepository.findById(storeId).get();
        storeRepository.delete(store);
    }



}
