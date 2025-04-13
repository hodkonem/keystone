FROM maven:3.9.6-eclipse-temurin-17 AS builder

RUN apt-get update && apt-get install -y git && \
    mkdir -p /root/.ssh && \
    chmod 700 /root/.ssh

COPY id_rsa /root/.ssh/id_rsa
RUN chmod 600 /root/.ssh/id_rsa && \
    ssh-keyscan github.com >> /root/.ssh/known_hosts

WORKDIR /build
COPY pom.xml .
RUN mvn -B install -N -DskipTests

WORKDIR /build/config-server
COPY config-server/pom.xml .
COPY config-server/src ./src
RUN mvn -B package spring-boot:repackage -DskipTests

FROM eclipse-temurin:17-jre-jammy

LABEL org.opencontainers.image.authors="Mikhail Latypov" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.title="Config Server"

RUN mkdir -p /config-repo && \
    chmod 777 /config-repo  # Разрешаем запись

RUN apt-get update && apt-get install -y --no-install-recommends git && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
RUN addgroup --system javauser && \
    adduser --system --ingroup javauser javauser && \
    chown -R javauser:javauser /app && \
    chown -R javauser:javauser /config-repo
USER javauser

COPY --from=builder --chown=javauser:javauser /build/config-server/target/config-server-*.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=50.0 \
-Dspring.cloud.config.server.git.uri=git@github.com:hodkonem/mercury.git \
-Dspring.cloud.config.server.git.basedir=/config-repo \
-Dspring.cloud.config.server.git.force-pull=true"

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=5 \
    CMD curl -fsS "http://localhost:8888/actuator/health" || exit 1

EXPOSE 8888
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.jar"]