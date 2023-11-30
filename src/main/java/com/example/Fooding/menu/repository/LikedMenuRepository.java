package com.example.Fooding.menu.repository;

import com.example.Fooding.menu.entity.LikedMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikedMenuRepository extends JpaRepository<LikedMenu, Long> {
    LikedMenu findByUserIdAndMenuId(Long userId, Long menuId);
    List<LikedMenu> findAllByUserId(Long userId);
}
