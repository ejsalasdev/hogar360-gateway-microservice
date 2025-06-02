package com.powerup.gatewaymicroservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Servicio para agregar documentación Swagger de microservicios.
 * Siguiendo principio KISS - implementación simple y directa.
 * 
 * Modifica las URLs de los servidores para que apunten al Gateway
 * en lugar de directamente a los microservicios.
 */
@Service
public class SwaggerAggregatorService {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerAggregatorService.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public SwaggerAggregatorService() {
        this.webClient = WebClient.create();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Obtiene documentación de Property Microservice
     */
    public Mono<String> getPropertyMicroserviceSwagger() {
        return getMicroserviceSwagger(8081, "property", "Property Microservice", "API para gestión de propiedades, categorías y ubicaciones");
    }

    /**
     * Obtiene documentación de User Microservice
     */
    public Mono<String> getUserMicroserviceSwagger() {
        return getMicroserviceSwagger(8082, "user", "User Microservice", "API para gestión de usuarios y autenticación");
    }

    /**
     * Obtiene documentación de Visit Microservice
     */
    public Mono<String> getVisitMicroserviceSwagger() {
        return getMicroserviceSwagger(8083, "visit", "Visit Microservice", "API para gestión de citas y visitas");
    }

    /**
     * Método genérico para obtener swagger de cualquier microservicio
     */
    private Mono<String> getMicroserviceSwagger(int port, String servicePath, String title, String description) {
        return getSwaggerFromUrl("http://localhost:" + port + "/v3/api-docs")
                .map(swaggerJson -> rewriteServerUrls(swaggerJson, servicePath))
                .doOnError(error -> logger.error("Error obteniendo swagger de {}: {}", title, error.getMessage()))
                .onErrorReturn(getDefaultSwagger(title, description));
    }

    /**
     * Reescribe las URLs de los servidores para que apunten al Gateway
     */
    private String rewriteServerUrls(String swaggerJson, String servicePath) {
        try {
            JsonNode rootNode = objectMapper.readTree(swaggerJson);
            if (rootNode instanceof ObjectNode objectNode) {
                // Crear nuevo array de servidores
                ArrayNode serversArray = objectMapper.createArrayNode();
                
                // Servidor principal del Gateway
                ObjectNode gatewayServer = objectMapper.createObjectNode();
                gatewayServer.put("url", "http://localhost:8080/" + servicePath);
                gatewayServer.put("description", "Gateway Server - " + servicePath);
                serversArray.add(gatewayServer);
                
                // Servidor alternativo sin prefijo (para rutas genéricas)
                ObjectNode altServer = objectMapper.createObjectNode();
                altServer.put("url", "http://localhost:8080/api");
                altServer.put("description", "Gateway Server - Generic Route");
                serversArray.add(altServer);
                
                // Reemplazar servers en el JSON
                objectNode.set("servers", serversArray);
                
                return objectMapper.writeValueAsString(objectNode);
            }
        } catch (Exception e) {
            logger.error("Error reescribiendo URLs del servidor: {}", e.getMessage());
        }
        return swaggerJson; // Retorna original si hay error
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
