SHELL=/bin/bash

.PHONY: run, deploy

run:
	- sbt run

deploy:
	- sbt assembly
	- docker build --force-rm --build-arg DATABASE_URL=$$DATABASE_URL -t catalog:latest .
	- docker tag catalog:latest registry.heroku.com/docker-crawler/worker
	- docker push registry.heroku.com/docker-crawler/worker

# 	- `aws ecr get-login --no-include-email --region $$AWS_REGION`
# 	- docker push $$REPOSITORY:latest
