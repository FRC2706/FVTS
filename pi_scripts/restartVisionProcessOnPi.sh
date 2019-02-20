#!/bin/bash

# Args: [PI_ADDR]

# Assuming the vision process is already installed and running on the target pi,
# restart it using systemd

PI_USER=pi
PI_ADDR="10.27.6.61"
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


# SSH to the pi and restart the vision process
echo "Restarting vision process on ${PI_USER}@${PI_ADDR}"
ssh ${PI_USER}@${PI_ADDR} "sudo systemctl stop vision.service && sudo systemctl daemon-reload && sudo systemctl start vision.service"
