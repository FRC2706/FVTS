#!/bin/bash

# Args: [PI_ADDR]


# Configure the target pi to run FVTS on boot as a system service

PI_USER=pi
PI_ADDR="10.27.6.61"
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


# TODO Explain-y comments

scp pi_scripts/vision.service ${PI_USER}@${PI_ADDR}:/tmp
ssh ${PI_USER}@${PI_ADDR} "sudo cp /tmp/vision.service /etc/systemd/system && sudo systemctl stop vision.service && sudo systemctl disable vision.service && sudo systemctl daemon-reload && sudo systemctl enable vision.service && sudo systemctl start vision.service"

