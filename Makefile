SHELL=/bin/bash

.PHONY: run, deploy

run:
	- sbt run

deploy:
	- sbt assembly deployHeroku
