FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/my-app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9091
ENTRYPOINT [ "java", "-jar", "app.jar", "--spring.config.location=file:/config/application.yml" ]