package com.waveheaven.back.characteristics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacteristicResponse {

    private Long id;
    private String name;
    private String iconUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
