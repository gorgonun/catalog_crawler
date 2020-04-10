FROM openjdk:8

WORKDIR /usr/src/app
COPY ./target/scala-2.12/fah-assembly-0.1.jar fah.jar

CMD java -jar fah.jar
