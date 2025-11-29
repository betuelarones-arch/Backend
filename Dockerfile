FROM eclipse-temurin:21-jdk-alpine AS build

# Configurar directorio de trabajo
WORKDIR /app

# Instalar Maven
RUN apk add --no-cache maven

# Copiar solo pom.xml primero para aprovechar cache de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación (sin tests para build más rápido)
RUN mvn clean package -DskipTests -B

# Etapa de producción - imagen más pequeña
FROM eclipse-temurin:21-jre-alpine

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Configurar directorio de trabajo
WORKDIR /app

# Crear directorio para uploads con permisos
RUN mkdir -p /app/uploads/profile-photos && \
    chown -R spring:spring /app

# Copiar JAR desde etapa de build
COPY --from=build /app/target/cliente_app-0.0.1-SNAPSHOT.jar app.jar

# Cambiar al usuario no-root
USER spring:spring

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Variables de entorno por defecto (pueden ser sobrescritas)
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE=prod

# Ejecutar aplicación con configuración optimizada
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
