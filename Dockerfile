# Estágio 1: build — usa Maven + JDK só para compilar, essa camada não vai pro container final
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: runtime — imagem final, bem mais leve, só com o JRE (não o JDK) e o .jar pronto
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# O Render injeta a variável PORT automaticamente; o Spring Boot precisa escutar nela
ENV PORT=8080
ENV TZ=America/Sao_Paulo
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
