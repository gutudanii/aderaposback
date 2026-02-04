# ---- Build Stage ----
FROM eclipse-temurin:21-jdk-jammy as build

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw* pom.xml ./
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Package the application
RUN ./mvnw package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/aderapos-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
