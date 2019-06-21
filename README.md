# 钉钉年假插件

#### 简介

钉钉年假插件是专门给企业内部使用的一款基于钉钉接口数据的插件，
它能让企业自行设置计算年假方式，达到根据企业的年假规则给予员工年假天数，仅限企业内部使用。

#### 第三方包引用

将lib下的两个jar加入到本地的maven仓库
参考命令：
mvn install:install-file -Dfile=D:\IdeaProjects\dingtalk\lib\lippi-oapi-encrpt.jar -DgroupId=com.laiwang.lippi -DartifactId=lippi.oapi.encryt -Dversion=1.0.4 -Dpackaging=jar
mvn install:install-file -Dfile=D:\IdeaProjects\dingtalk\lib\taobao-sdk-java-auto.jar -DgroupId=com.taobao.sdk -DartifactId=taobao.sdk.java.auto -Dversion=1.0 -Dpackaging=jar
在项目的pom.xml的dependencies中加入以下内容:
<dependency>
    <groupId>com.laiwang.lippi</groupId>
    <artifactId>lippi.oapi.encryt</artifactId>
    <version>1.0.4</version>
</dependency>
<dependency>
    <groupId>com.taobao.sdk</groupId>
    <artifactId>taobao.sdk.java.auto</artifactId>
    <version>1.0</version>
</dependency>

#### 钉钉开发者平台自建应用

选择企业内部开发 -> 小程序
应用首页：填写相关信息，服务器公网出口IP名单填公司出口IP，在本地开发时需加入钉钉返回的白名单IP，
人员设置：设置开发人员
安全域名设置：加入本地的IP，否则访问不了，上线后加入对外域名
版本管理：可设置开发板，体验版，线上版本
接口权限：高级权限-企业通讯录开通通讯录只读权限，高级权限-微应用开通审批，智能人事权限，具体权限根据业务进行开通
具体可阅读钉钉开发文档

#### 项目结构

Springboot搭建，maven管理

com.ipayroll.dingtalk.common - 公共包
com.ipayroll.dingtalk.config - 配置包
com.ipayroll.dingtalk.controller - controller层
com.ipayroll.dingtalk.data - 基类包
com.ipayroll.dingtalk.entity - entity层
com.ipayroll.dingtalk.enums - 枚举类包
com.ipayroll.dingtalk.exception - 异常处理层
com.ipayroll.dingtalk.job - 定时任务层
com.ipayroll.dingtalk.repository - JPA接口层
com.ipayroll.dingtalk.service - service层
com.ipayroll.dingtalk.util - 工具包
com.ipayroll.dingtalk.view - view层

#### 主要业务介绍

注册事件回调接口（/registerCallBack）：如果有需要对事件进行监听，比如钉钉上审批的事件等等，必须先注册这个事件。可根据企业自身需要注册需要回调的事件，但回调地址是唯一的，因为钉钉对每个企业只提供唯一一个注册的回调地址，所以上线后可调用更新事件回调接口（/updateCallBack）更新回调地址。

审批回调逻辑：当审批人在钉钉上审批之后，钉钉回调给注册审批事件的回调接口，每个事件都有开始和结束，可以根据回调数据获取到审批实例的id（processInstanceId），去调用钉钉提供的获取审批实例详情接口拿到具体信息，进行业务处理。因为钉钉目前没有提供专门获取年假接口，所以只能通过这种方式获取到请假信息

定时任务：synDataJob()每天定时去获取钉钉上最新的员工信息，并计算年假，所得数据写入到本地数据中。

#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request


#### 码云特技

1. 使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2. 码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3. 你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4. [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5. 码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6. 码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)