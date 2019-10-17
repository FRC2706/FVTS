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

# Java sample vision system

This is the WPILib sample build system for building Java based vision targeting for running on systems other than the roboRIO. This currently supports the following platforms

* Windows
* Raspberry Pi running Raspbian
* Generic Armhf devices (such as the BeagleBone Black or the Jetson)
* Linux x86 and x86_64

It has been designed to be easy to setup and use, and only needs a few minor settings to pick which system you want to be ran on. It has samples for interfacing with NetworkTables and CsCore from
any device, along with performing OpenCV operations.

## Choosing which system to build for

Run `buildScripts/linux.sh` for linux x86_64, `buildScripts/windows.bat` for windows, `buildScripts/raspbian-linuxtest.sh` to build for raspbian with linux tests and `buildScripts/raspbian-windowstest.bat`

## Choosing the camera

Change `cameraSelect` in `visionParams.properties` to change the USB camera number.

## Running

Run `gradlew run` after running the build script for your platform.
