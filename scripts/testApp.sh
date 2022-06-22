#!/bin/bash
set -euxo pipefail

./scripts/packageApps.sh

mvn -pl system liberty:create liberty:install-feature liberty:deploy
mvn -pl graphql liberty:create liberty:install-feature liberty:deploy

mvn -pl system liberty:start
mvn -pl graphql liberty:start

mvn -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl system failsafe:integration-test
mvn -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl graphql failsafe:integration-test
mvn -pl system failsafe:verify
mvn -pl graphql failsafe:verify

mvn -pl system liberty:stop
mvn -pl graphql liberty:stop

mvn -pl query liberty:create liberty:install-feature liberty:deploy

docker pull icr.io/appcafe/open-liberty:full-java11-openj9-ubi

docker build -t system:1.0-java8-SNAPSHOT --build-arg JAVA_VERSION=java8 system/.
docker build -t system:1.0-java11-SNAPSHOT --build-arg JAVA_VERSION=java11 system/.
docker build -t graphql:1.0-SNAPSHOT graphql/.
docker build -t query:1.0-SNAPSHOT query/.

mvn -pl query verify
