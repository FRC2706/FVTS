#!/bin/bash

# Args: [PI_ADDR]

# Assuming the vision process is already installed and running on the target pi,
# restart it using systemd

PI_USER=pi
PI_ADDR="10.27.6.55"

# If user provided an IP address on the command line, use that
if [[ $1 != "" ]]; then
  PI_ADDR=$1
fi


# SSH to the pi and restart the vision process
echo "Restarting vision process on ${PI_USER}@${PI_ADDR}"
# TODO
#ssh $PI_USER@$PI_ADDR 'systemd ...'
