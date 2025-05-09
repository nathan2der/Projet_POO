# Makefile for Wargame project

# Variables
MAVEN = mvn
JAVA = java
MAIN_CLASS = wargame.GameTest
TARGET_DIR = target/classes

# Default target
.PHONY: all
all: clean compile

# Clean the project
.PHONY: clean
clean:
	$(MAVEN) clean

# Compile the project
.PHONY: compile
compile:
	$(MAVEN) compile

# Run the test game
.PHONY: run
run: compile
	$(JAVA) -cp $(TARGET_DIR) $(MAIN_CLASS)

# Package the project into a JAR
.PHONY: package
package:
	$(MAVEN) package

# Install the project in local Maven repository
.PHONY: install
install:
	$(MAVEN) install

# Run Maven tests
.PHONY: test
test:
	$(MAVEN) test

# Build and run in one command
.PHONY: build-run
build-run: clean compile run

# Show help
.PHONY: help
help:
	@echo "Available targets:"
	@echo "  all        - Clean and compile the project (default)"
	@echo "  clean      - Clean the project"
	@echo "  compile    - Compile the project"
	@echo "  run        - Run the test game"
	@echo "  package    - Create a JAR file"
	@echo "  install    - Install in local Maven repository"
	@echo "  test       - Run tests"
	@echo "  build-run  - Clean, compile and run"
	@echo "  help       - Show this help message" 