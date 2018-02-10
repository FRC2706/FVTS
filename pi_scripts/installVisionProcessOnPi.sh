#!/bin/bash

# Args: [PI_ADDR]


# Configure the target pi to run TrackerboxReloaded on boot as a system service

PI_USER=pi
PI_ADDR="10.27.6.55"


# Error Handling: check if the current folder is the root of the TrackerboxReloaded git repo.
#   Quit with an error message if not.
# TODO

# If user provided an IP address on the command line, use that
if [[ $1 != "" ]]; then
  PI_ADDR=$1
fi


# Figure out what installation stuff we want to do
# TODO
