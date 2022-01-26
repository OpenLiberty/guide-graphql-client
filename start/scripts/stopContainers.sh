#!/bin/bash

docker stop query graphql system-java8 system-java11

docker network rm graphql-app
