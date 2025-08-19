FROM eclipse-temurin:24-jre
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar cashcoach.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "cashcoach.jar"]