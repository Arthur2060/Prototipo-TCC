# Estágio 1: Build (Compilação)
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# 1. Copia o JAR privado. O Docker falhará aqui se o arquivo não for encontrado.
COPY lib/spring-mqttx-0.0.1.jar /tmp/dep.jar

# 2. Instala o JAR no repositório Maven local do contêiner
RUN mvn install:install-file \
    -Dfile=/tmp/dep.jar \
    -DgroupId=com.rafaelcosta \
    -DartifactId=spring-mqttx \
    -Dversion=0.0.1 \
    -Dpackaging=jar


# Copia o pom.xml e baixa as dependências (para cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e compila o projeto
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Runtime (Execução)
# Usamos o JRE (Runtime Environment) baseado em Alpine (imagem menor)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Define o nome do JAR como variável de ambiente (melhor prática)
ARG JAR_FILE=target/*.jar

# CORREÇÃO: Copia o JAR do estágio de build para o estágio de runtime
# O caminho correto é /app/target/ (WORKDIR do estágio 'build' + target)
COPY --from=build /app/${JAR_FILE} app.jar

EXPOSE 8080

# Comando de execução da aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]