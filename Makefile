.PHONY: compile run-endpoint run-client

compile:
	sbt compile

run-endpoint:
	sbt runEndpoint

run-client:
	sbt runClient
