#!/bin/bash

# Configure the target pi to run TrackerboxReloaded on boot as a system service

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

echo Is the light on the camera on?

read -p 'Y/N? (Case sensitive!)' response

if [$response = 'N']; then
	echo Try rebooting the pi!
	read -p 'Type something when you are done rebooting' rebooting
	read -p 'Is the light on now? (Case sensitive!)' response
	if [$response = 'N']; then
		ssh ${PI_USER}@${PI_ADDR} "sudo systemctl stop vision.service"
		ssh ${PI_USER}@${PI_ADDR} "sudo systemctl disable vision.service"
		ssh ${PI_USER}@${PI_ADDR} "sudo systemctl daemon-reload"
		ssh ${PI_USER}@${PI_ADDR} "sudo systemctl enable vision.service"
		ssh ${PI_USER}@${PI_ADDR} "sudo systemctl start vision.service"
		read -p 'Is the light on now? (Case sensitive!)' response
		if [$response = 'N']; then
			echo "Copying visionParams.properties to ${PI_USER}@${PI_ADDR}"
			ssh ${PI_USER}@${PI_ADDR} "mkdir -p ${PI_DIR}"
			scp visionParams.properties ${PI_USER}@${PI_ADDR}:${PI_DIR}
			
			# ERROR HANDLING: if the copy failed, abort
			if [ $? -ne 0 ]; then
				# output to stderr
				>&2 echo "Error: Copy failed! Aborting."
				exit 0
			fi

			# Restart the vision process on the pi
			source ./pi_scripts/restartVisionProcessOnPi.sh ${PI_ADDR}
			read -p 'Is the light on now? (Case sensitive!)' response
			if [$response = 'N']; then
				ssh ${PI_USER}@${PI_ADDR} "sudo systemctl stop vision.service"
				ssh ${PI_USER}@${PI_ADDR} "sudo systemctl disable vision.service"
				ssh ${PI_USER}@${PI_ADDR} "sudo rm /etc/systemd/system/vision.service"
				read -p 'Is the light on now? (Case sensitive!)' response
				if [$response = 'N']; then
					source ./pi_scripts/uninstallVisionProcessOnPi.sh ${PI_ADDR}
					source ./pi_scripts/installVisionProcessOnPi.sh ${PI_ADDR}
					read -p 'Is the light on now? (Case sensitive!)' response
					if [$response = 'N']; then
						echo Something is very wrong... Contact a member of the vision team asap!
					fi
				fi
			fi
		fi
	fi
fi
