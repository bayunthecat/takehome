FROM amazoncorretto:17-alpine-jdk
COPY build/libs/takehome-0.0.1-SNAPSHOT.jar takehome-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=docker", "/takehome-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080