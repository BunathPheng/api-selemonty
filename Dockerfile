# ──────────────────────────────
# 1) Build Stage
# ──────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn -B package


# ──────────────────────────────
# 2) Runtime Stage
# ──────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Set default environment variables (can be overridden when running the container)
# ENV BASE_URL=http://34.143.146.124:8088
# ENV DB_URL=jdbc:postgresql://96.9.81.189:6438/postgres
# ENV DB_USERNAME=postgres
# ENV DB_PASSWORD=Limping5
# ENV FRONTEND_URL=http://34.142.191.106:3003
ENTRYPOINT ["java", "-jar", "app.jar"]
