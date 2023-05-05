# Spring Boot 2.7 脚手架

[![License](https://img.shields.io/badge/License-GPL--3.0-yellow.svg)](https://www.gnu.org/licenses)

-------



## 简介

1. 脚手架记录一些工作经验和学习总结。
2. 基于spring boot 2.7（java1.8基线），大量java资产运行在1.8上，且1.8生态成熟完备，未来会迁移至spring boot 3（java17基线）。
3. 采用阻塞式处理模型，技术成熟易掌控，响应式不易把控，相关技术也在发展中，比如操作关系数据库方面，近段时间的工作用到响应式技术栈（reactor、reactor-netty、webflux等），未来考虑响应式版本。
4. 面向中小型业务，用于构建微服务，不适用于网关（如nginx）类功能的开发（应考虑响应式技术栈）。




## 运行
1. 环境：java1.8 + mysql5.7 及以上 + redis 2.8 及以上。
2. 配置：把 main/resources/application-dev.yml 中的 spring.datasource 和 spring.redis 换成你的。
3. 启动：SpringBoot27.java是入口，执行其中的main方法即可。（需带方法参数：--spring.profiles.active=dev，VM options：-DAPP_NAME=spring-boot-27）
4. 试试 curl -X POST -H "content-type:application/json" -H "h1:v1" -d "{'k1':'v1'}" http://127.0.0.1:8080/foobar/httpTest。



## 单元测试
单元测试不仅验证现有代码的可运行性，也是保障代码迭代发展的关键手段，比如升级依赖版本、重构基础公共逻辑等，就需要重跑相关单元测试，保障相关

代码可运行，因此单元测试非常重要。

1. 最小化单元测试：比如@SpringBootTest重，会引入不相关的依赖，而且这些依赖可能导致单元测试很难构建，因此少用；又如用@WebMvcTest测试web逻辑，但实际可能通过@SpringJUnitConfig引入相关配置就够了，那就只用@SpringJUnitConfig。
2. 在1的基础上，尽量结合真实逻辑，少mock，比如少用@MockBean。
3. 对性能有要求的方法，要jmh测试。

脚手架大部功能有对应的单元测试，测试套件TestSuite.java整合所有单元测试，运行单元测试或套件前，把 test/resources/application.properties 中的 spring.datasource 和 test/resources/redis.properties 中的 spring.redis 换成你的。




## spring boot

spring boot有很多自动配置（*AutoConfiguration），很多用不上，又占用内存等资源，影响启动速度，因此可去掉，参SpringBoot27.java中@SpringBootApplication的exclude属性。



## servlet容器

spring boot 2.7内置的tomcat 9.0.x支持http2.0，但需要在操作系统本地安装相应库；underdow官方文档差，且计划把核心换成netty实现；所以选择jetty，其性能也适中，容器配置参JettyConfig.java。



## spring mvc

1. mvc配置参WebMvcConfig.java。

2. 支持http2.0的h2c，参JettyConfig.java。

3. 个人喜欢完全掌控mvc的配置，所以参官方：

   ```
   If you want to take complete control of Spring MVC, you can add your own @Configuration annotated with @EnableWebMvc
   ```

   同时方便把mvc做轻。

4. mvc是业务入口，日志追踪的起点，所以编写LogFilter.java。

5. spring boot默认的httpMessageConverters，包含xml等不常用（非主流）的Converter，因此自定义httpMessageConverters，仅支持string、表单、json，如有其它需求（如二进制json等），可添加相应Converter。

6. 为了像

   ```java
   @Controller
   @RequestMapping("/web-mvc")
   class TestController {
       @PostMapping("/test")
       @ResponseBody
       public Person test(@RequestBody Person person, Pageable pageable) {
           Assertions.assertEquals(16, pageable.getPageSize());
           person.setAge(168);
           return person;
       }
   }
   ```
   
   应用spring data的分页模型，增加了对应的HandlerMethodArgumentResolver。
   
7. 支持异步请求，某些场景可能需要（如长轮询（可考虑websocket，sse等替换）），spring boot基于内置容器已作封装，应用主要配置Executor处理请求。

8. 若需统一响应体结构、设置公共响应头等，可通过SB27ResponseAdvice.java、Result.java实现。

9. SB27ErrorController.java统一处理mvc异常，包括filter、controller抛出的，但controller抛出的异常，到达SB27ErrorController前，被HandlerExceptionResolver转换，导致源异常不清晰，因此加SB27ControllerAdvice.java处理controller异常。



## http客户端

1. 按spring官方建议，用WebClient。
2. WebClient原生响应式，也有阻塞式api，可用在阻塞式环境。
3. 阻塞式环境下，调用单个接口，WebClient相对RestTemplate（阻塞式实现，如apache httpclient等），可能有微小的性能差距；但实际场景中，一个业务逻辑往往要同时调用多个接口，WebClient可并行调用，有优势，WebClient支持http2.0也方便。
4. http2.0又分为h2（HTTP/2.0 support with TLS）、h2c（HTTP/2.0 support with clear-text），h2用于和浏览器交互（浏览器限制），重，可能还是https适合；h2c适合服务端内部交互，比如google的grpc，可理解为http2.0 + protobuf（二进制），因此脚手架仅支持h2c。
5. http2.0有推的功能，但有局限性，只能处理静态资源，因此应考虑websocket、sse等。
6. 参WebClientConfig.java、HTTP11WebClientConfig.java、HTTP2CWebClientConfig.java。



## websocket

SpringWebSocketServerConfig.java为websocket服务端配置，客户端可通过  ws://127.0.0.1:8080/websocket/xxx?clientId=c1 建立连接并交互，SpringWebSocketClientConfig.java为websocket客户端配置。



## 数据库

1. 用过jdbctemplate、hibernate、mybatis，前段时间工作用mybatis-plus，脚手架用spring-data-jpa。
2. 把data-jpa弱化成mybatis-plus用。
3. 实体关系标@Transient注解，即单表操作。
4. 关联查询通过hibernate session的native query实现，即原生sql关联查询。
5. 动态条件和分页查询，用hibernate原生api实现。
6. 通过hibernate session.detach等方式，把查询得到的实体置为游离态。
7. 新增批量插入实体的api，支持实体自定义整型id，替换JpaRepository.saveAll。
8. 官方自定义Repository的方式很别扭，可参UserRepository的方式。
9. 提供BaseJpaRepository及BaseJpaRepositoryImpl，业务Repository继承BaseJpaRepository，可方便自然地和数据库交互，参UserRepository。
10. 参JpaConfig.java、BaseJpaRepository.java、BaseJpaRepositoryImpl.java、UserRepositoryTests.java。



## redis

1. 参RedisConfig.java、RedisTemplateLockService.java、application-dev.yml中spring.redis部分。
2. 分布式锁，redission有对应功能，但redission分开源和商业版，纠结，所以基于RedisTemplate编写了分布式锁功能，提供相应注解，支持可重入；不支持续租、红锁、异步，这些复杂也重，如果有需要或已用上，可能说明系统设计或运行有问题。



## 分布式id

1. 参LongIdGenerator.java。
2. 类雪花算法实现，id由时间、服务器id、序号3部分顺序组成。
3. 支持8191个服务器节点 (服务器id取值范围:[0,8191])，每秒可生成131071个id。
4. 时钟回拨，应在运维层面处理，比如独立的时间同步服务器。
5. jmh test: LongIdGeneratorPerfTests。



## 日志

1. 公司的分布式日志收集由 log4j2(kafka appender) + kafka + logstash + es 实现，经受过大日志量的考验，因此脚手架用log4j2。
2. 分布式日志追踪参log4j2-dev.xml、LogFilter、TaskExecutorConfig.java中的taskDecorator，再通过1实现，不想引入zipkin、skywalking等把脚手架搞重搞复杂。



## 其它

1. 引入的spring-boot-starter-websocket，会创建Executor，进而替换掉spring boot原有的ThreadPoolTaskExecutor、ThreadPoolTaskScheduler ，所以在TaskExecutorConfig.java中按原spring boot的配置，定义了ThreadPoolTaskExecutor 、ThreadPoolTaskScheduler，若有逻辑需要Executor/Scheduler，应优先考虑复用前面两个资源。

   

2. 异常处理，若当前逻辑会抛异常，且调用方需要捕获并进行业务逻辑处理 (如执行另一逻辑分支)，则抛显式异常，即Exception，如果调用方捕获后，不作任何处理，则抛RuntimeException；抛异常时要考虑是否带异常栈。

   

3. 与调用方交互，若需统一数据模型，可用Result.java；关于各种O：VO、PO、DTO等，个人认为自然方便就好，没必要因为分层而引入各种O，一个实体在系统中流转即可，对一些需求，如实体

   ```java
   class User {
   private String name;
   private String password;
   }
   ```

   在与前端交互时，password不能传，那setPassword(null)即可。

   

## 转载请注明出处，谢谢



## 联系方式

微信：lancerwechat

