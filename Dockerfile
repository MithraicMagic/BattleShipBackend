#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src ./src
COPY pom.xml ./
RUN mvn -q -f ./pom.xml clean package

#
# Test stage
#
RUN mvn test

#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build ./target/battleships-Alpha-1.0.jar /usr/local/lib/bs.jar

#
# Run stage
#
RUN sudo chmod +x run.sh

EXPOSE 8080
EXPOSE 6003

ENTRYPOINT ["./run.sh"]