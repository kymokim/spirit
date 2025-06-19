package com.kymokim.spirit.menu.repository;

import com.kymokim.spirit.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface MenuRepository extends JpaRepository<Menu, Long> {
    @Query("SELECT MAX(m.sortOrder) FROM Menu m WHERE m.store.id = :storeId")
    Optional<Integer> findMaxSortOrderByStoreId(@Param("storeId") Long storeId);

}
