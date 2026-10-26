FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src src
RUN mvn -B clean verify
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN mkdir /app/data && chown 10001:10001 /app/data
COPY --from=build /build/target/doce-encanto-1.0.0.jar app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
