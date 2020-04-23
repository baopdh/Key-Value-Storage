#!/bin/bash
genpath="./gen-java/com"
path="/src/main/java"
thrift -r --gen java kvstore.thrift
cp -a $genpath ../Key-Value-Storage-Server$path
cp -a $genpath ../Key-Value-Storage-Client$path
