package com.waveheaven.back.characteristics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.waveheaven.back.products.entity.Product;
import com.waveheaven.back.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "characteristics")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Characteristic extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @ManyToMany(mappedBy = "characteristics")
    @Builder.Default
    @JsonIgnore
    private List<Product> products = new ArrayList<>();
}
