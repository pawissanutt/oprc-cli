#!/usr/bin/env just --justfile

# maven build without tests
build:
    ./mvnw -DskipTests package

# dependencies tree for compile
dependencies:
    ./mvnw dependency:tree

# display updates
updates:
    ./mvnw versions:display-dependency-updates


build-native:
    ./mvnw install -Pnative -DskipTests -Dquarkus.native.container-build=true

jbang-install:
    jbang app install --name=ocli --force target/oprc-cli-1.0-SNAPSHOT-runner.jar
