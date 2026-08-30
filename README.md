# 研王爷 · 考研伴学系统

基于 Spring Boot 3 + React 18 的前后端分离项目骨架。**一期闭环 + 二期接口预留**。

## 目录结构

```
.
├── backend/                  # Spring Boot 3.2 (Java 17)
│   └── src/main/
│       ├── java/com/yanyan/
│       │   ├── common/       # 通用：统一返回 R / 全局异常 / BaseEntity / 结果封装
│       │   ├── config/       # MyBatis-Plus / Jackson / Redis / 审计填充
│       │   ├── security/     # JWT 认证 + RBAC(SecurityConfig) + LoginUser(ThreadLocal)
│       │   ├── event/        # 领域事件(一期发布,二期消费:卡片/时长/打卡)
│       │   ├── task/         # 定时任务(Anki 复习提醒等)
│       │   └── module/
│       │       ├── auth      # 账号权限(一期,已有)
│       │       ├── study     # 学习计时/签到/日程(一期,已有)
│       │       ├── wrongbook # 错题本 + Anki(一期)
│       │       ├── anki      # SM-2 算法 + 统一复习调度(错题/背诵共用)
│       │       ├── recite    # 背诵打卡(一期)
│       │       ├── plan      # 计划模板导入(一期)
│       │       ├── rag       # RAG 溯源答疑(一期, AiProvider 可切换 Mock)
│       │       ├── dashboard # 数据大盘(一期)
│       │       └── community|group|school  # 二期预留占位包
│       └── resources/
│           ├── application.yml  # 主配置
│           ├── application-dev.yml
│           └── db/init.sql      # 数据库初始化(一期表 + 二期空表预埋)
├── frontend/                 # React 18 + Vite + TS (动态强交互)
│   └── src/
│       ├── api/http.ts       # axios 封装(JWT 注入/401 跳转/统一 R)
│       ├── stores/auth.ts    # Zustand 登录态
│       ├── components/       # 布局 / 路由守卫
│       └── features/         # 按域拆分(study/wrongbook/recite/rag/plan/dashboard + _phase2)
└── docker-compose.yml        # MySQL8 / Redis7 / MinIO
```

## 快速启动

1. 启动基础设施（MySQL + Redis）：
   ```bash
   docker compose up -d
   ```
   首次启动自动执行 `backend/src/main/resources/db/init.sql` 建库建表。

2. 启动后端（Java 17+，内置 Maven Wrapper，无需单独装 Maven）：
   ```bash
   cd backend
   .\mvnw.cmd spring-boot:run   # Windows
   # ./mvnw spring-boot:run    # macOS / Linux
   # 接口文档: http://localhost:8080/swagger-ui.html
   ```

3. 启动前端：
   ```bash
   cd frontend
   npm install
   npm run dev
   # 访问: http://localhost:3000  (已代理 /api -> 8080)
   ```

## 一期范围（已锁定）

| 模块 | 状态 | 说明 |
|---|---|---|
| 账号权限 | ✅ 已有 | Security + JWT + Redis 会话，RBAC 五表 |
| 学习计时 / 日程 | ✅ 已有 | 计时落库 → 领域事件 |
| 错题本 + Anki | 🆕 骨架 | SM-2 算法 + 统一调度器 |
| 计划模板导入 | 🆕 骨架 | 模板 → 批量生成日程 |
| RAG 溯源答疑 | 🆕 骨架 | AiProvider 抽象(可切换 Mock/商用) |
| 背诵打卡 | 🆕 骨架 | 与错题共用 Anki 调度 |
| 数据大盘 | 🆕 骨架 | study_stat_daily 预聚合占位 |

## 二期预留（不返工设计）

在 `_phase2` / `module.{community,group,school}` 中，均已预埋：
- **权限点**：`community:*` `group:*` `school:view`（默认不分配角色）
- **数据表**：post / comment / group / school / user_target 等空表已在 `init.sql`
- **领域事件**：`StudyCompletedEvent` `CheckinEvent` 一期发布，二期社区/小组排行榜直接监听
- **前端**：`/community` `/group` `/school` 占位路由 + 侧边栏「二期」灰态入口

## 技术要点

- **前后端分离**：单体起步，预留 `/api/v1/**` 版本化与 `features/*` 模块化，二期模块即插即用
- **强交互前端**：Framer Motion 动效、TanStack Query 乐观更新、Tailwind + AntD 组合，动效尊重 `prefers-reduced-motion`
- **AI 可切换**：`AiProvider` 接口 + `yanyan.ai.provider` 配置，Mock 保联调，商用模型二期接入
- **安全**：JWT 无状态认证 + Redis 二次鉴权（支持主动下线），@PreAuthorize 方法级权限