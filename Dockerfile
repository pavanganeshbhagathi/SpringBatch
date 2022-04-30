FROM openjdk:11
WORKDIR '/app'
ADD target/batch-0.0.1-SNAPSHOT.jar batch-0.0.1-SNAPSHOT.jar
EXPOSE 8086
COPY . .
ENTRYPOINT ["java", "-jar", "batch-0.0.1-SNAPSHOT.jar"]