#!/bin/bash

# Args: [PI_ADDR]


# Uninstall the FVTS system service

PI_USER=pi
PI_ADDR="10.27.6.61"

# If user provided an IP address on the command line, use that
if [[ $1 != "" ]]; then
  PI_ADDR=$1
fi

ssh ${PI_USER}@${PI_ADDR} "sudo systemctl stop vision.service && sudo systemctl disable vision.service && sudo rm /etc/systemd/system/vision.service && sudo systemctl daemon-reload"
