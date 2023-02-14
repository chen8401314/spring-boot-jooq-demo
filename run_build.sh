#!/usr/bin/env bash

cd `dirname $0`

branch=$1
image_tag=$(git rev-parse HEAD | cut -c1-12)
profile=$2

docker run --rm -v $PWD:/source/ -v ~/.gradle/:/root/.gradle/ 192.168.1.132:5000/tools/gradle:7.6.0-jdk17-focal bash -ex /source/build.sh $profile

echo "Service docker building stage..."
bash docker_build_publish.sh $image_tag

# when current branch is master
#if [ $branch == "origin/HEAD" ] || [ $branch == "master" ]; then
#    echo "Jar packages publish..."
#    docker run --rm -v $PWD:/source/ -v ~/.gradle/:/root/.gradle/ 192.168.1.132:5000/tools/gradle:5.5.1 bash -ex /source/publish.sh
#fi

echo "Clean up stage..."
docker run --rm -v $PWD:/source/ 192.168.1.132:5000/tools/alpine rm -rf /source/.gradle/ /source/eureka/build/ /source/user-service/build/ /source/user-service/log/
exit 0
