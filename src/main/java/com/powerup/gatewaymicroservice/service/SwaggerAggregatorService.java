package com.powerup.gatewaymicroservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Servicio para agregar documentación Swagger de microservicios.
 * Siguiendo principio KISS - implementación simple y directa.
 */
@Service
public class SwaggerAggregatorService {

    private final WebClient webClient;

    public SwaggerAggregatorService() {
        this.webClient = WebClient.create();
    }

    /**
     * Obtiene documentación de Property Microservice
     */
    public Mono<String> getPropertyMicroserviceSwagger() {
        return getMicroserviceSwagger(8081, "Property Microservice", "API para gestión de propiedades, categorías y ubicaciones");
    }

    /**
     * Obtiene documentación de User Microservice
     */
    public Mono<String> getUserMicroserviceSwagger() {
        return getMicroserviceSwagger(8082, "User Microservice", "API para gestión de usuarios y autenticación");
    }

    /**
     * Obtiene documentación de Visit Microservice
     */
    public Mono<String> getVisitMicroserviceSwagger() {
        return getMicroserviceSwagger(8083, "Visit Microservice", "API para gestión de citas y visitas");
    }

    /**
     * Método genérico para obtener swagger de cualquier microservicio
     */
    private Mono<String> getMicroserviceSwagger(int port, String title, String description) {
        return getSwaggerFromUrl("http://localhost:" + port + "/v3/api-docs")
                .doOnError(error -> System.err.println("Error obteniendo swagger de " + title + ": " + error.getMessage()))
                .onErrorReturn(getDefaultSwagger(title, description));
    }

    /**
     * Obtiene swagger desde URL específica con timeout
     */
    private Mono<String> getSwaggerFromUrl(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5));
    }

    /**
     * Swagger por defecto cuando el microservicio no está disponible
     */
    private String getDefaultSwagger(String title, String description) {
        return String.format("""
            {
              "openapi": "3.0.1",
              "info": {
                "title": "%s",
                "description": "%s - Microservicio no disponible actualmente",
                "version": "v1"
              },
              "servers": [
                {
                  "url": "http://localhost:8080",
                  "description": "Gateway Server"
                }
              ],
              "paths": {},
              "components": {}
            }
            """, title, description);
    }
}
