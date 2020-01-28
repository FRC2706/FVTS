#!/bin/bash
./gradlew build -PbuiltType=raspbian -PtestType=windows $@
