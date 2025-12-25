# 多平台商品销售与库存管理 PRD

版本：0.1

最后更新：2025-12-24

## 概要

目标是为多家抖音店与拼多多店提供统一的销售汇总、趋势分析与库存下发能力。系统每日汇总各店铺商品销量与库存快照，提供趋势可视化并支持将统一库存批量下发到各平台，同时记录审计与回执以保证幂等性与可追溯。

核心需求：
- 每日汇总商品的销售量与库存快照
- 支持按日/周/月的销售趋势分析与异常检测
- 支持批量/统一更新库存到多个平台，保证幂等、回执与重试

非功能性要求（NFR）
- 可扩展性：支持更多店铺与高并发拉取
- 可用性：关键任务（每日汇总）成功率 >= 99%
- 安全性：凭证加密、操作审计
- 可维护性：良好的日志与监控、清晰的回滚路径


## 使用者画像

- 运营人员：查看日销量、筛选滞销或热销商品、批量下发库存。需要直观的 dashboard 与批量操作界面。
- 技术人员/运维：关注同步任务健康、错误日志、系统监控与扩缩容。
- 管理者：查看业务指标（总销售额、库存周转、异常告警）。


## 范围与里程碑

M1（最小可交付）
- 支持抖音或拼多多任意一平台接入（可模拟器替代）
- 日汇总（按商品、店铺）与简单 Dashboard（折线图）
- 能手动或计划触发库存批量下发（记录回执）

M2
- 多平台并行接入、任务中心、重试与告警
- 大规模数据存储（分为 OLTP/OLAP）与导出功能

M3
- 高级预测（季节性）、异常检测、权限与审计完善


## 功能性需求

1. 日汇总
- 每日自动（默认凌晨 01:00）统计上一日每个商品在每家店铺的销售量（sold_qty）与销售额（sold_amount）。
- 支持手动重新生成某日汇总（幂等）。

2. 趋势分析
- 支持按天/周/月聚合的折线图，支持比较（环比/同比）。
- 提供 Top-N（销量、销售额）、滞销列表、库存周转率指标。
- 支持导出 CSV/Excel。

3. 统一更新库存
- 支持从 central inventory 发起对接店铺的批量库存下发（overwrite 或 adjust 模式）。
- 批量下发为异步任务：返回 task id，任务执行结果（成功/失败/部分成功）可查询并导出。
- 下发需记录每条下发请求的回执（成功/失败码与消息），支持按失败原因重试。
- 幂等保障：每次批量下发含 batch_id，平台重复请求不重复扣减库存。


## 非功能性需求

- 接口限流：对平台 API 做并发控制与速率限制（配置化）。
- 数据保留：原始事件（订单、库存快照）保留至少 90 天，汇总数据保留至少 2 年。
- 安全：凭证加密存储（例如 KMS/Vault），操作记录与审计日志保留 1 年。
- 可观测性：关键指标（任务成功率、队列长度、同步延时）应有告警阈值。


## 架构概览

组件：
- Connectors（抖音/拼多多适配器）
- ETL/Sync Service（事件标准化、去重、入库）
- Scheduler（定时任务）与 Worker Queue（处理下发、重试）
- 数据存储：Postgres (OLTP) + ClickHouse/物化视图 (OLAP) + Redis 缓存
- API 服务（REST）与 Frontend Dashboard
- Audit & Logging & Monitoring

部署选项：Docker + Kubernetes（或 Docker Compose 用于 PoC）


## 数据模型（核心表）

示例（简化，Postgres）：

- stores
  - id (PK), platform ENUM("douyin","pdd"), platform_store_id TEXT, name TEXT, credentials_ref TEXT, enabled BOOL

- products
  - id (PK), sku TEXT, title TEXT, platform_meta JSONB, created_at, updated_at

- orders (原始事件)
  - id (PK), platform_order_id, store_id, product_id, sku, qty INT, amount NUMERIC, status, created_at, raw_payload JSONB

- inventory_snapshots
  - id, product_id, store_id, sku, quantity INT, snapshot_time TIMESTAMP

- daily_sales
  - id, product_id, store_id, date DATE, sold_qty INT, sold_amount NUMERIC, created_at

- sync_jobs
  - id, job_type ENUM("inventory_sync","order_sync"), store_id, status ENUM, started_at, finished_at, meta JSONB

- audit_logs
  - id, actor, action, resource_type, resource_id, request JSONB, response JSONB, created_at


## 平台接入（Connector）设计要点

- 支持 OAuth/token 刷新、签名算法、限流策略。将平台差异封装，向上游暴露统一事件格式：
  ```json
  {
    "platform": "douyin",
    "store_id": "xxxx",
    "event_type": "order|inventory",
    "event_id": "...",
    "timestamp": "...",
    "payload": { ... }
  }
  ```
- 优先使用平台提供的增量 API（updated_at、cursor），若仅支持全量则采用事件去重策略（基于事件ID或 order_id）。
- 并发控制：每个 store 一个队列或 semaphore，配置每秒请求上限。


## 同步/ETL 流程

1. Connector 拉取原始数据 -> 入队到事件队列（Kafka/RabbitMQ）
2. Worker 消费事件，标准化字段，写入 `orders` / `inventory_snapshots` 原始表
3. Aggregator（可周期性触发）计算 `daily_sales`：基于订单状态筛选（例如已支付/已完成）并按 sku 聚合
4. Snapshot job：每日对每个 store 的库存做快照并写入 `inventory_snapshots`

错误处理：事件处理失败记录到 `sync_jobs` 与 `audit_logs`，重试队列带指数退避


## API 设计（关键接口）

- GET /api/v1/daily-sales?date=YYYY-MM-DD&store_id=&sku=
- GET /api/v1/trends?sku=&start=&end=&granularity=day|week|month
- POST /api/v1/inventory/sync  
  请求体：{ "batch_id":"...", "items": [{"store_id":1,"sku":"SKU1","quantity":100}], "mode":"overwrite|adjust" }
  返回：{ "task_id":"..." }
- GET /api/v1/sync/tasks/{task_id}

幂等性：`batch_id` 保证重复请求不重复执行。下发结果记录到 `audit_logs` 与 `sync_jobs`。


## UI/前端需求（页面列表）

- Dashboard 总览（销售、订单、库存、同步失败）
- 商品列表（可按 SKU/店铺/分类筛选）
- 商品详情（趋势图 + 日销量表）
- 库存下发页（上传 CSV 或表单填写，预览 -> 下发 -> 查看回执）
- 任务中心（同步历史、重试）


## 异常与冲突处理策略

- 订单取消/退款：在 `daily_sales` 中单独记录退款量，不直接修改历史销量（可配置）。
- 库存冲突：若下发失败或回执与 central inventory 有大差异，触发人工审核并提供回滚入口。
- API 限流：实现队列节流与任务拆分，失败重试并告警运营人员。


## 安全与权限

- 凭证加密：使用 KMS/Vault 存储平台凭证，应用只持有解密权限
- API 认证：JWT + RBAC
- 操作审计：所有下发和手动操作写 `audit_logs`


## 监控与运维

- 关键指标：daily job 成功率、API 错误率、队列长度、任务平均延迟
- 日志：集中化采集（ELK/EFK），错误告警发到企业微信/钉钉/邮件
- 部署：镜像仓库 + Kubernetes 部署（建议）


## 测试计划

- 单元测试：Connector 的 API 适配层、ETL 转换逻辑
- 集成测试：模拟平台返回、端到端每日汇总
- 灰度：先在少量店铺上启用自动下发，再扩大到全部店铺


## Roadmap / 交付建议

- Week 0-2：确定店铺清单与 API 凭证，准备开发环境、数据库 schema
- Week 2-6：完成 M1（单平台 PoC、每日汇总、基础 Dashboard、手动库存下发）
- Week 6-10：加入第二平台、任务中心、重试策略与监控
- Week 10-14：优化性能、引入 OLAP、预测与异常检测


## Appendix：示例 SQL（Postgres）

```sql
CREATE TABLE products (
  id serial PRIMARY KEY,
  sku text UNIQUE,
  title text,
  platform_meta jsonb,
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

CREATE TABLE daily_sales (
  id serial PRIMARY KEY,
  product_id int references products(id),
  store_id int,
  date date,
  sold_qty int,
  sold_amount numeric(12,2),
  UNIQUE(product_id, store_id, date)
);

CREATE TABLE inventory_snapshots (
  id serial PRIMARY KEY,
  product_id int,
  store_id int,
  sku text,
  quantity int,
  snapshot_time timestamptz default now()
);
```


---

如需，我可以：
- 把本 PRD 转为 PDF 或放到仓库的 `docs/` 下并创建相应 issue/任务列表；
- 生成 M1 的详细实施计划（任务分解、估时、必要的样例代码模板）；
- 直接开始实现 PoC（需要你提供测试店铺凭证或我使用模拟器）。

请选择下一步：生成实施计划 / 创建 docs 文件夹并提交 PRD / 开始 PoC 实现。