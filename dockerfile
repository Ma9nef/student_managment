# ---- Stage 1: Build ----
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn -DskipTests=true clean package

# ---- Stage 2: Runtime (Distroless) ----
FROM gcr.io/distroless/java17-debian12:nonroot

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
