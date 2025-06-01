package com.powerup.gatewaymicroservice.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuración Swagger siguiendo principio KISS.
 * Configuración mínima - SpringDoc maneja automáticamente los endpoints de SwaggerController.
 */
@Configuration
public class SwaggerConfig {
    // Configuración automática - SpringDoc detecta automáticamente los endpoints
    // Los endpoints /v3/api-docs/property, /user, /visit se registran automáticamente
}
