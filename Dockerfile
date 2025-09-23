FROM gradle:8.14.3-jdk17 AS builder
WORKDIR /build
RUN mkdir -p /build/.gradle_home && chown gradle:gradle /build/.gradle_home
ENV GRADLE_USER_HOME=/build/.gradle_home
COPY --chown=gradle:gradle build.gradle settings.gradle /build/
COPY --chown=gradle:gradle src /build/src
RUN gradle --no-daemon clean build -x test

FROM openjdk:17-jdk-slim AS runtime
WORKDIR /app
EXPOSE 9090
COPY --from=builder /build/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
