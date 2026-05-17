FROM amazoncorretto:21 AS build
WORKDIR /app

RUN yum install -y tar gzip curl

COPY mvnw .
COPY .mvn/ .mvn/
COPY pom.xml .

COPY catalog/pom.xml catalog/
COPY order/pom.xml order/
COPY order/src/ order/src/

RUN ./mvnw clean package -pl order -DskipTests

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/order/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
