package com.kymokim.spirit.drink.repository;

import com.kymokim.spirit.drink.entity.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface DrinkRepository extends JpaRepository<Drink, Long> {
    List<Drink> findAllByStoreId(Long storeId);
    @Query("SELECT MAX(d.sortOrder) FROM Drink d WHERE d.store.id = :storeId")
    Optional<Integer> findMaxSortOrderByStoreId(@Param("storeId") Long storeId);
}
