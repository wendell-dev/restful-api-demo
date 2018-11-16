# 基于springBoot编写的RESTFul API
本项目可用于快速搭建基于springBoot的RESTFul API服务，同时集成了swagger作为接口的在线文档与调试工具。

当我们需要对访问进行授权时，通常做法是给客户端一个token，服务端对这个token进行验证，验证通过即可访问API。本项目中也集成token的生成，同时通过拦截器统一验证了token的有效性，这依赖于redis来存储token，但这也是比较流行的做法。

## 打包
mvn clean package
## 运行项目
java -jar restful-api-demo-0.0.1-SNAPSHOT.jar
## 本地接口文档测试访问地址
http://localhost/swagger-ui.html
