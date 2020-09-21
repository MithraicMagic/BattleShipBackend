#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src ./src
COPY pom.xml ./
COPY run.sh .
COPY package.json .
COPY package-lock.json .
RUN mvn -q -f ./pom.xml clean package

#
# Test stage
#
RUN mvn test

#
# Expose required ports
#

EXPOSE 8080
EXPOSE 6003
EXPOSE 5000

#
# Host test results
#
FROM node:alpine
RUN npm i -g serve
CMD ["serve", "-s", "target/site/jacoco", "-l", "5000"]

#
# Host backend
#

FROM openjdk:11-jre-slim
COPY --from=build ./target/battleships-Alpha-1.0.jar /usr/local/lib/bs.jar

CMD ["java", "-jar", "/usr/local/lib/bs.jar"]

