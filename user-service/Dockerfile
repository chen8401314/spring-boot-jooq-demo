FROM 192.168.1.132:5000/tools/jdk/huagui-jdk-21.0.2

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

ADD build/libs/user-service-1.0.0-SNAPSHOT.jar /source/

EXPOSE 8080
ENTRYPOINT [ "java","--add-opens","java.base/java.lang=ALL-UNNAMED", "-Duser.timezone=UTC", "-jar", "source/user-service-1.0.0-SNAPSHOT.jar" ]
