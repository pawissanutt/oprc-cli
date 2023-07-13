#!/usr/bin/env just --justfile

# maven build without tests
build:
   mvnd -DskipTests package

# dependencies tree for compile
dependencies:
  ./mvnw dependency:tree

# display updates
updates:
  ./mvnw versions:display-dependency-updates


build-native:
    ./mvnw install -Pnative -DskipTests -Dquarkus.native.container-build=true

jbang-install:
    jbang app install --name=ocli --force target/quarkus-app/quarkus-run.jar
