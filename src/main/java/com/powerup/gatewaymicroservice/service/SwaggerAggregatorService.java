package com.powerup.gatewaymicroservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class SwaggerAggregatorService {

  private static final Logger logger = LoggerFactory.getLogger(SwaggerAggregatorService.class);
  private final WebClient webClient;
  private final ObjectMapper objectMapper;

  @Value("${microservices.property.url}")
  private String propertyServiceUrl;

  @Value("${microservices.user.url}")
  private String userServiceUrl;

  @Value("${microservices.visit.url}")
  private String visitServiceUrl;

  @Value("${microservices.gateway.url}")
  private String gatewayServiceUrl;

  public SwaggerAggregatorService() {
    this.webClient = WebClient.create();
    this.objectMapper = new ObjectMapper();
  }

  public Mono<String> getPropertyMicroserviceSwagger() {
    return getMicroserviceSwagger(propertyServiceUrl, "property", "Property Microservice",
        "API para gestión de propiedades, categorías y ubicaciones");
  }

  public Mono<String> getUserMicroserviceSwagger() {
    return getMicroserviceSwagger(userServiceUrl, "user", "User Microservice",
        "API para gestión de usuarios y autenticación");
  }

  public Mono<String> getVisitMicroserviceSwagger() {
    return getMicroserviceSwagger(visitServiceUrl, "visit", "Visit Microservice",
        "API para gestión de citas y visitas");
  }

  private Mono<String> getMicroserviceSwagger(String serviceUrl, String servicePath, String title, String description) {
    return getSwaggerFromUrl(serviceUrl + "/v3/api-docs")
        .map(swaggerJson -> rewriteServerUrls(swaggerJson, servicePath))
        .doOnError(error -> logger.error("Error obteniendo swagger de {}: {}", title, error.getMessage()))
        .onErrorReturn(getDefaultSwagger(title, description));
  }

  private String rewriteServerUrls(String swaggerJson, String servicePath) {
    try {
      JsonNode rootNode = objectMapper.readTree(swaggerJson);
      if (rootNode instanceof ObjectNode objectNode) {
        // Crear nuevo array de servidores
        ArrayNode serversArray = objectMapper.createArrayNode();

        // Servidor principal del Gateway
        ObjectNode gatewayServer = objectMapper.createObjectNode();
        gatewayServer.put("url", gatewayServiceUrl + "/" + servicePath);
        gatewayServer.put("description", "Gateway Server - " + servicePath);
        serversArray.add(gatewayServer);

        // Servidor alternativo sin prefijo (para rutas genéricas)
        ObjectNode altServer = objectMapper.createObjectNode();
        altServer.put("url", gatewayServiceUrl + "/api");
        altServer.put("description", "Gateway Server - Generic Route");
        serversArray.add(altServer);

        // Reemplazar servers en el JSON
        objectNode.set("servers", serversArray);

        return objectMapper.writeValueAsString(objectNode);
      }
    } catch (Exception e) {
      logger.error("Error reescribiendo URLs del servidor: {}", e.getMessage());
    }
    return swaggerJson;
  }

  private Mono<String> getSwaggerFromUrl(String url) {
    return webClient.get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .timeout(Duration.ofSeconds(5));
  }

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
              "url": "%s",
              "description": "Gateway Server"
            }
          ],
          "paths": {},
          "components": {}
        }
        """, title, description, gatewayServiceUrl);
  }
}
