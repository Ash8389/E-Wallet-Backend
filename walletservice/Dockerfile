# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY common-dto /common-dto
RUN mvn -f /common-dto/pom.xml install -DskipTests

COPY walletservice/pom.xml .
RUN mvn dependency:go-offline -q

COPY walletservice/src ./src
RUN mvn package -DskipTests -q

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S ewallet && adduser -S ewallet -G ewallet
USER ewallet

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
