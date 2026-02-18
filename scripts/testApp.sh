#!/bin/bash
SERVER_VERSION=$(docker version --format '{{.Server.APIVersion}}')
CLIENT_VERSION=$(docker version --format '{{.Client.APIVersion}}')
echo "Server version: $SERVER_VERSION"
echo "Client version: $CLIENT_VERSION"

if [[ "$CLIENT_VERSION" < "$SERVER_VERSION" || "$CLIENT_VERSION" == "$SERVER_VERSION" ]]; then
    SUPPORTED_VERSION="$CLIENT_VERSION"
    echo "Client <= Server" 
else 
    SUPPORTED_VERSION="$SERVER_VERSION"
    echo "Server is smaller"
fi
echo "supported: $SUPPORTED_VERSION"
echo "api.version=$SUPPORTED_VERSION" >> ~/.docker-java.properties

set -euxo pipefail
docker version
docker ps
./mvnw -version

./scripts/packageApps.sh

./mvnw -ntp -pl system liberty:create liberty:install-feature liberty:deploy
./mvnw -ntp -pl graphql liberty:create liberty:install-feature liberty:deploy

./mvnw -ntp -pl system liberty:start
./mvnw -ntp -pl graphql liberty:start

./mvnw -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl system failsafe:integration-test
./mvnw -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl graphql failsafe:integration-test
./mvnw -ntp -pl system failsafe:verify
./mvnw -ntp -pl graphql failsafe:verify

./mvnw -ntp -pl system liberty:stop
./mvnw -ntp -pl graphql liberty:stop

./mvnw -ntp -pl query liberty:create liberty:install-feature liberty:deploy

docker pull -q icr.io/appcafe/open-liberty:kernel-slim-java11-openj9-ubi

docker build -t system:1.0-java11-SNAPSHOT --build-arg JAVA_VERSION=java11 system/.
docker build -t system:1.0-java17-SNAPSHOT --build-arg JAVA_VERSION=java17 system/.
docker build -t graphql:1.0-SNAPSHOT graphql/.
docker build -t query:1.0-SNAPSHOT query/.

docker ps
./mvnw -ntp -pl query verify
