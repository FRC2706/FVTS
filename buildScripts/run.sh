#!/bin/bash

chmod +x output/MergeVision-all.jar
java -Djava.library.path=output/ -jar output/MergeVision-all.jar $@
