FROM adoptopenjdk/openjdk11:latest
WORKDIR /
ADD target/mrktmkr-0.0.1-SNAPSHOT.jar app/mrktmkr.jar
CMD java -jar app/mrktmkr.jar