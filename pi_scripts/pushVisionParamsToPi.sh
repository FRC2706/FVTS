#!/bin/bash

# Args: [PI_ADDR]

PI_USER=pi
PI_ADDR="10.27.6.55"
PI_DIR='/home/pi/TrackerboxReloaded'

# Error Handling: check if the current folder is the root of the TrackerboxReloaded git repo.
#   Quit with an error message if not.
# TODO

if [ ! -e "visionParams.properties" ]; then
    echo "It looks like the file your looking for does not exist! Are you sure you are in the root trackerboxReloaded Dir?"
    exit 1
else
    echo "The file you are looking for exists"
fi

echo "Copying visionParams.properties to ${PI_USER}@${PI_ADDR}"
scp visionParams.properties ${PI_USER}@${PI_ADDR}:${PI_DIR}



# ERROR HANDLING: if the rsync failed, abort
if [[ $? ]]; then
  # output to stderr
  >&2 echo "Error: Copy failed! Aborting."
  exit 0
fi

# Restart the vision process on the pi
source ./pi_scripts/restartVisionProcessOnPi.sh ${PI_ADDR}
