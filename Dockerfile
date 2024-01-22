FROM eclipse-temurin:17-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG VERSION
COPY target/spring-six-starter-${VERSION}/WEB-INF/lib /app/lib
COPY target/spring-six-starter-${VERSION}/META-INF /app/META-INF
COPY target/spring-six-starter-${VERSION}/WEB-INF/classes /app

ENTRYPOINT [ "java", "-cp", "app:app/lib/*", "com.ti.demo.springsixstarter.SpringSixStarterApplication" ]
EXPOSE 8081
