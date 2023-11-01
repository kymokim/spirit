package com.example.Fooding.menu.repository;

import com.example.Fooding.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllBy();
    //List<Menu> findAllByStoreId(Long storeId);
}
