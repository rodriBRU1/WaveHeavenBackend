package com.waveheaven.back.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyDTO {
    private Long id;

    @NotBlank(message = "Policy title is required")
    @Size(max = 100, message = "Policy title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Policy description is required")
    private String description;
}
