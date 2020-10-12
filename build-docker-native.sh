#!/bin/bash

./build-native.sh
#./mvnw package -Pnative -Dquarkus.native.container-build=true -DskipTests=true
docker build -f src/main/docker/Dockerfile.native -t adamcrow64/starttv:native .

