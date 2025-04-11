FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn -B install -N -DskipTests

WORKDIR /build/eureka-server
COPY eureka-server/pom.xml .
COPY eureka-server/src ./src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY --from=builder /build/eureka-server/target/eureka-server-*.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]