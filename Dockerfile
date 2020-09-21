#
# Host backend
#

FROM openjdk:11-jre-slim
COPY target/battleships-Alpha-1.0.jar /usr/local/lib/bs.jar

ENTRYPOINT ["java", "-jar", "/usr/local/lib/bs.jar"]

