package com.waveheaven.back.whatsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsAppMessageResponse {

    private String messageSid;
    private String status;
    private String to;
    private LocalDateTime sentAt;
    private boolean success;
    private String errorMessage;
}
