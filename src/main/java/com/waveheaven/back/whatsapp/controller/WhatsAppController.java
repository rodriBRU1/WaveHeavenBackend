package com.waveheaven.back.whatsapp.controller;

import com.waveheaven.back.whatsapp.dto.WhatsAppMessageRequest;
import com.waveheaven.back.whatsapp.dto.WhatsAppMessageResponse;
import com.waveheaven.back.whatsapp.service.WhatsAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
@Tag(name = "WhatsApp", description = "WhatsApp messaging endpoints")
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    @PostMapping("/send")
    @Operation(summary = "Send WhatsApp message", description = "Send a message to the provider via WhatsApp")
    public ResponseEntity<WhatsAppMessageResponse> sendMessage(
            @Valid @RequestBody WhatsAppMessageRequest request) {
        WhatsAppMessageResponse response = whatsAppService.sendMessage(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Check WhatsApp configuration status", description = "Check if WhatsApp service is properly configured")
    public ResponseEntity<Map<String, Object>> getStatus() {
        boolean configured = whatsAppService.isConfigured();
        return ResponseEntity.ok(Map.of(
                "configured", configured,
                "message", configured
                        ? "WhatsApp service is configured and ready"
                        : "WhatsApp service is not configured. Please set Twilio credentials."
        ));
    }
}
