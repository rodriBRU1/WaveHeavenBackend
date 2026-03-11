package com.waveheaven.back.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Async
    public void sendRegistrationConfirmation(String to, String firstName, String lastName) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("lastName", lastName);
            context.setVariable("email", to);
            context.setVariable("loginUrl", frontendUrl + "/login");
            context.setVariable("frontendUrl", frontendUrl);

            String htmlContent = templateEngine.process("email/registration-confirmation", context);

            sendHtmlEmail(to, "¡Bienvenido a WaveHeaven!", htmlContent);

            log.info("Email de confirmación de registro enviado a: {}", to);
        } catch (Exception e) {
            log.error("Error al enviar email de confirmación de registro a {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendReservationConfirmation(String to, String userFullName, Long reservationId,
                                            String productName, String productImageUrl,
                                            LocalDate startDate, LocalDate endDate) {
        try {
            Context context = new Context();
            context.setVariable("userFullName", userFullName);
            context.setVariable("reservationId", reservationId);
            context.setVariable("productName", productName);
            context.setVariable("productImageUrl", productImageUrl);
            context.setVariable("startDate", startDate.format(DATE_FORMATTER));
            context.setVariable("endDate", endDate.format(DATE_FORMATTER));
            context.setVariable("reservationsUrl", frontendUrl + "/my-reservations");
            context.setVariable("frontendUrl", frontendUrl);

            String htmlContent = templateEngine.process("email/reservation-confirmation", context);

            sendHtmlEmail(to, "Confirmación de Reserva - WaveHeaven", htmlContent);

            log.info("Email de confirmación de reserva enviado a: {} para reserva #{}", to, reservationId);
        } catch (Exception e) {
            log.error("Error al enviar email de confirmación de reserva a {}: {}", to, e.getMessage());
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
