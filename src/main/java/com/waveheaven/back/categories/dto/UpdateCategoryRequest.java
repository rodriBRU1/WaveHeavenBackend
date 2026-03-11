package com.waveheaven.back.categories.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequest {

    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    private String title;

    private String description;
    private String imageUrl;

}
