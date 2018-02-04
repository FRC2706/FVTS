#!/bin/bash

# Args: [PI_IP]

PI_USER=pi
PI_IP="10.27.6.55"
PI_DIR='/home/pi/TrackerboxReloaded'

# Error Handling: check if the current folder is the root of the TrackerboxReloaded git repo.
#   Quit with an error message if not.
# TODO

echo "Copying visionParams.properties to $PI_USER@$PI_IP"
rsync visionParams.properties $PI_USER@$PI_IP:$PI_DIR

# ERROR HANDLING: if the rsync failed, abort
if [[ $? ]]; then
  # output to stderr
  >&2 echo "Error: Copy failed! Aborting."
  exit(1)
fi

# Restart the vision process on the pi
./pi_scripts/restartVisionProcessOnPi.sh $PI_IP
