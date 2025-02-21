FROM registry.cn-beijing.aliyuncs.com/lstyyzx/jdk:17.0.2.3
ENV LANG C.UTF-8

COPY ./target/system-1.1.jar /usr/share/coffee-system.jar

EXPOSE 9021
# ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XshowSettings:vm", "-Djava.security.egd=file:/dev/./urandom","-jar","/usr/share/app.jar"]
ENTRYPOINT exec java -Denv=test -jar /usr/share/coffee-system.jar

