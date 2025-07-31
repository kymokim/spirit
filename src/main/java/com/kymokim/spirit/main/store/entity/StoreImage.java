package com.kymokim.spirit.main.store.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="store_image")
@Entity
@Getter
@NoArgsConstructor
@Data
public class StoreImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public StoreImage(String url, Store store, Integer sortOrder) {
        this.url = url;
        this.store = store;
        this.sortOrder = sortOrder;
    }
}
