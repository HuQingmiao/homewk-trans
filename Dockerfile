# 使用官方OpenJDK基础镜像（基于Debian）
FROM openjdk:21-jdk

# 定义工作目录
WORKDIR /app

# 复制编译后的JAR文件到容器中
COPY homewk-trans-1.0.0-SNAPSHOT.jar              /app/homewk-trans-1.0.0-SNAPSHOT.jar

#COPY start.sh                         /app/start.sh
#RUN chmod +x /app/start.sh

EXPOSE 9090

# 设置容器启动时的默认命令
#CMD ["/bin/bash",  "/app/start.sh"]
CMD ["java",  "-jar",  "homewk-trans-1.0.0-SNAPSHOT.jar"]


## 创建镜像： docker build -t homewk-trans-1.0.0 .
## 启动容器:  docker run  --name homewk-trans  -dp 31001:9090 --cpus 2.0 -m 3072m  homewk-trans-1.0.0
## 停止容器： docker stop  容器id

## 查看日志：
    ### docker logs -tf  容器id
    ### docker logs --tail num 容器id  # num为要显示的日志条数

## 保存镜像:  docker save -o      homewk-trans-1.0.0.tar homewk-trans-1.0.0
## 导入镜像： docker load --input homewk-trans-1.0.0.tar