#!/bin/sh
if [ "$PROFILE" = "dev" ]; then
  java -jar app.jar --spring.profiles.active=h2
elif [ "$PROFILE" = "prod" ]; then
  java -jar app.jar --spring.profiles.active=prod
else
  java -jar app.jar
fi