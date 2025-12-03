# Étape 1 : Build avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -e -X clean package -DskipTests -Dmaven.wagon.http.retryHandler.count=10

# Étape 2 : Runtime léger
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "app.jar"]
ENV SPRING_DATASOURCE_URL=none
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=none
