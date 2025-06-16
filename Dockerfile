FROM eclipse-temurin:21-jdk-alpine

# Crea una directory app
WORKDIR /app

# Copia il jar buildato
COPY target/*.jar app.jar

# Espone la porta 8080
EXPOSE 8080

# Comando di avvio
ENTRYPOINT ["java","-jar","app.jar"]
