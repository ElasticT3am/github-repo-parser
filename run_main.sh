#!/bin/bash


YEAR="DEFINE_ME"
USERNAME="DEFINE_ME"
PASSWORD="DEFINE_ME"


#Comment out the ARGS variable based on the expected behaviour

#           Download only - 4 threads (don't upload to neo4j)
#ARGS="--year ${YEAR} --username ${USERNAME} --password ${PASSWORD} --download --threads 4"

#           Download and upload to neo4j - 8 threads
#ARGS="--year ${YEAR} --username ${USERNAME} --password ${PASSWORD} --download --upload --threads 8"

#           Don't download - use already downloaded repos in ~/.repoparser/<YEAR> and upload to neo4j
#ARGS="--year ${YEAR} --username ${USERNAME} --password ${PASSWORD} --upload --threads 8"

mvn -Dexec.args="${ARGS}" -Dexec.mainClass="com.elasticthree.projectparser.Main" exec:java
