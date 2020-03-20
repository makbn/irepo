FROM java:openjdk-8-jdk-alpine


WORKDIR /app
COPY target/irepo.jar  irepo.jar

ENTRYPOINT ["java","-jar","irepo.jar"]