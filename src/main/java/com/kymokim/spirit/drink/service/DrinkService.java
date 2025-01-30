package com.kymokim.spirit.drink.service;

import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.drink.dto.RequestDrink;
import com.kymokim.spirit.drink.dto.ResponseDrink;
import com.kymokim.spirit.drink.exception.DrinkErrorCode;
import com.kymokim.spirit.drink.repository.DrinkRepository;
import com.kymokim.spirit.drink.entity.Drink;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DrinkService {
    private final DrinkRepository drinkRepository;
    private final StoreRepository storeRepository;
    private final S3Service s3Service;

    private Store resolveStore(Long storeId){
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private Drink resolveDrink(Long drinkId){
        return drinkRepository.findById(drinkId)
                .orElseThrow(() -> new CustomException(DrinkErrorCode.DRINK_NOT_FOUND));
    }

    @Transactional
    public void createDrink(MultipartFile file, RequestDrink.CreateDrinkDto createDrinkDto) {
        Store store = resolveStore(createDrinkDto.getStoreId());
        Drink drink = RequestDrink.CreateDrinkDto.toEntity(createDrinkDto, store);
        String imageUrl;
        if (file != null){
            imageUrl = s3Service.upload(file, "drink/" + String.valueOf(drink.getId()));
            drink.setImgUrl(imageUrl);
        }
        drinkRepository.save(drink);
    }

    @Transactional
    public void updateImage(MultipartFile file, Long drinkId){
        Drink drink = resolveDrink(drinkId);
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
        drinkRepository.save(drink);
    }

    @Transactional
    public void deleteImage(Long drinkId){
        Drink drink = resolveDrink(drinkId);
        String originUrl;
        if (!(drink.getImgUrl() == null) && !drink.getImgUrl().isEmpty()){
            originUrl = drink.getImgUrl();
        } else {
            throw new CustomException(DrinkErrorCode.DRINK_ORIGIN_IMG_URL_EMPTY);
        }
        s3Service.deleteFile(originUrl);
        drink.setImgUrl(null);
        drinkRepository.save(drink);
    }

    @Transactional
    public List<ResponseDrink.DrinkListDto> getByStore(Long storeId){
        List<Drink> entityList = drinkRepository.findAllByStoreId(storeId);
        List<ResponseDrink.DrinkListDto> dtoList = new ArrayList<>();
        entityList.forEach(drink -> dtoList.add(ResponseDrink.DrinkListDto.toDto(drink)));
        return dtoList;
    }

    @Transactional
    public ResponseDrink.GetDrinkDto getDrink(Long drinkId) {
        Drink drink = resolveDrink(drinkId);
        return ResponseDrink.GetDrinkDto.toDto(drink);
    }

    @Transactional
    public void updateDrink(Long drinkId, RequestDrink.UpdateDrinkDto updateDrinkDto) {
        Drink originalDrink = resolveDrink(drinkId);
        Drink updatedDrink = RequestDrink.UpdateDrinkDto.toEntity(originalDrink, updateDrinkDto);
        drinkRepository.save(updatedDrink);
    }

    @Transactional
    public void deleteDrink(Long drinkId) {
        Drink drink = resolveDrink(drinkId);
        drinkRepository.delete(drink);
    }
}
