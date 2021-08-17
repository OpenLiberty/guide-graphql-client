@ECHO Building images
@ECHO OFF

start /b docker build -q -t system:1.0-java8-SNAPSHOT --build-arg JAVA_VERSION=java8 system\.
start /b docker build -q -t system:1.0-java11-SNAPSHOT --build-arg JAVA_VERSION=java11 system\.
start /b docker build -q -t query:1.0-SNAPSHOT query\.
start /b docker build -q -t client:1.0-SNAPSHOT client\.
