FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build
COPY pom.xml .
RUN mvn -B install -N -DskipTests

WORKDIR /build/config-server
COPY config-server/pom.xml .
COPY config-server/src ./src

# Собираем с явным указанием на spring-boot-maven-plugin
RUN mvn -B package spring-boot:repackage -DskipTests

FROM eclipse-temurin:17-jre-jammy

LABEL org.opencontainers.image.authors="Mikhail Latypov" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.title="Config Server"

RUN mkdir -p /tmp/config-repo && \
    chmod 700 /tmp/config-repo

WORKDIR /app
RUN addgroup --system javauser && \
    adduser --system --ingroup javauser javauser && \
    chown -R javauser:javauser /app && \
    chown -R javauser:javauser /tmp/config-repo
USER javauser

COPY --from=builder --chown=javauser:javauser /build/config-server/target/config-server-*.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=50.0 \
-Dspring.cloud.config.server.git.force-pull=true"

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=5 \
    CMD curl -fsS "http://localhost:8888/actuator/health" || exit 1

EXPOSE 8888
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.jar"]