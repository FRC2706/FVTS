#!/bin/bash

chmod +x output/FVTS-all.jar
java -Djava.library.path=output/ -jar output/FVTS-all.jar $@
