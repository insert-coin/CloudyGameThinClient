#!/bin/bash

cd "`dirname "$0"`"

jsonfile="$(find . -maxdepth 1 -name 'json*.jar')"
captchafile="$(find . -maxdepth 1 -name 'simplecaptcha*.jar')"

if [ -z "$jsonfile" ] || [ -z “$captchafile” ]
then
    echo “json jar or simplecaptcha jar not found in current directory”

else 
    javac -cp \* *.java
    java -cp .:\* CloudyLauncher
fi