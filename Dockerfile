# Multi-stage build para optimizar el tamaño de la imagen
FROM eclipse-temurin:17.0.15_6-jdk-alpine AS builder

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./

# Hacer ejecutable el wrapper de Gradle
RUN chmod +x ./gradlew

# Copiar código fuente
COPY src/ src/

# Construir la aplicación
RUN ./gradlew build

# Imagen final
FROM eclipse-temurin:17.0.15_6-jre-alpine

# Crear usuario no-root para seguridad
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR desde el stage de construcción
COPY --from=builder /app/build/libs/*.jar app.jar

# Cambiar propietario del archivo
RUN chown spring:spring app.jar

# Cambiar a usuario no-root
USER spring

# Exponer puerto
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
