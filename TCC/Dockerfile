FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY lib/spring-mqttx-0.0.1.jar /tmp/dep.jar
RUN mvn install:install-file ^
    -Dfile=/tmp/dep.jar ^
    -DgroupId=com.rafaelcosta ^
    -DartifactId=spring-mqttx ^
    -Dversion=0.0.1 ^
    -Dpackaging=jar

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/TCC-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]