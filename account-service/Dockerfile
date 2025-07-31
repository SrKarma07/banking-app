# ---------- build ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# 1) compilar e instalar la librería común en el repositorio local
COPY platform-common/pom.xml platform-common/pom.xml
RUN mvn -B -ntp -f platform-common/pom.xml dependency:go-offline
COPY platform-common/src platform-common/src
RUN mvn -B -ntp -f platform-common/pom.xml -DskipTests -Dspring-boot.repackage.skip=true install

# 2) compilar el micro‑servicio
COPY account-service/pom.xml account-service/pom.xml
RUN mvn -B -ntp -f account-service/pom.xml dependency:go-offline
COPY account-service/src account-service/src
RUN mvn -B -ntp -f account-service/pom.xml -DskipTests package

# ---------- runtime ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/account-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
