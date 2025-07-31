# ---------- build ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY platform-common/pom.xml platform-common/pom.xml
RUN mvn -B -ntp -f platform-common/pom.xml dependency:go-offline
COPY platform-common/src platform-common/src
RUN mvn -B -ntp -f platform-common/pom.xml -DskipTests -Dspring-boot.repackage.skip=true install

COPY customer-service/pom.xml customer-service/pom.xml
RUN mvn -B -ntp -f customer-service/pom.xml dependency:go-offline
COPY customer-service/src customer-service/src
RUN mvn -B -ntp -f customer-service/pom.xml -DskipTests package

# ---------- runtime ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/customer-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app/app.jar"]
