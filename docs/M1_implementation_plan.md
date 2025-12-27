# M1 实施计划 - 多平台商品销售与库存管理 PoC

## 概述

根据PRD文档，M1（最小可交付版本）的目标是实现一个支持抖音或拼多多任一平台接入的系统，具备日汇总、简单Dashboard和手动库存下发功能。

## 项目结构

```
docs/
├── M1_implementation_plan.md
src/
├── main/
│   ├── java/com/multishop/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── mapper/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   └── resources/
│       └── templates/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README-POC.md
```

## Sprint 0（准备）— 已完成
- 初始化仓库结构、创建分支 `feature/m1-poc`、新增 `docs/` — 完成
- 准备开发环境（Postgres Docker 容器、Redis 可选） — 完成
- 基础项目架构搭建（Spring Boot, JPA, Thymeleaf） — 完成
- 基础实体、服务、控制器创建 — 完成

## Sprint 1（核心模型与数据层）— 3 天
- 设计并实现核心数据模型（products, daily_sales, inventory_snapshots） — 1d
- 实现 Product 相关的 Repository、Service、DTO 和 Mapper — 1d
- 实现数据库连接配置（H2/Postgres） — 0.5d
- 实现基础的 REST API 端点 — 0.5d

## Sprint 2（UI 和 Dashboard）— 3 天
- 创建 Dashboard 页面，展示商品列表和库存信息 — 1d
- 实现基础的前端页面（Thymeleaf 模板） — 1d
- 添加示例数据和页面交互功能 — 1d

## Sprint 3（库存同步功能）— 3 天
- 实现库存同步的 API 端点 — 1d
- 实现手动库存下发功能 — 1d
- 添加错误处理和重试机制 — 1d

## Sprint 4（集成与测试）— 2 天
- 集成各模块并进行全面测试 — 1d
- 修复缺陷并优化性能 — 1d

## 技术实现细节

### 数据模型
- Product：商品基本信息（SKU、名称、各平台库存）
- DailySales：每日销售汇总数据
- InventorySnapshot：库存快照

### API 接口
- `GET /api/products` - 获取所有商品
- `POST /api/products` - 添加新商品
- `PUT /api/products/{id}/stock` - 更新库存
- `GET /` - Dashboard 页面

### 配置和部署
- 使用 Spring Boot 3.3.0
- 支持 H2（本地）和 PostgreSQL（Docker）数据库
- Docker Compose 部署环境

## 风险和注意事项
- 平台 API 限流策略需考虑
- 数据一致性保证
- 错误处理和重试机制
- 安全性（凭证加密存储）

## 完成标准
- 能够展示商品列表
- 能够手动更新各平台库存
- 数据持久化到数据库
- Docker 环境正常运行
- 基础 UI 能够访问和操作

# M1 实施计划（PoC）

版本：0.1

目标：实现最小可交付 PoC（M1），覆盖以下功能：
- 接入任意一平台（抖音 或 拼多多）或使用模拟器
- 每日汇总（按商品、店铺）并产生 `daily_sales`
- 简单 Dashboard（商品趋势折线图）
- 手动/计划触发的批量库存下发（异步任务、回执记录、重试）

总体周期建议：3 周（单个开发者/小团队），按 2 周冲刺 + 1 周缓冲/验收

---

## 前置条件/输入

- 平台 API 文档与测试凭证（若无凭证可用模拟器）
- 开发环境：Java 17, Maven, Node 18+, Docker
- 目标运行环境：Postgres（本地或托管）、Redis（可选）

---

## 交付物

- 可运行的后端服务（Spring Boot） + 最小前端（React）
- Postgres schema（含 Flyway/Liquibase 脚本）
- Dockerfile 与 docker-compose.yml（用于本地 PoC 部署）
- 文档：部署说明、用户手册、接口文档（OpenAPI）
- 自动化测试：单元与集成测试套件

---

## 里程碑与任务分解（详）

Sprint 0（准备） — 2 天
- 收集接入信息（店铺清单、API 文档、测试凭证） — 0.5d
- 初始化仓库结构、创建分支 `feature/m1-poc`、新增 `docs/` — 0.5d
- 准备开发环境（Postgres Docker 容器、Redis 可选） — 1d

Sprint 1 — 7-9 天
1. 数据模型与迁移（1.5d）
   - 设计并实现 Postgres 表（products, stores, orders, inventory_snapshots, daily_sales, sync_jobs, audit_logs）
   - 编写 Flyway/Liquibase 脚本
   - 验证迁移可在本地容器中运行

2. Connector 基础（抖音或拼多多）（2.5d）
   - 实现一个可配置的 connector 框架（HTTP 客户端、认证、限流模块、重试）
   - 实现拉取订单与库存的示例接口（支持增量拉取）
   - 将拉取到的数据写入 `orders` / `inventory_snapshots`

3. ETL/Worker（2d）
   - 实现事件标准化与去重逻辑
   - 实现基础 Worker（可使用 Spring Scheduler 或队列 + Worker）

4. Aggregator（1d）
   - 实现每日汇总逻辑（按 sku 与 store 聚合）
   - 提供手动触发接口或 CLI 用于回填/重算

Sprint 2 — 5-6 天
5. API 服务（1.5d）
   - 实现 REST API：/daily-sales, /trends, /inventory/sync、/sync/tasks
   - 返回格式遵循 OpenAPI，编写基本文档

6. 库存下发任务（2d）
   - 实现异步任务队列（RabbitMQ / Redis Queue / 内存队列）
   - 批量下发实现（batch_id、幂等、回执记录、失败重试）

7. 前端 Dashboard（最小可视化，1.5d）
   - 简单 React 页面：商品列表、选择 SKU 后展示趋势折线图、库存下发表单（CSV 上传 + 预览）

Sprint 3 — 缓冲 + 测试 + 部署（3-4 天）
8. 测试与集成（1.5d）
   - 单元测试（Connector 模拟、ETL 逻辑）
   - 集成测试：在本地 docker-compose 模式下跑 end-to-end

9. 容器化与部署（0.5d）
   - 后端/前端 Dockerfile、docker-compose.yml
   - 部署说明文档

10. 验收、文档与演示（1d）
   - 准备验收清单：每日汇总生成、趋势查询、库存下发并记录回执
   - 演示给产品/运营并收集反馈

---

## 估时汇总（建议）
- 总计：约 3 周（15 工作日）
  - 准备：2 日
  - 开发：12 日
  - 验收/缓冲：1 日

注：若团队多人并行（1 backend + 1 frontend + 1 devops），可缩短到 1-1.5 周。

---

## 技术选型（PoC 推荐）
- 后端：Spring Boot (Java 17) — 与现有项目技术栈一致
- DB：Postgres（PoC 使用 docker compose）
- 前端：React + Vite（轻量）
- 队列：Redis Streams / RabbitMQ（取决于运维熟悉度）
- CI：GitHub Actions（或 GitLab CI）
- 部署：Docker Compose（PoC），生产建议 Kubernetes

---

## 验收标准（M1）
- 能拉取并存储至少 7 天的原始订单/库存事件
- 每日作业能在 01:00 后生成 `daily_sales`（可手动触发并得出相同结果）
- Dashboard 能够展示某 SKU 的日销量折线图
- 能将一次批量库存下发到平台（或模拟器），并记录回执成功/失败
- 自动化测试覆盖关键模块（Connector, ETL, Aggregator）

---

## 风险与缓解
- 平台 API 限流或变更：缓解方案为可配置限速、分片下发、人工回退
- 测试凭证不可得：使用模拟器或调整 PoC 范围以使用 mock 数据
- 数据一致性问题：实现幂等设计、回溯重算逻辑与人工审核流程

---

## 交付清单（交付时应提供）
- 源码在 `feature/m1-poc` 分支
- `docker-compose.yml` 用于一键启动（Postgres + 后端 + 前端）
- Flyway/Liquibase migration scripts
- README（快速开始、如何运行本地 PoC、如何提供店铺凭证）
- OpenAPI 文档（或 Swagger UI）

---

## 后续建议
- 在 M1 稳定后，优先做：多平台并行、任务中心、监控告警与回滚机制
- 引入 OLAP（ClickHouse）以支持大规模历史数据分析与快速聚合

## 学习与实践路线（可选，推荐加入到团队培训）

本节给出把 M1 实施计划与个人/团队学习路线结合起来的建议，适合想同时学习技术栈并在项目中实战的同学。

- 学习顺序（建议）：
   1. Spring Boot（后端基础：REST、Data JPA、配置、profiles）
   2. Postgres 与数据库建模（迁移工具 Flyway/Liquibase）
   3. React + Vite（前端展示与调用后端 API）
   4. 异步消息（RabbitMQ 或 Redis Streams，了解幂等/重试模型）
   5. 容器化（Docker / docker-compose）与本地集成测试
   6. Kubernetes（Minikube/Kind）与部署演练

- 每步落地要点（在本项目中）：
   - Spring Boot：实现 products / daily-sales 的 REST API，并能在本地用 Maven 启动调试。
   - Postgres：为 PoC 加入 Flyway 初始迁移脚本，使用 docker-compose 启动数据库并验证迁移。
   - React：在 `frontend/` 下用 Vite 建最小页面，展示商品列表与折线趋势图。
   - 消息队列：在 `docker-compose` 中加入 RabbitMQ（或 Redis），把库存下发实现为消息任务，编写简单 consumer 写回 `sync_jobs`。
   - 容器化：为后端和前端编写 Dockerfile，能用 `docker compose up --build` 一键启动全量 PoC。
   - Kubernetes：可选，把 PoC 部署到本地 k8s 验证配置与 Secrets 管理。

- 验收建议（对学习效果的衡量）：
   - 能在本地用 IDE 调试后端并通过 Postgres 持久化数据；
   - 前端能调用 API 并展示图表；
   - 提交一次批量库存下发任务后，消息能被 Worker 消费并在 DB 中记录回执；
   - 能使用 docker-compose 启动完整栈并做完整端到端演示。

---

### pgAdmin
1. 更新了docker-compose.yml文件：
添加了pgAdmin服务，使用dpage/pgadmin4:latest镜像
配置了默认的登录凭据（邮箱：admin@admin.com，密码：admin）
映射端口5050到pgAdmin的Web界面
设置了数据卷以持久化pgAdmin配置
添加了对db服务的依赖
2. 更新了pom.xml文件：
添加了spring-boot-starter-jdbc依赖，以便DebugController可以使用JdbcTemplate
3. 创建了DebugController：
允许通过浏览器查看数据库表结构和数据
提供了查看不同表的端点
现在，您可以按以下步骤使用pgAdmin：

重新构建并启动Docker Compose服务：
bash
docker-compose up --build
访问pgAdmin：
打开浏览器并导航到 http://localhost:5050
使用邮箱 admin@admin.com 和密码 admin 登录
在pgAdmin中连接到您的数据库：
右键点击 "Servers"，选择 "Create" -> "Server"
在 "General" 选项卡中，设置名称为 "multishop-db"
在 "Connection" 选项卡中：
Host name/address: db (Docker容器名称)
Database: multishop
Username: postgres
Password: postgres
使用DebugController查看数据：
访问 http://localhost:8080/debug/tables 查看表列表
访问 http://localhost:8080/debug/products 查看产品数据
访问 http://localhost:8080/debug/dailysales 查看每日销售数据
访问 http://localhost:8080/debug/inventorysnapshots 查看库存快照数据
这样，您就可以通过pgAdmin图形界面或通过DebugController的Web页面来查看和管理数据库中的数据了。