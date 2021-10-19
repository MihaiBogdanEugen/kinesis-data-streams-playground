.PHONY: help
## help: Prints this help message
help:
	@echo "Usage: \n"
	@sed -n 's/^##//p' ${MAKEFILE_LIST} | column -t -s ':' |  sed -e 's/^/ /'

.PHONY: clean
## clean: Clean the files and directories generated during build
clean:
	./mvnw clean

.PHONY: build
## build: Compile all resources
build: clean
	./mvnw compile

.PHONY: run-app
## run-app: Run the App class
run-app: build
	./mvnw exec:java -D"exec.mainClass"="de.mbe.aws.tests.App"

.PHONY: display-dependency-updates
## display-dependency-updates: Display dependency updates
display-dependency-updates:
	./mvnw versions:display-dependency-updates