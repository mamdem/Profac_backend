# Étape 1 : Construction du projet
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Exécution de l'application
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/target/Profac_backend-0.0.1-SNAPSHOT.jar Profac_backend.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
