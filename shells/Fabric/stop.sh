#!/bin/bash

${PWD}/fabric-samples/test-network/network.sh down

#echo "Stopping containers"
#docker stop $(docker ps -aq --filter label=service=hyperledger-fabric; docker ps -aq --filter name='dev-peer*'; docker ps -aq --filter name=ccaas) | sort | uniq 2>/dev/null || true
