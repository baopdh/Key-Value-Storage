#!/bin/bash
thrift -r --gen java kvstore.thrift
cp -a gen-java/kvstore/. ../Key-Value-Storage-Server/src/main/java/com/baopdh/dbserver/thrift/gen
cp -a gen-java/kvstore/. ../Key-Value-Storage-Client/src/main/java/com/baopdh/dbclient/thrift/gen
