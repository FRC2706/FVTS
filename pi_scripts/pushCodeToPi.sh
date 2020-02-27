#!/bin/bash

# Args: [PI_ADDR]

PI_USER=pi
PI_ADDR="10.27.6.61"
PI_DIR='/home/pi/FVTS'
LOCAL_ZIP_PATH="output/FVTS.zip"
PARAMS_FILE="visionParams.properties"

# Error Handling: check if the current folder is the root of the FVTS git repo.
#   Quit with an error message if not.
if [ ! -e $PARAMS_FILE ]; then
    echo "It looks like ${PARAMS_FILE} does not exist! Are you sure you are in the root FVTS Dir?"
    exit 1
fi

# If user provided an IP address on the command line, use that
if [[ $1 != "" ]]; then
  PI_ADDR=$1
fi

echo "Make sure you've done a \"git pull\" so that you're deploying the latest code!"
echo

./buildScripts/raspbian-linuxtest.sh

# ERROR HANDLING: check that the build succeeded
if [ $? -ne 0 ]; then
  # gradle will have output lots of error messages, so we don't need to.
  exit 1
fi

# Make sure the target folder exists and copy the newly built zip to the pi
ssh ${PI_USER}@${PI_ADDR} "rm -rf ${PI_DIR}"
echo "Copying newly built jar to $PI_USER@$PI_ADDR"
ssh ${PI_USER}@${PI_ADDR} "mkdir -p ${PI_DIR}"
scp ${LOCAL_ZIP_PATH} ${PI_USER}@${PI_ADDR}:${PI_DIR}
ssh ${PI_USER}@${PI_ADDR} "rm -rf resources/"
scp -r resources/ ${PI_USER}@${PI_ADDR}:${PI_DIR}/
ssh ${PI_USER}@${PI_ADDR} "yes | unzip ${PI_DIR}/$(basename ${LOCAL_ZIP_PATH}) -d ${PI_DIR}"

# Push the vision params file too
source pi_scripts/pushVisionParamsToPi.sh ${PI_ADDR}
