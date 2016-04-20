#!/bin/bash

DIR_NAME=`dirname $0`;

usage() {

    echo "\n### ERROR ###";
    echo "You need to specify three parameters to this program:";
    echo "1: username";
    echo "2: password";
    echo "3: participantId";
    echo "\nE.g: ./smp.sh me@company.com secret 9908:976098897";
}

########## Config file ##########

CONFIG_FILE=$DIR_NAME/../conf/shell-script-config;

if [ ! -r $CONFIG_FILE ]
then
    echo "\n### ERROR ###";
    echo "Can't find config file: $CONFIG_FILE";
    exit 4;
else
    . $CONFIG_FILE;
fi


########## Check parameters ##########

# check parameters
if [ $# -ne 3 ]
then
    usage;
    exit 3;
fi

USERNAME=$1;
PASSWORD=$2;
PARTICIPANT_ID=$3;

echo "Checking if recipient with participantId $PARTICIPANT_ID is registered in SMP";

########## Find Java > 1.5 ##########

DETECT_JAVA_SH=$DIR_NAME/lib/detect-java.sh;

if [ -r $DETECT_JAVA_SH ]
then

    # find Java 1.5
    . $DETECT_JAVA_SH;
else
    echo "\n### ERROR ###";
    echo "Can't find file $DETECT_JAVA_SH";
    exit 12;
fi

# did we find java?
if [ ! $JAVA_HOME ]
then

    echo "\n### ERROR ###";
    echo "Can't find Java 1.5 (or higher) on this computer"
    echo "The safest way to tell me where Java is installed is to define JAVA_HOME";
    exit 18;
fi

########## Ringo ##########

# we need to enter the directory so that relative path defined in logback.xml was valid
cd $DIR_NAME;

# see if ringo jar is present in working directory
DETECT_RINGO_SH=lib/detect-ringo.sh;

if [ -r $DETECT_RINGO_SH ]
then
    . $DETECT_RINGO_SH;
else
    echo "\n### ERROR ###";
    echo "Can't find file $DETECT_RINGO_SH";
    exit 22;
fi

# did we find ringo jar
if [ ! $RINGO_JAR ]
then

    echo "\n### ERROR ###";
    echo "Can't find $RINGO_JAR_FILE_NAME in current working directory: `pwd`";
    exit 28;
fi

########## Call jar file ##########

# call standalone java app
$JAVA_HOME/bin/java -Dlogback.configurationFile=../conf/logback-smp.xml -classpath $RINGO_JAR no.sr.ringo.standalone.RingoClientMain --username $USERNAME --password $PASSWORD --z $PARTICIPANT_ID --smp --address $AP_ADDRESS

# return exit code from java
exit $?;