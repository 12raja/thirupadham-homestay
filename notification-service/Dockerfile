# Stage 1: build the jar using Maven + JDK 21.
# This stage is thrown away after building - it's not part of the final image.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# Stage 2: copy just the built jar onto a small JRE-only image.
# Much smaller than shipping Maven + JDK + source code in the final image.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/notification-service.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
