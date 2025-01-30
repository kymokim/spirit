package com.kymokim.spirit.drink.repository;

import com.kymokim.spirit.drink.entity.Drink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DrinkRepository extends JpaRepository<Drink, Long> {
    List<Drink> findAllByStoreId(Long storeId);
}
