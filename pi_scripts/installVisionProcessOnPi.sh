#!/bin/bash

# Args: [PI_IP]


# Configure the target pi to run TrackerboxReloaded on boot as a system service

PI_USER=pi
PI_IP="10.27.6.55"


# Error Handling: check if the current folder is the root of the TrackerboxReloaded git repo.
#   Quit with an error message if not.
# TODO

# If user provided an IP address on the command line, use that
if [[ $1 != "" ]]; then
  PI_IP=$1
fi


# Figure out what installation stuff we want to do
# TODO
