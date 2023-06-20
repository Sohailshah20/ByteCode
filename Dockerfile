#FROM openjdk:17 as buildStage
#WORKDIR /app
#COPY mvnw .
#COPY .mvn .mvn
#COPY pom.xml .
#COPY src src
#RUN ./mvnw package
#COPY ./target/*.jar compiler-demo.jar
#
#FROM openjdk:17
#COPY --from=buildStage /app/compiler-demo.jar .
#ENTRYPOINT ["java", "-jar", "compiler-demo.jar"]


# Use a base image with Java 11
# Use a minimal base image
#FROM openjdk:17
#
#FROM openjdk:17 as buildStage
#WORKDIR /app
#COPY mvnw .
#COPY .mvn .mvn
#COPY pom.xml .
#COPY src src
#COPY .mvn/wrapper .mvn/wrapper
#RUN ./mvnw package -DskipTests
#COPY target/*.jar compiler-demo.jar
#FROM openjdk:17
#COPY --from=buildStage /app/compiler-demo.jar /app/compiler-demo.jar
#ENTRYPOINT ["java", "-jar", "/app/compiler-demo.jar"]
