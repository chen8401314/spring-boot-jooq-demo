#!/usr/bin/env bash
currPath=`dirname $0`
cd `dirname $0`

profile=$1
if [ "$profile" = "" ]; then
    profile=dev
fi
${currPath}/gradlew -x test build -Dprofile=$profile
#excludeTest=$2
#if [ "$excludeTest" = "true" ]
#then
#   ${currPath}/gradlew -x test build -Dprofile=$profile
#else
#   ${currPath}/gradlew build -Dprofile=$profile
#fi
# temporary disable this
#${currPath}/gradlew jacocoTestReport -Dprofile=$profile

bash test.sh



