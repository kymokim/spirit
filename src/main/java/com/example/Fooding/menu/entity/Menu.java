package com.example.Fooding.menu.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "menu")
@Entity
@Getter
@NoArgsConstructor
@Data
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Name")
    private String menuName;
    @Column(name = "content")
    private String menuContent;
    @Column(name = "price")
    private Long price;

    @Builder
    public Menu(String menuName, String menuContent, Long price, Long store ) {
        this.menuName = menuName;
        this.menuContent = menuContent;
        this.price = price;
    }

    public void update(String menuName, String menuContent, Long price ) {
        this.menuName = menuName;
        this.menuContent = menuContent;
        this.price = price;
    }
}
