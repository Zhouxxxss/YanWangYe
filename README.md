# 研王爷 · 考研伴学系统

基于 **Spring Cloud Alibaba 微服务 + React 18** 的前后端分离系统。**一期闭环 + 二期接口预留**。

## 架构

```
frontend ──HTTP──▶ ywy-gateway (8100) ──lb://──▶ ywy-auth (8102)
        JWT 鉴权/限流/路由          Nacos 服务发现    ywy-user (8103)
                                                      ywy-study (8104)
                                                      ywy-rag (8105)
                                                      ywy-community (预留,二期)
                                                      ywy-shopping (预留,二期)
```

- **注册中心 / 配置中心**：Nacos（命名空间 `yanyan`）
- **服务间通信**：OpenFeign
- **认证**：网关 `AuthGlobalFilter` 集中 JWT 鉴权，身份信息经 Header 透传下游；网关内置令牌桶限流
- **身份模型**：单一 `user` 表（凭据 + 档案合并，无独立认证表）。认证链路 = JWT 签名 + Redis 双 token 会话（access 滑动 / refresh 轮换），`logout` / `refresh` 走 Redis 校验，不按请求查库
- **公众号关注自动注册**：auth 服务经可插拔 `WxMpClient` 适配器（未配 appid/secret 走 Mock 联调），account 记 openid，默认用户名/密码为空，用户在「我的」页补设
- **存储**：MySQL（业务数据）/ Redis（会话、Anki 到期队列）/ 对象存储（RustFS，RAG 模块可插拔）

## 目录结构

```
.
├── backend-ms/                 # Spring Cloud 微服务聚合工程
│   ├── ywy-gateway/            # 网关：路由 / JWT 鉴权 / 限流
│   ├── ywy-common/             # 通用：统一返回 R / 异常 / JwtUtils / 公共组件
│   ├── ywy-api/                # Feign 接口 + 跨服务 DTO
│   ├── ywy-auth/               # 认证授权（登录/注册/刷新/退出）
│   ├── ywy-user/               # 用户档案 / 好友
│   ├── ywy-study/              # 错题 / 背诵 / 学习计时 / 计划模板 / 遗忘曲线 / 数据大盘
│   ├── ywy-rag/                # RAG 知识库：文件分片上传 / 向量检索 / 智能溯源答疑
│   ├── ywy-community/          # 二期预留（发帖 / 好友聊天）
│   ├── ywy-shopping/           # 二期预留（二手资料 / 商家入驻）
│   ├── deploy/sql/init.sql     # 数据库初始化
│   └── docker-compose.yml      # MySQL / Redis / Nacos 基础设施编排
├── frontend/                   # React 18 + Vite + TS (动态强交互)
│   └── src/
│       ├── api/http.ts         # axios 封装(JWT 注入/401 跳转/统一 R)
│       ├── stores/auth.ts      # Zustand 登录态
│       ├── components/         # 布局 / 路由守卫
│       └── features/           # 按域拆分(study/wrongbook/recite/rag/plan/dashboard)
└── 研王爷-技术栈与架构方案.html
```

## 快速启动

前置：安装 Java 17+、Maven、Docker。

1. 启动基础设施（MySQL + Redis + Nacos）：
   ```bash
   cd backend-ms
   docker compose up -d
   ```
   首次启动自动执行 `deploy/sql/init.sql` 建库建表。Nacos 控制台 `http://localhost:8848/nacos`。

2. 打包并启动各微服务（独立 JVM 进程）：
   ```bash
   cd backend-ms
   mvn -DskipTests package
   # 分别以独立进程启动，端口见上：
   java -jar ywy-gateway/target/ywy-gateway-0.1.0.jar   # 8100
   java -jar ywy-auth/target/ywy-auth-0.1.0.jar         # 8102
   java -jar ywy-user/target/ywy-user-0.1.0.jar         # 8103
   java -jar ywy-study/target/ywy-study-0.1.0.jar       # 8104
   java -jar ywy-rag/target/ywy-rag-0.1.0.jar           # 8105
   ```

3. 启动前端：
   ```bash
   cd frontend
   npm install
   npm run dev
   # 访问: http://localhost:3000  (已代理 /auth、/users 等服务前缀 -> 网关 8100)
   ```

统一入口为网关 `http://localhost:8100`，请求按服务前缀（`/auth`、`/users`、`/study` 等）经网关鉴权并路由到对应微服务。

## 一期范围（已锁定）

| 模块 | 服务 | 状态 |
|---|---|---|
| 账号权限 | ywy-auth | ✅ JWT + Redis 会话，集中鉴权 |
| 用户档案 / 好友 | ywy-user | ✅ |
| 学习计时 / 日程 | ywy-study | ✅ |
| 错题本 + Anki | ywy-study | SM-2 算法 + 统一调度 |
| 计划模板导入 | ywy-study | 模板 → 批量生成日程 |
| 背诵打卡 | ywy-study | 与错题共用 Anki 调度 |
| RAG 溯源答疑 | ywy-rag | Agentic RAG + 向量检索 + 引用溯源 |
| 数据大盘 | ywy-study | 预聚合 + 热力图 |

## 二期预留（不返工设计）

- **ywy-community**（发帖 / 好友聊天）、**ywy-shopping**（二手资料 / 商家入驻）已建模块骨架
- RAG 的存储与向量组件通过接口抽象，`local | rustfs` 与 `memory | milvus` 可切换
- 网关已为社区 / 购物路由预留 `/community/**`、`/shopping/**`

## 技术要点

- **微服务独立部署**：每个模块独立 JVM 进程，经 Nacos 注册与发现，网关负载均衡路由
- **Service 层契约化**：每个业务 Service 为「接口 + impl」结构，接口定义服务契约
- **强交互前端**：Framer Motion 动效、TanStack Query 乐观更新、Tailwind + AntD 组合，动效尊重 `prefers-reduced-motion`
- **安全**：网关统一 JWT 鉴权（白名单放行 / Token 校验 / 身份 Header 透传）+ 令牌桶限流