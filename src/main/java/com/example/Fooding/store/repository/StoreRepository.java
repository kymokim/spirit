package com.example.Fooding.store.repository;

import com.example.Fooding.menu.entity.Menu;
import com.example.Fooding.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findAllBy();
}