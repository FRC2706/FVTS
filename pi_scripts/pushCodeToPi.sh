#!/bin/bash

# Args: [PI_ADDR]

PI_USER=pi
PI_ADDR="10.27.6.55"
PI_DIR='/home/pi/TrackerboxReloaded'
LOCAL_JAR_PATH="output/CameraVision-all.jar"

# ERROR HANDLING: check if the current folder is the root of the TrackerboxReloaded git repo.
#   Quit with an error message if not.
# TODO

# If user provided an IP address on the command line, use that
if [[ $1 != "" ]]; then
  PI_ADDR=$1
fi

echo "Make sure you've done a \"git pull\" so that you're deploying the latest code!"
echo

# ERROR HANDLING: Test that build.gradle is set to build for raspbian.
#   Quit with an error message if not.
# Some grep magic -- grep is a fancy pattern-matcher, (like a ctrl+f tool)
#   command explanation:
#     cat build.gradle: read the file, pipe the output to the next command
#     grep "arm-raspbian": filter the text so it only has lines that contain "arm-raspbian", pipe the output
#     grep -v "^//": -v means "not", -q means "quiet" so it doesn't print its output to screen,
#       "^//" means "lines that begin with //"
#       ... ie if the line returned by `grep "arm-raspbian"` begins with //, then fail.
cat build.gradle | grep "arm-raspbian" | grep -vq "^//"
if [ $? -ne 0 ]; then
  >&2 echo $(cat build.gradle | grep -n "arm-raspbian")  # -n prints the line number where it found it
  >&2 echo "Error: your build.gradle is not configured to build for raspbian."
  >&2 echo "    I'm sorry Dave, I'm afraid I can't do that."
  exit 1
fi


./gradlew build

# ERROR HANDLING: check that the build succeeded
if [ $? -ne 0 ]; then
  # gradle will have output lots of error messages, so we don't need to.
  exit 1
fi

# Make sure the target folder exists and copy the newly built jar to the pi
echo "Copying newly built jar to $PI_USER@$PI_ADDR"
ssh ${PI_USER}@${PI_ADDR} "mkdir -p ${PI_DIR}"
scp ${LOCAL_JAR_PATH} ${PI_USER}@${PI_ADDR}:${PI_DIR}

# ERROR HANDLING: if the rsync failed, abort
if [ $? -ne ]; then
  # output to stderr
  >&2 echo "Error: Copy failed! Aborting."
  exit 1
fi

# Push the vision params file too
source ./pi_scripts/pushvisionParamsToPi.sh

# Restart the vision process on the pi
source ./pi_scripts/restartVisionProcessOnPi.sh ${PI_ADDR}
