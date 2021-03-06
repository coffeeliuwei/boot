## 微服务入门（Microservice Architecture）
微服务架构的定义  
“微服务架构”(Microservice Architecture)一词在过去的几年间涌现出来，作为一套可以独立部署的服务，用来描述一种特殊的设计软件应用的方式。虽然没有这个架构风格没有明确的定义，但围绕业务功能的组织，自动部署（automated deployment），端智能(intelligence in the endpoints,)以及对语言和数据的分散控制存在某些共同的特征。

### 单体架构特点
|问题|说明|
|:---:|:---:|
|测试、部署问题	测试、部署成本高：|业务运行在一个进程中，因此系统中任何程序的改变，都需要对整个系统重新测试并部署|
|伸缩性	可伸缩性差：|单体架构系统由于单进程的局限性，水平扩展时只能基于整个系统进行扩展，无法针对某一个功能模块按需扩展。例如：电商系统，包含了 用户、商品、订单、交易、支付；如果商品的访问量非常大，我想对商品进行集群部署，这时你是无法做到，你只能对整个系统进行集群部署。|
|靠性性	可靠性差：|某个BUG，例如死循环、内存溢出，会导致整个进程宕机，影响整个系统，影响其他功能。|
|系统迭代	迭代困难：|由于所有的功能都在一个系统里面，会导致日常迭代相当困难，例如互联网项目，多个项目每月都有一次正常迭代，必定导致代码分支过多，分支合代码繁琐困难。|
|跨语言程度	|整个系统（甚至整个企业）统一的技术栈，管理起来看似简单。但有时候统一的标准并不适合所有的实际情况。|
|团队协助	|整个系统一个团队。如果系统变得庞大，成员就需要学习大量的代码和领域知识，团队内的沟通和协作也变得低效。|

### 微服务和单体架构的区别

|性能|单体架构特点|微服务的特点|
|:---:|:---:|:---:|
|测试、部署问题|测试、部署成本高：业务运行在一个进程中，因此系统中任何程序的改变，都需要对整个系统重新测试并部署|每个微服务组件都是简单灵活的，能够独立部署。不像单体系统，需要一个庞大的应用服务器来支撑。|
|伸缩性|可伸缩性差：单体架构系统由于单进程的局限性，水平扩展时只能基于整个系统进行扩展，无法针对某一个功能模块按需扩展。例如：电商系统，包含了 用户、商品、订单、交易、支付；如果商品的访问量非常大，我想对商品进行集群部署，这时你是无法做到，你只能对整个系统进行集群部署。|微服务之间是松耦合，微服务内部是高内聚，每个微服务很容易按需扩展。水平扩展只要按服务进行扩展即可。|
|可靠性|可靠性差：某个BUG，例如死循环、内存溢出，会导致整个进程宕机，影响整个系统，影响其他功能。|不会因为某个bug,而导致整个系统宕机。因为服务之间是松耦合的关系，不是强依赖。|
|系统迭代|迭代困难：由于所有的功能都在一个系统里面，会导致日常迭代相当困难，例如互联网项目，多个项目每月都有一次正常迭代，必定导致代码分支过多，分支合代码繁琐困难。|	"由一个小团队负责更专注专业，相应的也就更高效可靠。每个微服务可以由不同的团队独立开发。"|
|跨语言程度	|整个系统（甚至整个企业）统一的技术栈，管理起来看似简单。但有时候统一的标准并不适合所有的实际情况。|微服务架构与语言工具无关，自由选择合适的语言和工具，高效的完成业务目标即可。|
|团队协助|整个系统一个团队。如果系统变得庞大，成员就需要学习大量的代码和领域知识，团队内的沟通和协作也变得低效。|团队按微服务配置。成员专注于小的领域和代码集。沟通成本低。容易学习|

### 微服务架构带来的问题	
1. 运维成本过高，部署物数量多、监控进程多导致整体运维复杂度提升。	
2. 接口兼容多版本（面向服务开发，就是面向接口开发，一般接口的变更，必然导致多个客户端要跟着改，那怎么办呢，就必须做接口的多版本开发）	
3. 分布式系统的复杂性（本来就一个系统，把他拆成多个服务，就会引发，网络延迟（因为网络不稳定）、服务容错性、服务的负载均衡等等）	
4. 分布式事务（微服务开发，带来的难题就是分布式事务的处理，关于分布式事务业界也有很多处理的方法）	

### 单体变微服务策略

+ 拆分：对应用进行水平和垂直拆分，例如商品中心、计费中心、订单中心等。
+ 解耦：通过服务化和订阅、发布机制对应用调用关系解耦，支持服务的自动注册和发现
+ 透明：通过服务注册中心管理服务的发布和消费、调用关系
+ 独立：服务可以独立打包、发布、部署、启停、扩容和升级，核心服务独立集群部署
+ 分层：梳理和抽取核心应用、公共应用，作为独立的服务下沉到核心和公共能力层，逐渐形成稳定的服务中心，使前端应用能更快速的响应多变的市场需求"

### MVC、 RPC、SOA 与微服务的架构区别
![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/36.jpg?raw=true)

### 如何设计微服务
#### AKF拆分原则
![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/37.jpg?raw=true)

AKF扩展立方体（参考《The Art of Scalability》），是一个叫AKF的公司的技术专家抽象总结的应用扩展的三个维度。理论上按照这三个扩展模式，可以将一个单体系统，进行无限扩展。
+ Y 轴（功能） ：就是按功能进行拆分，它基于不同的业务拆分。
+ X 轴 (水平扩展) ：很好理解，将微服务运行多个实例，做个集群加负载均衡的模式。
+ Z 轴（数据分区） ：基于类似的数据分区，比如一个互联网打车应用突然火了，用户量激增，集群模式撑不住了，那就按照用户请求的地区进行数据分区，北京、上海、四川等多建几个集群。
+ 场景说明：比如滴滴打车，一个集群撑不住时，分了多个集群，后来用户激增还是不够用，经过分析发现是乘客和车主访问量很大，就将滴滴打车拆成了三个乘客服务、车主服务、支付服务。三个服务的业务特点各不相同，独立维护，各水平扩展。

#### 前后端分离原则
前后端分离原则，简单来讲就是前端和后端的代码分离也就是技术上做分离，我们推荐的模式是最好直接采用物理分离的方式部署，进一步促使进行更彻底的分离。不要继续以前的服务端模板技术，比如JSP ，把Java JS HTML CSS 都堆到一个页面里，稍复杂的页面就无法维护。
这种分离模式的方式有几个好处：

1. 前后端技术分离，可以由各自的专家来对各自的领域进行优化，这样前端的用户体验优化效果会更好。
2. 分离模式下，前后端交互界面更加清晰，就剩下了接口和模型，后端的接口简洁明了，更容易维护。
3. 前端多渠道集成场景更容易实现，后端服务无需变更，采用统一的数据和模型，可以支撑多个前端；例如 微信h5前端、安卓、IOS。

#### 无状态服务

什么是状态：如果一个数据需要被多个服务共享，才能完成一笔交易，那么这个数据被称为状态。进而依赖这个“状态”数据的服务被称为有状态服务，反之称为无状态服务。

那么这个无状态服务原则并不是说在微服务架构里就不允许存在状态，表达的真实意思是要把有状态的业务服务改变为无状态的计算类服务，那么状态数据也就相应的迁移到对应的“有状态数据服务”中。

场景说明：例如我们以前在本地内存中建立的数据缓存、Session缓存，到现在的微服务架构中就应该把这些数据迁移到分布式缓存中存储，让业务服务变成一个无状态的计算节点。迁移后，就可以做到按需动态伸缩，微服务应用在运行时动态增删节点，就不再需要考虑缓存数据如何同步的问题。

#### Restful 通信风格
REST (REpresentation State Transfer，表述性状态转移) 。REST 指的是一组架构约束条件和原则。REST 从资源的角度来观察整个网络，分布在各处的资源由 URI 确定，而客户端的应用通过 URI 来获取资源的表征。REST 是设计风格而不是标准。REST 通常基于使用 HTTP，URI，和 XML 以及 HTML 这些现有的广泛流行的协议和标准。

### springcloud与dubbo比较
|对比项	|Dubbo|SpringCloud|
|:---:|:---:|:---:|
|出身背景|"阿里核心框架是服务化治理"|"Spring社区核心框架是Netflix开源微服务架构群体"|
|文档质量|集中，健全|多，大部分是英文版|
|性能|1|3|
|服务注册中心|zookeeper|Spring Cloud Netflix Eureka|
|服务调用方式|RPC|REST API|
|服务网关|无|Spring Cloud Netflix Zuul|
|断路器	|集群容错|Spring Cloud Netflix Hystrix|
|分布式配置	|无|Spring Cloud Config|
|服务跟踪|无|Spring Cloud Sleuth|
|消息总线|无|Spring Cloud Bus|
|数据流	|无|Spring Cloud Stream|
|批量任务|无|Spring Cloud Task|

#### 分析：
由于Spring Cloud与Dubbo天生使用的协议层面不一样，前者是HTTP，后者是TCP(使用的是Netty NIO框架，序列化使用的阿里定制版Hessian2)，导致两个框架的性能差距略大。基本上是三比一的差距！Dubbo官方TPS是1W左右，这和我们的测试最高值是接近的。在之前我们还进行过一次测试，那次测试是真实的项目测试，包含了对数据库的访问，最后二者的结果相差并不是很大。由此也得出，框架的性能可能对一个真实的请求(Request)影响并不是很大，或者说并不起决定性作用，也许真正影响性能的是你的业务代码，比如数据库访问以及IO，当然了，框架的性能在一些对性能要求敏感的应用来说也是要考虑的。

虽然Spring Cloud在性能上与Dubbo有天生的劣势，但考虑到Spring Cloud作为一套专门的微服务框架，再加上RESTful风格的API的趋势，从综合的角度，Spring Cloud无疑是你所在的公司未来微服务化进程中不可缺少的选择之一！

### Spring Cloud版本规划

|版本号|版本|用途|
|:---:|:---:|:---:|
|BUILD-XXX|	开发版|一般是开发团队内部用的|
|GA|稳定版|	"内部开发到一定阶段了，各个模块集成后，经过全面测试，发现没问题了。可以对外发行了，这个时候叫GA（AenerallyAvailable）版，系统的核心功能已经可以使用。意思就是基本上可以使用了。|
|"PRE(M1 M2)"|里程碑版|"由于GA版还不属于公开发行版，里面还有功能不完善的或者一些bug，于是就有了milestone（里程碑）版，milestone版本主要修复一些bug和调整ui。一个GA后，一般有多个里程碑，例如  M1  M2 M3.......|
|RC	|候选发布版|从 BUILD 后 GA 再到 M  基本上系统就定型了，这个时候系统就进入Release Candidates（RC候选发布）版，该阶段的软件类似于最终发行前的一个观察期，该期间只对一些发现的等级高的bug进行修复，发布RC1，RC2等版本。|
|SR|正式发布版|	公开正式发布。正式发布版一般也有多个发布，例如SR1  SR2 SR3等等，一般是用来修复大BUG或优化。|

