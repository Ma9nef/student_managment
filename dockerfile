# ---- Stage 1: Build ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src src
RUN mvn clean package -DskipTests -B

# ---- Stage 2: Production Runtime ----
FROM gcr.io/distroless/java17-debian12:nonroot

WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8089

# Add a production health check endpoint
# (Spring Boot Actuator must be enabled)
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -qO- http://localhost:8089/actuator/health || exit 1

# Run using non-root user for security
USER nonroot

ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]
