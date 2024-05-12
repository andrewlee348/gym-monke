FROM openjdk:19
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=build/libs/server-all.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]