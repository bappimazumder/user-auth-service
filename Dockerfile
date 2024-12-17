# Use an official OpenJDK runtime as a parent image for the build stage
FROM openjdk:17-alpine as build

# Set the working directory
WORKDIR /user-auth

# Copy the build files
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradle ./gradle
COPY src ./src

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Build the application (WAR file)
RUN ./gradlew clean build -x test

# Second stage: create a minimal image with just the WAR file
FROM openjdk:17-alpine

# Set the working directory
WORKDIR /user-auth

# Copy the WAR file from the build stage
COPY --from=build /user-auth/build/libs/*.war /user-auth/user-auth.jar

# Expose the port the app runs on (adjust if needed)
EXPOSE 8084

# Run the application using the Spring Boot loader
ENTRYPOINT ["java", "-jar", "user-auth.jar"]