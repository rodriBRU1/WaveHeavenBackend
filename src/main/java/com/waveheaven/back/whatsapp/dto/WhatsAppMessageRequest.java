package com.waveheaven.back.whatsapp.dto;

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
public class WhatsAppMessageRequest {

    @NotBlank(message = "Message is required")
    @Size(max = 1600, message = "Message cannot exceed 1600 characters")
    private String message;

    private Long productId; // Optional: to include product context
}
