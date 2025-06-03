# Gateway Microservice 🚀

API Gateway para el sistema de gestión de propiedades inmobiliarias.

## Ecosistema del Proyecto

Este gateway es parte de un sistema completo de microservicios para gestión inmobiliaria:

### 🏗️ **Arquitectura del Sistema**
```
Frontend Angular ──→ Gateway (este repo) ──→ Microservicios Backend
```

### 📦 **Repositorios Relacionados**
| Componente | Repositorio | Descripción | Puerto |
|------------|-------------|-------------|--------|
| 🅰️ **Frontend** | [hogar360front](https://github.com/ejsalasdev/hogar360-frontend) | Interfaz Angular 16 + TypeScript | 4200 |
| 🚪 **Gateway** | **Este repositorio** | API Gateway centralizado | 8080 |
| 🏠 **Property Service** | [property-microservice](https://github.com/ejsalasdev/hogar360-property-microservice) | Gestión de propiedades | 8081 |
| 👤 **User Service** | [user-microservice](https://github.com/ejsalasdev/hogar360-user-microservice) | Autenticación y usuarios | 8082 |
| 📅 **Visit Service** | [visit-microservice](https://github.com/ejsalasdev/hogar360-visit-microservice) | Citas y visitas | 8083 |


## Descripción

Este gateway centraliza el acceso a tres microservicios:
- **Property Service** (puerto 8081) - Gestión de propiedades, categorías y ubicaciones
- **User Service** (puerto 8082) - Autenticación y gestión de usuarios  
- **Visit Service** (puerto 8083) - Programación de visitas a propiedades

## Tecnologías Implementadas

- ✅ **Spring Cloud Gateway** - Enrutamiento y filtrado de solicitudes
- ✅ **Spring Boot WebFlux** - Programación reactiva
- ✅ **SpringDoc OpenAPI** - Documentación Swagger centralizada
- ✅ **Spring Boot Actuator** - Endpoints de monitoreo
- ✅ **CORS Global** - Configuración cross-origin


## Inicio Rápido

### 🚀 **Ecosistema Completo**

Para levantar todo el sistema de microservicios:

1. **Orden de inicio recomendado:**
   ```bash
   # 1. Microservicios backend (en paralelo)
   cd ../property-microservice && ./gradlew bootRun &
   cd ../user-microservice && ./gradlew bootRun &
   cd ../visit-microservice && ./gradlew bootRun &
   
   # 2. Gateway (este repositorio)
   cd ../gateway-microservice && ./gradlew bootRun &
   
   # 3. Frontend Angular
   cd ../hogar360front && npm start
   ```

2. **Verificar servicios:**
   - Property: http://localhost:8081/actuator/health
   - User: http://localhost:8082/actuator/health  
   - Visit: http://localhost:8083/actuator/health
   - Gateway: http://localhost:8080/actuator/health
   - Frontend: http://localhost:4200

### 🔧 **Solo Gateway (Desarrollo Local)**

1. **Prerrequisitos**
   ```bash
   - Java 17+
   - Gradle 8+
   ```

2. **Variables de Entorno (Opcionales)**
   ```bash
   # Si no se configuran, usa valores por defecto para desarrollo local
   export PROPERTY_SERVICE_URL=http://localhost:8081
   export USER_SERVICE_URL=http://localhost:8082
   export VISIT_SERVICE_URL=http://localhost:8083
   export GATEWAY_SERVICE_URL=http://localhost:8080
   export SERVER_PORT=8080
   ```

3. **Ejecutar la aplicación**
   ```bash
   ./gradlew bootRun
   ```

4. **Acceder a los endpoints**
   - Gateway: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health Check: http://localhost:8080/actuator/health

## Configuración de Entornos

### Local (Valores por Defecto)
La aplicación funciona sin configuración adicional usando:
- Property Service: `http://localhost:8081`
- User Service: `http://localhost:8082`
- Visit Service: `http://localhost:8083`
- Gateway: `http://localhost:8080`

### Producción (Dokploy)
Configurar estas variables de entorno en Dokploy:
```bash
PROPERTY_SERVICE_URL=https://property.hogar360.site
USER_SERVICE_URL=https://user.hogar360.site
VISIT_SERVICE_URL=https://visit.hogar360.site
GATEWAY_SERVICE_URL=https://gateway.hogar360.site
SERVER_PORT=8080
```

## Enrutamiento

### Estrategia de Rutas
- **15 rutas específicas** para endpoints críticos (`/api/v1/*`)
- **3 rutas genéricas** con path stripping (`/{service}/**`)
- **3 rutas de documentación** para agregación Swagger

### Servicios Conectados
| Servicio | Puerto | Rutas Ejemplo | Descripción |
|----------|--------|---------------|-------------|
| **Property** | 8081 | `/api/v1/house/*`, `/property/**` | Propiedades, categorías, ubicaciones |
| **User** | 8082 | `/api/v1/auth/login`, `/user/**` | Autenticación y usuarios |
| **Visit** | 8083 | `/api/v1/appointmentslot/*`, `/visit/**` | Citas y visitas |

### Documentación API
- **Swagger UI Centralizado**: `/swagger-ui.html`
- **OpenAPI por servicio**: `/v3/api-docs/{service}`

## Características

### Swagger Centralizado
- **Agregación automática** de documentación de los 3 microservicios
- **Reescritura de URLs** para que apunten al Gateway
- **Fallback** con documentación por defecto si un servicio no está disponible
- **Timeout de 5 segundos** por servicio

### Configuración CORS
- Permite todos los orígenes (`*`)
- Métodos: GET, POST, PUT, DELETE, PATCH, OPTIONS
- Headers: Todos permitidos

### Monitoreo
- **Health Check**: `/actuator/health`

## Arquitectura del Sistema

### 🏗️ **Vista General**
```
┌─────────────────┐    ┌─────────────────┐
│ Frontend Angular│───▶│  Gateway:8080   │
│ (Puerto 4200)   │    │  (Este Repo)    │
└─────────────────┘    └─────────┬───────┘
                                 │
                    ┌────────────┼────────────┐
                    ▼            ▼            ▼
            ┌──────────────┐ ┌──────────┐ ┌──────────┐
            │Property:8081 │ │User:8082 │ │Visit:8083│
            │- Propiedades │ │- Auth    │ │- Citas   │
            │- Categorías  │ │- Usuarios│ │- Visitas │
            │- Ubicaciones │ └──────────┘ └──────────┘
            └──────────────┘
```

### 🔄 **Flujo de Datos**
1. **Frontend** envía peticiones al Gateway
2. **Gateway** enruta automáticamente según el path
3. **Microservicios** procesan las requests específicas
4. **Gateway** agrega respuestas y las devuelve al Frontend

## Desarrollo

### Principios Aplicados
- **KISS** (Keep It Simple, Stupid) - Implementación simple y directa
- **DRY** (Don't Repeat Yourself) - Configuración reutilizable
- **Variables de entorno** - Configuración flexible para diferentes entornos

### Git Flow
- Rama `main` - Código de producción
- Rama `develop` - Código de desarrollo
- Ramas `feature/*` - Desarrollo de funcionalidades

---

**Desarrollado con Spring Cloud Gateway, WebFlux y SpringDoc OpenAPI** • **Java 17** • **Gradle 8**
