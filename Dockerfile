FROM openjdk:8

WORKDIR /usr/src/app
COPY ./target/scala-2.12/catalog_crawler-assembly-0.1.jar catalog.jar

ARG DATABASE_URL
ENV DATABASE_URL=$DATABASE_URL

CMD java -cp catalog.jar catalog.setups.CrawlerSetup && java -cp catalog.jar catalog.setups.ParserSetup
