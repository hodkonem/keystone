# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline -N  # Кэшируем зависимости родительского POM

WORKDIR /build/auth-service
COPY auth-service/pom.xml .
RUN mvn -B dependency:go-offline     # Кэшируем зависимости модуля
COPY auth-service/src ./src
RUN mvn -B clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
RUN addgroup --system javauser && \
    adduser --system --ingroup javauser javauser && \
    chown -R javauser:javauser /app
USER javauser

COPY --from=builder --chown=javauser:javauser /build/auth-service/target/auth-service.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=50.0 -Djava.security.egd=file:/dev/./urandom"
ENV SERVER_PORT=8080

HEALTHCHECK --interval=30s --timeout=5s \
  CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

EXPOSE ${SERVER_PORT}
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.jar"]