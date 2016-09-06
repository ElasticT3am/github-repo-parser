#!/bin/bash

DIRECTORIES=`find . -maxdepth 1 -type d`

for directory in $DIRECTORIES; do
    [ -z "`find $directory -type f`" ] && echo "$directory is empty"
done
