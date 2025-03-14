FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
COPY build/libs/loan-flow-simulator-*.jar app.jar
EXPOSE 8080
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]