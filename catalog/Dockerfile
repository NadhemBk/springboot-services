FROM amazoncorretto:21 AS build
WORKDIR /app

RUN yum install -y tar gzip curl

COPY mvnw .
COPY .mvn/ .mvn/
COPY pom.xml .

COPY order/pom.xml order/
COPY catalog/pom.xml catalog/
COPY catalog/src/ catalog/src/

RUN ./mvnw clean package -pl catalog -DskipTests

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/catalog/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
