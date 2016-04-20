#!/bin/bash

RINGO_JAR_FILE_NAME=$(ls ../lib/ringo-client-*.jar|head -1);

if [ -r ${RINGO_JAR_FILE_NAME} ]
then
    RINGO_JAR=${RINGO_JAR_FILE_NAME};
fi;