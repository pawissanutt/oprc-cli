#!/usr/bin/env just --justfile
mvn := "mvnd"
# maven build without tests
build:
    ./mvnw package

build-no-test:
  {{mvn}} package -DskipTests

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

native-window:
    cmd /c 'call "C:\Program Files (x86)\Microsoft Visual Studio\2022\BuildTools\VC\Auxiliary\Build\vcvars64.bat" && mvn package -Dnative'
