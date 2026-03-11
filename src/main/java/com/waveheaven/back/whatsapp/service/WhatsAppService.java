package com.waveheaven.back.whatsapp.service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.waveheaven.back.products.entity.Product;
import com.waveheaven.back.products.repository.ProductRepository;
import com.waveheaven.back.shared.exception.ResourceNotFoundException;
import com.waveheaven.back.whatsapp.dto.WhatsAppMessageRequest;
import com.waveheaven.back.whatsapp.dto.WhatsAppMessageResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {

    private final ProductRepository productRepository;

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.whatsapp-from}")
    private String whatsappFrom;

    @Value("${twilio.whatsapp-to}")
    private String whatsappTo;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isEmpty()
            && authToken != null && !authToken.isEmpty()) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully");
        } else {
            log.warn("Twilio credentials not configured. WhatsApp messaging will not work.");
        }
    }

    public WhatsAppMessageResponse sendMessage(WhatsAppMessageRequest request) {
        try {
            // Build message with product context if provided
            String messageBody = buildMessageBody(request);

            // Send message via Twilio
            Message message = Message.creator(
                    new PhoneNumber(whatsappTo),
                    new PhoneNumber(whatsappFrom),
                    messageBody
            ).create();

            log.info("WhatsApp message sent successfully. SID: {}", message.getSid());

            return WhatsAppMessageResponse.builder()
                    .messageSid(message.getSid())
                    .status(message.getStatus().toString())
                    .to(whatsappTo)
                    .sentAt(LocalDateTime.now())
                    .success(true)
                    .build();

        } catch (ApiException e) {
            log.error("Twilio API error sending WhatsApp message: {}", e.getMessage());
            return WhatsAppMessageResponse.builder()
                    .success(false)
                    .errorMessage("Error al enviar mensaje: " + e.getMessage())
                    .sentAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("Error sending WhatsApp message: {}", e.getMessage());
            return WhatsAppMessageResponse.builder()
                    .success(false)
                    .errorMessage("Error inesperado al enviar mensaje")
                    .sentAt(LocalDateTime.now())
                    .build();
        }
    }

    private String buildMessageBody(WhatsAppMessageRequest request) {
        StringBuilder messageBuilder = new StringBuilder();

        // Add product context if productId is provided
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + request.getProductId()));

            messageBuilder.append("*Consulta sobre producto:* ")
                    .append(product.getName())
                    .append("\n\n");
        }

        messageBuilder.append(request.getMessage());

        return messageBuilder.toString();
    }

    public boolean isConfigured() {
        return accountSid != null && !accountSid.isEmpty()
                && authToken != null && !authToken.isEmpty()
                && whatsappTo != null && !whatsappTo.isEmpty();
    }
}
