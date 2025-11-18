package com.waveheaven.back.products.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {

    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;

    private String description;

    private Long categoryId;

    private List<Long> characteristicIds;

    @Valid
    private List<ImageDTO> images;
}
