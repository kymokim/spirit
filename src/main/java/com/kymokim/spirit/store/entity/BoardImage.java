package com.kymokim.spirit.store.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="board_image")
@Entity
@Getter
@NoArgsConstructor
@Data
public class BoardImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_type")
    private BoardType boardType;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public BoardImage(String url, Store store, Integer sortOrder, BoardType boardType) {
        this.url = url;
        this.store = store;
        this.sortOrder = sortOrder;
        this.boardType = boardType;
    }
}
