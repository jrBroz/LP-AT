# Estágio de Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copia o pom e o código fonte
COPY pom.xml .
COPY src ./src
# Roda o Maven para gerar o .jar
RUN mvn clean package -DskipTests

# Estágio de Execução
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copia o .jar compilado do estágio anterior
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
