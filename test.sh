#!/usr/bin/env bash
cd `dirname $0`
echo $0
type_text = $(echo "cat //report/counter/@type" | xmllint --shell /build/reports/jacocoXml.xml | sed '1d;2d;$d' | sed 's/-//g')
miss_text = $(echo "cat //report/counter/@missed" | xmllint --shell /build/reports/jacocoXml.xml | sed '1d;2d;$d' | sed 's/-//g')
cover_text = $(echo "cat //report/counter/@covered" | xmllint --shell /build/reports/jacocoXml.xml | sed '1d;2d;$d' | sed 's/-//g')
IFS=";"
type_arr=($type_text)
miss_arr=($miss_text)
cover_arr=($cover_text)
num=${#type_text[@]}
n=0
echo "type miss cover percent"
while [ $n -lt num]
do
  val='expr ${miss_arr[$n]} + ${cover_arr[$n]}'
  ret=$(((${cover_arr[$n]} / $val * 100) + (${cover_arr[$n]} % $val> 0)))
  awk '{print int($1+0.5)}' ret
  echo "${type_arr[$n]} ${miss_arr[$n]} ${cover_arr[$n]} $ret%"
n ='expr $n + 1'
done
