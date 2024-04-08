FROM eclipse-temurin:17-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG VERSION
COPY target/spring-six-starter-${VERSION}.war /app/spring-six-starter.war

ENTRYPOINT [ "java", "-jar", "app/spring-six-starter.war" ]
EXPOSE 8081
