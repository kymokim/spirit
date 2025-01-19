package com.kymokim.spirit.store.entity;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public StoreImage(String url, Store store) {
        this.url = url;
        this.store = store;
    }
}
