package com.waveheaven.back.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private int productCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
