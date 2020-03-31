FROM openjdk:8

WORKDIR /usr/src/app
COPY ./target/scala-2.12/fah-assembly-0.1.jar fah.jar

ARG DATABASE_URL
ENV DATABASE_URL=$DATABASE_URL

CMD java -jar fah.jar
