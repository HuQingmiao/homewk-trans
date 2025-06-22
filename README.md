# 交易管理Mini系统

### 一. 需求与要求
创建一个与银行系统内交易管理相关的简单应用程序。 该应用程序应使用户能够记录、查看和管理金融交易。 本任务不需要用户认证。
具体要求：
* 使用Java 21和Spring Boot编写
* 主要实体为交易(transaction)
* 所有数据保存在内存中，无需持久化存储
* 需要解决的关键点包括：
   *  清晰且结构良好的API
   *  所有核心操作的性能优化
   *  全面的测试，包括单元测试和压力测试
   *  使用Docker、Kubernetes等工具进行容器化
   *  在适用场景下实现缓存机制
   *  健壮的验证和异常处理
   *  高效的数据查询和分页功能
   *  遵循RESTful API设计原则
* 最终交付应为一个 易于运行和测试的独立项目
* 使用 Maven 进行项目管理
* 若使用 标准JDK之外的外部库，需在 README 文件中列出并说明其用途


### 二. 设计思路
1) 将交易记录以 Map<id, 交易记录> 的形式存储，使得根据id查找、删除交易记录时高效。

2) 对主要查询字段（如：账户、交易类型、交易渠道等）建立倒排索引，即：利用Map<字段,List<交易记录>>建立字段与交易记录的映射关系。
    这样，就可以根据字段值快速找到匹配的交易记录。

3) 支持按交易时间进一步过滤，过滤后按交易时间排序，排序后再分页。

4) 对用户最近的查询条件及查询结果进行缓存。 这样，当用户来回翻页、反复点击查询时，系统就可以直接从缓存获取数据了。
   每次查询前，系统会将本次查询条件与上次的查询条件进行比较，如果查询条件没有变化，则直接从缓存获取；否则，重新查询并更新缓存。

   
### 三. 项目产出情况
1) 最终产品已部署到公网环境，请登录：http://111.230.96.181:9090/ ;
   如需查阅API文档，请访问： http://111.230.96.181:9090/swagger-ui.html 

2) 源码已上传GitHub：https://github.com/HuQingmiao/homewk-trans

3) Docker镜像制作失败，原因是拉取不到jdk21的镜像，尝试了好几个镜像源都不行，所以只提供常规部署包。


### 四. 常规部署包的运行说明
1) 将项目根目录下的文件 homewk-trans.zip 解压到任意目录
2) 进入解压后的目录 homewk-trans ， 执行 start.bat 或 start.sh 启动服务
3) 访问首页：http://ip:9090/ ，就可以看到交易记录相关的新、删、改、查功能
4) 如需查阅API文档，请访问：http://ip:9090/swagger-ui.html


### 五. Docker镜像制作及运行说明
1) 将项目根目录下的文件 homewk-trans.zip 解压到任意目录
2) 进入解压后的目录 homewk-trans ， 执行：`docker build -t homewk-trans` 创建镜像。（这一步，没执行成功，因为是拉取不到jdk21的镜像）
3) 执行: `docker save -o  homewk-trans.tar  homewk-trans ` 保存镜像。

4) 进入docker目录，执行：`docker load -i homewk-trans.tar` 导入镜像。
5) 执行：`docker run -dp 9090:9090  homewk-trans` 启动容器。
6) 访问首页：http://ip:9090/ ，就可以看到交易记录相关的新、删、改、查功能。
7) 如需查阅API文档，请访问：http://ip:9090/swagger-ui.html


### 六. 依赖包说明（Java 和 Spring Boot以外的）
1) junit-jupiter         用于单元测试
2) springdoc-openapi     用于生成API文档、调试接口
3) aspectjweaver         用于异常处理切面类, 见：com.hsbc.homewk.trans.common.ResultAspect
4) commons-lang3         用于生成Transaction实体类的 toString()方法
5) fastjson              用于测试，输出对象的信息
6) httpclient            用于测试，模拟用户发送HTTP请求