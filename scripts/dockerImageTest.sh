#!/bin/bash
while getopts t:d:b:u: flag;
do
    case "${flag}" in
        t) DATE="${OPTARG}";;
        d) DRIVER="${OPTARG}";;
        *) echo "Invalid option";;
    esac
done

echo "Testing daily Docker image"

sed -i "\#<artifactId>liberty-maven-plugin</artifactId>#a<configuration><install><runtimeUrl>https://public.dhe.ibm.com/ibmdl/export/pub/software/openliberty/runtime/nightly/"$DATE"/"$DRIVER"</runtimeUrl></install></configuration>" query/pom.xml system/pom.xml
sed -i "\#<looseApplication>false</looseApplication>#a<install><runtimeUrl>https://public.dhe.ibm.com/ibmdl/export/pub/software/openliberty/runtime/nightly/"$DATE"/"$DRIVER"</runtimeUrl></install>"  graphql/pom.xml
cat query/pom.xml graphql/pom.xml system/pom.xml

sed -i "s;FROM openliberty/daily:latest;g" system/Dockerfile graphql/Dockerfile query/Dockerfile
cat system/Dockerfile graphql/Dockerfile query/Dockerfile

docker pull "openliberty/daily:latest"

../scripts/testApp.sh