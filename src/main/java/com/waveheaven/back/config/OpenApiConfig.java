package com.waveheaven.back.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "WaveHeaven API",
                version = "1.0.0",
                description = "API REST para la plataforma WaveHeaven - Sistema de gestión de productos con autenticación JWT",
                contact = @Contact(
                        name = "WaveHeaven Team",
                        email = "contacto@waveheaven.com"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Ingresa el token JWT obtenido del endpoint /api/auth/login"
)
public class OpenApiConfig {
}
