#!/usr/bin/env bash
currPath=`dirname $0`

cd `dirname $0`

${currPath}/gradlew publish
