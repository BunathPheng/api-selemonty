# ──────────────────────────────
# 1) Build Stage
# ──────────────────────────────
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Dependency layer cached separately — only re-downloads when pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn -B package -DskipTests -q

# ──────────────────────────────
# 2) Runtime Stage
# ──────────────────────────────
# Pin to a specific digest for reproducibility and to avoid getting
# a pebble binary compiled with a vulnerable Go version (see Trivy scan).
# Update this digest periodically to pull patched base images.
# To find the latest digest:
#   docker pull eclipse-temurin:21-jre-noble
#   docker inspect eclipse-temurin:21-jre-noble --format='{{index .RepoDigests 0}}'
FROM eclipse-temurin:21-jre-noble

WORKDIR /app

# Create a non-root user for security
RUN groupadd --system appgroup && \
    useradd --system --gid appgroup --no-create-home appuser

COPY --from=build --chown=appuser:appgroup /app/target/*.jar app.jar

USER appuser

# Document the port (does not publish — use -p at runtime)
EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
