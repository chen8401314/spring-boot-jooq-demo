#!/bin/sh
cd `dirname $0`

for dir in $(ls -l | grep ^d | awk '{print $NF}')
do
  if [ "$dir" != "classes" ] && [ "$dir" != "schema" ] && [ "$dir" != "base-common" ]; then
    echo $dir
    docker images | grep $dir | awk '{print $3}' | xargs docker rmi -f || true
  fi
done

exit 0
