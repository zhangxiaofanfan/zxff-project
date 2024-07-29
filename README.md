# zxff-project

## 1. 项目结构

> 项目整体主要分为两个部分, 项目地址: https://gitee.com/zhangxiaofanfan/zxff-project, 参考项目地址: https://github.com/YunaiV/yudao-cloud
>
> - 后端项目
> - 前端项目(zxff-project-ui)

后端项目:

- zxff-dependencies: BOM依赖管理
  - *BOM是什么*: https://juejin.cn/post/7062238313461219341
  - 宗旨: 除了 BOM 中存在版本号, 其他地方只需要引入依赖坐标即可, BOM 做项目版本管理;
- zxff-module-gateway: 项目网关
- zxff-module-sso: 项目单点登录模块
- zxff-module-corntab: 项目定时任务模块
- zxff-module-spring-boot-admin: 项目 spring-boot-admin 监控模块

前端项目:

- zxff-ui-admin-vue3: 使用 vue3 技术的 zxff 项目 admin 管理前端
- zxff 项目微信小程序项目, 主要是下单功能 TODO

中间件:

- 注册中心: nacos
- 消息中间件: kafka

## 2. 后端项目

### 2.1 业务模块

> 业务模块标识: ${priject_path}/zxff-module-{module_name}

#### 1. zxff-module-sso

- 名称: zxff-module-sso

- 功能: 为系统提供单点登录服务;

- 功能介绍: 
  - 登录功能: 为

### 2.2 功能模块

> 功能模块标识: ${priject_path}/zxff-framework/zxff-spring-boot-starter-{function_name}

#### 1. zxff-spring-boot-starter-api-doc

- 名称: zxff-module-sso

- 功能: 为系统提供单点登录服务;

- 功能介绍: 
  - 登录功能: 为

#### 2. zxff-spring-boot-starter-monitor

#### 3. zxff-spring-boot-starter-mybatis

#### 4. zxff-spring-boot-starter-permission

#### 5. zxff-spring-boot-starter-redis

#### 6. zxff-spring-boot-starter-rpc

#### 7. zxff-spring-boot-starter-security

#### 8. zxff-spring-boot-starter-web



### 2.3 通用模块

- 名称: zxff-module
- 功能: 为系统提供通用功能支撑;
- 功能介绍



## 3. 前端项目

## 4. 中间件

## 5. 写在最后

## 5.1 前端知识点

### 1. 钩子函数

- `onMount()` 函数:

  - 官网链接: https://cn.vuejs.org/api/composition-api-lifecycle.html#onmounted
  - 注册一个回调函数，在组件挂载完成后执行
  - 解释: 当组件 `LoginForm` 被使用加载完成之后调用 *getCookie* 和 *getTenantByWebsite* 函数;

  <img src=".images/onMounted%E5%87%BD%E6%95%B0%E4%BD%BF%E7%94%A8.png" alt="image-20240704153951223" style="zoom:50%;" />

- `watch()` 函数:

  - 官网链接: https://cn.vuejs.org/api/reactivity-core.html#watch
  - 用于声明在数据更改时调用的侦听回调。
  - 解释: 

<img src=".images/watch%E5%87%BD%E6%95%B0%E4%BD%BF%E7%94%A8.png" alt="image-20240704154312193" style="zoom:50%;" />
