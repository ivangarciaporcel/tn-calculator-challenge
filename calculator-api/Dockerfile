FROM openjdk:17-jdk-slim
LABEL maintainer="Ivan Garcia <ivangp05@gmail.com>"
ENV JAVA_TOOL_OPTIONS -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
