#!/bin/bash

# Args: [PI_IP]

PI_USER=pi
PI_IP="10.27.6.55"
PI_DIR='/home/pi/TrackerboxReloaded'
LOCAL_JAR_PATH="output/CameraVision-all.jar"

# ERROR HANDLING: check if the current folder is the root of the TrackerboxReloaded git repo.
#   Quit with an error message if not.
# TODO

# If user provided an IP address on the command line, use that
if [[ $1 != "" ]]; then
  PI_IP=$1
fi

echo "Make sure you've done a \"git pull\" so that you're deploying the latest code!"

# ERROR HANDLING: Test that build.gradle is set to build for raspbian
#   Quit with an error message if not.
# TODO
# some grep magic

./gradlew build

# ERROR HANDLING: check that the build succeeded
if [[ $? ]]; then
  # gradle will have output lots of error messages, so we don't need to.
  exit(1)
fi

# Make sure the target folder exists and copy the newly built jar to the pi
echo "Copying newly build jar to $PI_USER@$PI_IP"
ssh $PI_USER@$PI_IP "mkdir -p $PI_DIR"
scp $LOCAL_JAR_PATH $PI_USER@$PI_IP:$PI_DIR

# ERROR HANDLING: if the rsync failed, abort
if [[ $? ]]; then
  # output to stderr
  >&2 echo "Error: Copy failed! Aborting."
  exit(1)
fi

# Restart the vision process on the pi
./pi_scripts/restartVisionProcessOnPi.sh $PI_IP
