# Build stage
FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:24-jre
ENV APP_PORT=8080 \
    APP_CONTEXT_PATH=/ \
    ENABLE_PROMETHEUS=true \
    STARTUP_READY_DELAY_MS=0
WORKDIR /app
COPY --from=build /workspace/target/wise-quotes-api.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
