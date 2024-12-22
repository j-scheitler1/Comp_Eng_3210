#!/bin/bash

# Check if a parameter (LEGv8 binary file) was passed
if [ -z "$1" ]; then
    echo "Usage: sh run.sh <legv8_binary_file>"
    exit 1
fi

java Main $1
