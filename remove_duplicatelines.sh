#!/bin/bash
PROG_NAME=$(basename $0)


if [ "$#" -eq 0 ]
then
    echo "usage: ${PROG_NAME} [text_files]"
    echo "example: ${PROG_NAME} *.txt"
    exit
fi

for file in $*; do
    sort -u -o $file $file
done
