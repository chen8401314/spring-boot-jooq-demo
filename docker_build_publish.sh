#!/usr/bin/env bash

cd `dirname $0`

image_tag=$1
if [ "$image_tag" = "" ]; then
    image_tag=latest
fi
moduleArr=("api-gateway" "api-test" "asset-service" "component-library-service" "eureka" "new-metro-project" "newoa" "progress-plan-service" "survey-service" "user-service")
for dir in $(ls -l | grep ^d | awk '{print $NF}')
do
  if echo "${moduleArr[@]}" | grep -w "$dir" &>/dev/null; then
    echo $dir
    docker build -t 192.168.1.132:5000/build/$dir:$image_tag $dir
    exitCode=$?
    if [ $exitCode != "0" ]; then #checking the docker building
      echo $exitCode
      exit $exitCode
    fi
    docker tag 192.168.1.132:5000/build/$dir:$image_tag 192.168.1.132:5000/build/$dir
    docker push 192.168.1.132:5000/build/$dir:$image_tag
    docker push 192.168.1.132:5000/build/$dir
  fi
done
