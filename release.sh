#!/bin/sh
./mvnw release:clean -DskipPitest -DskipTests
./mvnw release:prepare -DskipPitest -DskipTests
./mvnw release:perform -DskipPitest -DskipTests

