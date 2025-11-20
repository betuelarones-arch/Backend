FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Instalar Maven
RUN apk add --no-cache maven

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/cliente_app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
