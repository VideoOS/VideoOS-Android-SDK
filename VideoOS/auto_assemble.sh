#!/bin/bash
TOKEN="45575a29a8a6ab766ebb0ae78879da23"
SHORT="AndroidDevApp"

cd videoos_dev_app
gradle clean assembleRelease

fir publish --short=$SHORT --token=$TOKEN ./build/outputs/apk/videoos_dev_app-release.apk