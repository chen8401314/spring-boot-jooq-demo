FROM 192.168.1.132:5000/tools/jdk/huagui-jdk-21.0.2

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

ADD build/libs/api-gateway-1.0.0-SNAPSHOT.jar /source/

EXPOSE 8080
ENTRYPOINT [ "java", "-Duser.timezone=UTC", "-jar", "source/api-gateway-1.0.0-SNAPSHOT.jar" ]
