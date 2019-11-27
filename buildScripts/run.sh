#!/bin/bash

chmod +x output/CameraVision-all.jar
java -Djava.library.path=output/ -jar output/CameraVision-all.jar $@
