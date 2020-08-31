#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src ./src
COPY pom.xml ./
RUN mvn -f ./pom.xml clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build ./target/battleships-Alpha-1.0.jar /usr/local/lib/bs.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/bs.jar"]