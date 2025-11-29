package com.kymokim.spirit.store.service;

import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.drink.entity.Drink;
import com.kymokim.spirit.drink.entity.DrinkType;
import com.kymokim.spirit.drink.repository.DrinkRepository;
import com.kymokim.spirit.store.entity.MainDrink;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@MainTransactional
public class MainDrinkSynchronizer {

    private final DrinkRepository drinkRepository;
    private final StoreRepository storeRepository;

    public void synchronize(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
        synchronize(store);
    }

    private void synchronize(Store store) {
        Map<DrinkType, Long> minPriceByType = calculateMinPriceByType(store.getId());
        if (store.getMainDrinks() == null) {
            store.setMainDrinks(new java.util.HashSet<>());
        }
        Set<MainDrink> mainDrinks = store.getMainDrinks();

        for (MainDrink mainDrink : mainDrinks) {
            DrinkType drinkType = mainDrink.getType();
            if (!minPriceByType.containsKey(drinkType)) {
                mainDrink.updatePrice(null);
                continue;
            }
            Long newPrice = minPriceByType.get(drinkType);
            if (!Objects.equals(mainDrink.getPrice(), newPrice)) {
                mainDrink.updatePrice(newPrice);
            }
            minPriceByType.remove(drinkType);
        }

        for (Map.Entry<DrinkType, Long> entry : minPriceByType.entrySet()) {
            mainDrinks.add(MainDrink.builder()
                    .type(entry.getKey())
                    .price(entry.getValue())
                    .isVisible(Boolean.FALSE)
                    .build());
        }

        storeRepository.save(store);
    }

    private Map<DrinkType, Long> calculateMinPriceByType(Long storeId) {
        List<Drink> drinks = drinkRepository.findAllByStoreId(storeId);
        Map<DrinkType, Long> minPriceByType = new EnumMap<>(DrinkType.class);
        for (Drink drink : drinks) {
            if (drink.getType() == null) {
                continue;
            }
            Long parsedPrice = parsePrice(drink.getPrice());
            if (parsedPrice == null) {
                continue;
            }
            minPriceByType.merge(drink.getType(), parsedPrice, Math::min);
        }
        return minPriceByType;
    }

    private Long parsePrice(String rawPrice) {
        if (rawPrice == null || rawPrice.isBlank()) {
            return null;
        }
        String numeric = rawPrice.replaceAll("[^0-9]", "");
        if (numeric.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(numeric);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
