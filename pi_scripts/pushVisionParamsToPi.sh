#!/bin/bash

# Args: [PI_ADDR]

PI_USER=pi
PI_ADDR="10.27.6.55"
PI_DIR='/home/pi/TrackerboxReloaded'
PARAMS_FILE="visionParams.properties"

# Error Handling: check if the current folder is the root of the TrackerboxReloaded git repo.
#   Quit with an error message if not.
if [ ! -e $PARAMS_FILE ]; then
    echo "It looks like ${PARAMS_FILE} does not exist! Are you sure you are in the root trackerboxReloaded Dir?"
    exit 1
fi

# If user provided an IP address on the command line, use that
if [[ $1 != "" ]]; then
  PI_ADDR=$1
fi

echo "Copying visionParams.properties to ${PI_USER}@${PI_ADDR}"
ssh ${PI_USER}@${PI_ADDR} "mkdir -p ${PI_DIR}"
scp visionParams.properties ${PI_USER}@${PI_ADDR}:${PI_DIR}

# ERROR HANDLING: if the copy failed, abort
if [ $? -ne 0 ]; then
  # output to stderr
  >&2 echo "Error: Copy failed! Aborting."
  exit 0
fi

# Restart the vision process on the pi
source ./pi_scripts/restartVisionProcessOnPi.sh ${PI_ADDR}
