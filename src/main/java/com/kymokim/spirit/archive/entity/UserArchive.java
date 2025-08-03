package com.kymokim.spirit.archive.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "user_archive")
@Entity
@Getter
@NoArgsConstructor
@Data
public class UserArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_id")
    private Long originalId;

    @Column(name = "ci")
    private String ci;

    @Column(name = "type")
    private ArchiveType type;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Builder
    public UserArchive(Long originalId, String ci, ArchiveType type, LocalDateTime expirationDate){
        this.originalId = originalId;
        this.ci = ci;
        this.type = type;
        this.expirationDate = expirationDate;
    }
}
