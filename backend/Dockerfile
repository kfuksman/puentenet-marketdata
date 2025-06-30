FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven files first
COPY pom.xml .
COPY src ./src

# Install Maven and build the application
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Copy the built JAR file (the JAR name should match the artifactId and version)
RUN cp target/market-monitor-backend-1.0.0.jar app.jar

# Expose port
EXPOSE 8092

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 