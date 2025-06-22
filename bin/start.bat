
# 将windows控制台的输出编码改为UTF8
chcp 65001

set APPLICATION_NAME="homewk-trans"
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.7
set JVM_MEM_OPTS=-Xms1024m -Xmx2048m -XX:MaxPermSize=512m

cd "%~dp0"

java -Dfile.encoding=utf-8 -jar  homewk-trans-1.0.0-SNAPSHOT.jar  --spring.profiles.active=local
