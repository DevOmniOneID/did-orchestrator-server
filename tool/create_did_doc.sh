#!/bin/bash

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ]; then
  echo "Usage: $0 <wallet filename> <did doc filename> <did> <controller>"
  exit 1
fi

#filename .wallet / .did
WALLET_FILENAME=$1

#filename .wallet / .did
DID_FILENAME=$2

#did name
DID=$3

#controller did
CONTROLLER=$4

java -jar did-cli-tool-server-1.0.0.jar did createDid -m $WALLET_FILENAME -f $DID_FILENAME -id $DID -ci $CONTROLLER -mi assert -ai auth -ki keyagree -ii invoke -p