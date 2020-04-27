SHELL=/bin/bash

.PHONY: deploy

deploy:
    sbt stage deployHeroku
    sbt assembly deployHeroku
