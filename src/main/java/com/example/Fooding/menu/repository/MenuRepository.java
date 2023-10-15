package com.example.Fooding.menu.repository;

import com.example.Fooding.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MenuRepository extends JpaRepository<Menu, Long> {
}
