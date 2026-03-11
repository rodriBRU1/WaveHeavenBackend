package com.waveheaven.back.favorites.dto;

import com.waveheaven.back.products.dto.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponse {
    private Long id;
    private ProductResponse product;
    private LocalDateTime createdAt;
}
