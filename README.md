# Vision2019

# Attribution and license

2D Vision re-written in Java

We release our software under the MIT license in the hopes that other teams use and/or modify our software.

Our one request is that if you do find our code helpful, please send us an email at frc2706@owcrobots.ca letting us know. We love to hear when we've helped somebody, and we'd like to be able to measure our impact on the community.

Thanks, and enjoy!

Team contact: frc2706@owcrobots.ca Supervising mentor: John Gray


The base code for this was from the WPILib samples: https://github.com/wpilibsuite/VisionBuildSamples/tree/master/Java

# Development
In order to make changes to this source code there are a few things that must be done to setup your IDE for vision development.

Step #1: Build Vision2019 according to the instructions in this document

Step #2: Add `output/CameraVision-all.jar` to the build path of your IDE and set its native library location to `output/`

Step #3: Add `src/main/java` and `src/test/java` as source folders in your IDE

Step #4: Configure the run profile for `Main.java` to pass `--development` as an argument to enable development mode

Note: Do not pass `--development` to the program when attempting to benchmark it, the UI updating is on the main thread and makes the FPS 10x worse

# Java sample vision system

This currently supports the following platforms

* Windows
* Raspberry Pi running Raspbian
* Generic Armhf devices (such as the BeagleBone Black or the Jetson)
* Linux x86 and x86_64

It has been designed to be easy to setup and use, and only needs a few minor settings to pick which system you want to be ran on. It has samples for interfacing with NetworkTables and CsCore from
any device, along with performing OpenCV operations.

## Choosing which system to build for

Run `buildScripts/linux.sh` for linux x86_64, `buildScripts/windows.bat` for windows, `buildScripts/raspbian-linuxtest.sh` to build for raspbian with linux tests, or `buildScripts/raspbian-windowstest.bat` to build for raspbian with windows tests

## Choosing the camera

Change `cameraSelect` in `visionParams.properties` to change the USB camera number.

## Running

### Running for non robot purposes

To run vision either execute `Main.java` (inside Eclipse) or run `cd output/`, then `./runCameraVision` (Linux) or `runCameraVision.bat` (Windows), note that the `visionParams.properties` and `master.cf` must be in the directory that you run vision from.

### Running on a pi

To run vision on a pi is is super easy. First run `./pi_scripts/installVisionProcessOnPi.sh [ip address]` to install the `vision` service to a pi, then run `./pi_scripts/linux/pushCodeToPi.sh [ip address]` (Linux) or `./pi_scripts/windows/pushCodeToPi.sh [ip address]`

After the program is installed and running on the raspberry pi it can be restarted using `./pi_scripts/restartVisionProcessOnPi.sh [ip address]`, you can push configuration changes using `./pi_scripts/pushVisionParamsToPi.sh [ip address]`, and if you want to uninstall vision you can run `./pi_scripts/uninstallVisionProcessOnPi.sh` (note that it does not remove the vision code/config from the pi, it only removed the service)

## Accessing the CLI

Vision2019 has an integrated logging server which can be remotely accessed. This is done through running `telnet [ip address] 5810` (note that this requires installing/enabling `telnet`)
