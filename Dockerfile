# ================================
# Etapa 1: Construcción (Build)
# ================================
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copia el POM y las dependencias
COPY pom.xml .
COPY src ./src

# Compila el proyecto y genera el JAR
RUN mvn clean package -DskipTests

# ================================
# Etapa 2: Ejecución (Runtime)
# ================================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copia el artefacto compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto expuesto del microservicio (según tu .env)
EXPOSE 8080

# Ejecuta la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
