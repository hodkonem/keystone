FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build
COPY pom.xml .

RUN mvn -B install -N -DskipTests

WORKDIR /build/notification
COPY notification/pom.xml .
COPY notification/src ./src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
RUN addgroup --system javauser && \
    adduser --system --ingroup javauser javauser && \
    chown -R javauser:javauser /app
USER javauser

COPY --from=builder --chown=javauser:javauser /build/notification/target/notification-*.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=50.0"

EXPOSE ${SERVER_PORT}
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.jar"]