#!/usr/bin/env bash

JAVA_SRC=../server/src/main/java

protoc --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java \
    --grpc-java_out=$JAVA_SRC *.proto

protoc --java_out=$JAVA_SRC *.proto