package com.kymokim.spirit.main.store.entity;

import com.kymokim.spirit.auth.auth.entity.Auth;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "store_suggestion")
@Entity
@Getter
@NoArgsConstructor
@Data
public class StoreSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "suggested_at")
    private LocalDateTime suggestedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    private Long suggesterId;

    @Builder
    public StoreSuggestion(Store store, Long suggesterId) {
        this.store = store;
        this.suggesterId = suggesterId;
    }
}
