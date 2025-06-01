package com.powerup.gatewaymicroservice.controller;

import com.powerup.gatewaymicroservice.service.SwaggerAggregatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Controller para agregar documentación Swagger de microservicios.
 * Siguiendo principio KISS - configuración simple y directa.
 */
@RestController
public class SwaggerController {

    private final SwaggerAggregatorService swaggerAggregatorService;

    @Autowired
    public SwaggerController(SwaggerAggregatorService swaggerAggregatorService) {
        this.swaggerAggregatorService = swaggerAggregatorService;
    }

    /**
     * Endpoint para obtener documentación del Property Microservice
     */
    @GetMapping(value = "/v3/api-docs/property", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getPropertyApiDocs() {
        return swaggerAggregatorService.getPropertyMicroserviceSwagger();
    }

    /**
     * Endpoint para obtener documentación del User Microservice
     */
    @GetMapping(value = "/v3/api-docs/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getUserApiDocs() {
        return swaggerAggregatorService.getUserMicroserviceSwagger();
    }

    /**
     * Endpoint para obtener documentación del Visit Microservice
     */
    @GetMapping(value = "/v3/api-docs/visit", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getVisitApiDocs() {
        return swaggerAggregatorService.getVisitMicroserviceSwagger();
    }
}
