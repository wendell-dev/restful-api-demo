# 1. Build with: docker build -t wendell/restful-api-demo .
# 2. Run with: docker run-p 8081:8081 -d --name restful-api-demo wendell/restful-api-demo
# @see: https://spring.io/guides/gs/spring-boot-docker/#_containerize_it
FROM openjdk:8-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.restful.api.demo.App"]
