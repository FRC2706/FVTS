#!/bin/bash

# Args: [PI_ADDR]


# Configure the target pi to run TrackerboxReloaded on boot as a system service

PI_USER=pi
PI_ADDR="10.27.6.55"

# If user provided an IP address on the command line, use that
if [[ $1 != "" ]]; then
  PI_ADDR=$1
fi

ssh ${PI_USER}@${PI_ADDR} "sudo systemctl stop vision.service"
ssh ${PI_USER}@${PI_ADDR} "sudo systemctl disable vision.service"
ssh ${PI_USER}@${PI_ADDR} "sudo rm /etc/systemd/system/vision.service"
