package com.kymokim.spirit.menu.repository;

import com.kymokim.spirit.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MenuRepository extends JpaRepository<Menu, Long> {
}
