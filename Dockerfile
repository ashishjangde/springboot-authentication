FROM maven:3.9.9-eclipse-temurin-21-alpine

WORKDIR /app

COPY .mvn .mvn

COPY mvnw .

COPY pom.xml .

RUN  ./mvnw dependency:go-offline

EXPOSE 8080

COPY src src

CMD ["./mvnw", "spring-boot:run"]