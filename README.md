# FVTS - The FRC Vision Tracking System
![](https://github.com/FRC2706/FVTS/workflows/Build%20%26%20Test/badge.svg)

# About

FVTS is a vision system written in Java designed to be used for any FRC game with minimal to no modifications. It is not fully tested and as such considered experimental, however it will likely work as shown by our non robot testing.

# Attribution and license

We release our software under the MIT license in the hopes that other teams use and/or modify our software.

The base code for this was from the WPILib samples: https://github.com/wpilibsuite/VisionBuildSamples/tree/master/Java

# Info

This currently supports the following platforms

* Windows
* Raspberry Pi running Raspbian
* Generic Armhf devices (such as the BeagleBone Black or the Jetson)
* Linux x86 and x86_64

NOTE: In order for FVTS to be able to run it must be built with a Java version that is less than or equal to the Java runtime version that it will be run in

It has been designed to be easy to setup and use, and only needs a few minor settings to pick which system you want to be ran on. It has samples for interfacing with NetworkTables and CsCore from
any device, along with performing OpenCV operations.
