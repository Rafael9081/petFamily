
FROM openjdk:17-jdk-slim as builder


WORKDIR /app


# sCOPY src ./src


RUN mvn clean install -DskipTests


FROM openjdk:17-jre-slim


WORKDIR /app


COPY --from=builder /app/target/*.jar app.jar


EXPOSE 8080


ENTRYPOINT ["java", "-jar", "app.jar"]