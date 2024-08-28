# Use an official Eclipse Temurin image to build the application
FROM eclipse-temurin:21-jdk AS builder
# Set the working directory
WORKDIR /extracted
# Copy the JAR file to the container
COPY target/*.jar app.jar
# Extract the layers using Spring Boot's layertools
RUN java -Djarmode=layertools -jar app.jar extract

# Use the Eclipse Temurin JRE image for the runtime
FROM eclipse-temurin:21-jre
# Create a non-root user and group for better security
RUN groupadd -r spring && useradd -r -g spring spring
# Set the working directory
WORKDIR /application
# Copy the extracted layers from the build stage
COPY --from=builder /extracted/dependencies/ ./
COPY --from=builder /extracted/spring-boot-loader/ ./
COPY --from=builder /extracted/snapshot-dependencies/ ./
COPY --from=builder /extracted/application/ ./
# Change ownership of the application directory to the non-root user
RUN chown -R spring:spring /application
# Switch to the non-root user
USER spring:spring
# Expose the application port
EXPOSE 8080
# Set the entry point to run the Spring Boot application
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]