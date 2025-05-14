# Maven commands
MAVEN = mvn
MAVEN_CLEAN = $(MAVEN) clean
MAVEN_COMPILE = $(MAVEN) compile
MAVEN_RUN = $(MAVEN) exec:java -Dexec.mainClass="controleur.MainOfGame"

# Default target
all: build

# Build the project
build:
	$(MAVEN_COMPILE)

# Run the project
run: build
	$(MAVEN_RUN)

# Build and run in one command
build-run: build run

# Clean compiled files
clean:
	$(MAVEN_CLEAN)

.PHONY: all build run build-run clean 