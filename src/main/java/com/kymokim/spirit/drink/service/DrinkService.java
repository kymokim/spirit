package com.kymokim.spirit.drink.service;

import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.common.service.TransactionRetryUtil;
import com.kymokim.spirit.drink.dto.RequestDrink;
import com.kymokim.spirit.drink.dto.ResponseDrink;
import com.kymokim.spirit.drink.exception.DrinkErrorCode;
import com.kymokim.spirit.drink.repository.DrinkRepository;
import com.kymokim.spirit.drink.entity.Drink;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.StoreRepository;
import com.kymokim.spirit.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@MainTransactional
public class DrinkService {
    private final DrinkRepository drinkRepository;
    private final StoreRepository storeRepository;
    private final S3Service s3Service;
    private final StoreService storeService;

    private Store resolveStore(Long storeId){
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private Drink resolveDrink(Long drinkId){
        return drinkRepository.findById(drinkId)
                .orElseThrow(() -> new CustomException(DrinkErrorCode.DRINK_NOT_FOUND));
    }

    public void createDrink(MultipartFile file, RequestDrink.CreateDrinkDto createDrinkDto) {
        Store store = resolveStore(createDrinkDto.getStoreId());
        storeService.validateStoreAccess(store.getId());
        Integer maxOrder = drinkRepository.findMaxSortOrderByStoreId(store.getId()).orElse(-1);
        Drink drink = createDrinkDto.toEntity(store, maxOrder + 1, AuthResolver.resolveUserId());
        String imageUrl;
        if (file != null){
            imageUrl = s3Service.upload(file, "drink/" + String.valueOf(drink.getId()));
            drink.setImgUrl(imageUrl);
        }
        drinkRepository.save(drink);
        store.addDrinkList(drink);
        storeRepository.save(store);
    }

    public void updateImage(MultipartFile file, Long drinkId){
        Drink drink = resolveDrink(drinkId);
        storeService.validateStoreAccess(drink.getStore().getId());
        String imageUrl;
        if (file != null){
            if (drink.getImgUrl() == null) {
                imageUrl = s3Service.upload(file, "drink/" + String.valueOf(drink.getId()));
            }
            else {
                imageUrl = s3Service.update(file, "drink/" + String.valueOf(drink.getId()), drink.getImgUrl());
            }
        } else {
            throw new CustomException(DrinkErrorCode.DRINK_IMG_FILE_EMPTY);
        }
        drink.setImgUrl(imageUrl);
        drink.getHistoryInfo().update(AuthResolver.resolveUserId());
        drinkRepository.save(drink);
    }

    public void deleteImage(Long drinkId){
        Drink drink = resolveDrink(drinkId);
        storeService.validateStoreAccess(drink.getStore().getId());
        String originUrl;
        if (!Objects.equals(drink.getImgUrl(), null) && !drink.getImgUrl().isEmpty()){
            originUrl = drink.getImgUrl();
        } else {
            throw new CustomException(DrinkErrorCode.DRINK_ORIGIN_IMG_URL_EMPTY);
        }
        s3Service.deleteFile(originUrl);
        drink.setImgUrl(null);
        drinkRepository.save(drink);
    }

    @MainTransactional(readOnly = true)
    public List<ResponseDrink.DrinkListDto> getByStore(Long storeId){
        return TransactionRetryUtil.executeWithRetry(() -> {
            Store store = resolveStore(storeId);
            List<ResponseDrink.DrinkListDto> dtoList = new ArrayList<>();
            if (!store.getDrinkList().isEmpty())
                store.getDrinkList().forEach(drink -> dtoList.add(ResponseDrink.DrinkListDto.toDto(drink)));
            return dtoList;
        }, 3);
    }

    @MainTransactional(readOnly = true)
    public ResponseDrink.GetDrinkDto getDrink(Long drinkId) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Drink drink = resolveDrink(drinkId);
            return ResponseDrink.GetDrinkDto.toDto(drink);
        }, 3);
    }

    public void updateDrink(Long drinkId, RequestDrink.UpdateDrinkDto updateDrinkDto) {
        Drink originalDrink = resolveDrink(drinkId);
        storeService.validateStoreAccess(originalDrink.getStore().getId());
        Drink updatedDrink = updateDrinkDto.toEntity(originalDrink);
        updatedDrink.getHistoryInfo().update(AuthResolver.resolveUserId());
        drinkRepository.save(updatedDrink);
    }

    public void updateDrinkSortOrder(RequestDrink.UpdateDrinkSortOrderDto updateDrinkSortOrderDto) {
        storeService.validateStoreAccess(updateDrinkSortOrderDto.getStoreId());
        List<Long> drinkIdInOrderList = updateDrinkSortOrderDto.getDrinkIdInOrderList();
        List<Drink> drinks = drinkRepository.findAllById(drinkIdInOrderList);

        for (int i = 0; i < drinkIdInOrderList.size(); i++) {
            Long drinkId = drinkIdInOrderList.get(i);
            Drink drink = drinks.stream()
                    .filter(drink1 -> drink1.getId().equals(drinkId))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(DrinkErrorCode.DRINK_NOT_FOUND));

            if (!drink.getStore().getId().equals(updateDrinkSortOrderDto.getStoreId())) {
                throw new CustomException(DrinkErrorCode.INVALID_MENU_STORE_RELATION);
            }
            drink.setSortOrder(i);
        }
    }

    public void deleteDrink(Long drinkId) {
        Drink drink = resolveDrink(drinkId);
        Store store = resolveStore(drink.getStore().getId());
        storeService.validateStoreAccess(store.getId());
        if (!Objects.equals(drink.getImgUrl(), null) && !drink.getImgUrl().isEmpty()){
            s3Service.deleteFile(drink.getImgUrl());
        }
        store.removeDrinkList(drink);
        drinkRepository.delete(drink);
        storeRepository.save(store);
    }
}
