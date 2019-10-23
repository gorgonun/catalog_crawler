SHELL=/bin/bash

setup:
	- python3 -m virtualenv venv -p python3.6 && \
		pip install -r requirements.txt