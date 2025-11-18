package com.waveheaven.back.products.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.waveheaven.back.characteristics.dto.CharacteristicResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryTitle;
    private List<CharacteristicResponse> characteristics;
    private List<ImageDTO> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
