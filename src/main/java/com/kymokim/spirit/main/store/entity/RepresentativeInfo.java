package com.kymokim.spirit.main.store.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class RepresentativeInfo {
    @Column(name = "name")
    private String name;

    @Column(name = "is_main_rep")
    private Boolean isMainRep;

    @Builder
    public RepresentativeInfo(String name, Boolean isMainRep){
        this.name = name;
        this.isMainRep = isMainRep;
    }
}
