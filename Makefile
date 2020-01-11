SHELL=/bin/bash

.PHONY: run, deploy

run:
	- sbt run

deploy:
	- sbt assembly
	- docker build -t catalog ./ --build-arg DATABASE_URL=$$DATABASE_URL
