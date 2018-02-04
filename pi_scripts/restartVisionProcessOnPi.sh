#!/bin/bash

# Args: [PI_IP]

# Assuming the vision process is already installed and running on the target pi,
# restart it using systemd

PI_USER=pi
PI_IP="10.27.6.55"

# If user provided an IP address on the command line, use that
if [[ $1 != "" ]]; then
  PI_IP=$1
fi


# SSH to the pi and restart the vision process
echo "Restarting vision process on $PI_USER@$PI_IP"
# TODO
#ssh $PI_USER@$PI_IP 'systemd ...'
