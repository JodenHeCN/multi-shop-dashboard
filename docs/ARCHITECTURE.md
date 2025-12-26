# 项目架构说明（多店铺看板）

本文档说明代码仓库中的分层架构、从前端到后端的调用流程，并附带一个 Mermaid 架构图（可在 VS Code 中用插件预览）。

## 分层概览

- 表示层（Controller）
  - `com.multishop.controller.HomeController`：处理页面请求（`GET /`），返回视图 `index.html`。
  - `com.multishop.controller.ProductController`：提供 REST API（`/api/products`），返回或接收 DTO。
- 业务层（Service）
  - `com.multishop.service.ProductService`：封装业务逻辑、执行 DTO ↔ 实体 的转换、协调持久层。
- 持久层（Repository）
  - `com.multishop.repository.ProductRepository`：继承 `JpaRepository`，负责与数据库读写交互。
- 实体 / DTO
  - `com.multishop.model.Product`：JPA 实体映射数据库表 `products`。
  - `com.multishop.dto.ProductDto`：API 层使用的数据传输对象。
- 异常处理
  - `com.multishop.exception.ResourceNotFoundException` 与 `GlobalExceptionHandler`：统一把业务异常映射为 HTTP 404（对浏览器返回 HTML，对 API 返回 JSON）。

## 从前端到后端的调用流程（3 种典型场景）

1. 页面渲染（浏览器访问 `/`）
   - 浏览器发起 `GET /` → Spring MVC 路由到 `HomeController.index()`。
   - `HomeController` 调用 `ProductService.getAllProducts()`（服务返回 DTO 列表）。
   - `ProductService` 调用 `ProductRepository.findAll()` 获取实体，映射为 DTO。
   - `HomeController` 把 DTO 列表放入 `Model`，返回视图 `index`（Thymeleaf 渲染 `templates/index.html`）。

2. REST 列表 / 新增（API）
   - `GET /api/products` → `ProductController.list()` → `ProductService.getAllProducts()` → 返回 JSON 列表（DTO）。
   - `POST /api/products` (JSON body) → `ProductController.add(@RequestBody ProductDto)` → `ProductService.addProduct(productDto)` → 保存并返回 saved DTO。

3. 更新库存（PUT `/api/products/{id}/stock`）
   - `PUT` 到对应 URL，controller 从 path / params 中读取 `id`, `douyin`, `pdd`。
   - Controller 调用 `ProductService.updateStock(id, douyin, pdd)`：service 查询 entity、修改字段并保存；若不存在抛出 `ResourceNotFoundException`。
   - `GlobalExceptionHandler` 捕获该异常：若请求 Accept 包含 `text/html` 返回 `error.html`（用户友好页面），否则返回 JSON 错误体和 `404` 状态码。

## 架构图（Mermaid）

将下面的 Mermaid 源直接粘到 VS Code 中的 Markdown 文件并使用 Mermaid 预览插件查看：

```mermaid
flowchart LR
  subgraph Browser[Browser / Client]
    user[User]
  end

  user -->|GET /| HomeController[HomeController<br/>@Controller]
  HomeController -->|calls| ProductService[ProductService<br/>(returns DTOs)]
  ProductService -->|calls| Repo[ProductRepository<br/>extends JpaRepository]
  Repo --> DB[(Database)]
  HomeController -->|returns view| Template[index.html]

  user -->|GET /api/products| APIController[ProductController<br/>@RestController]
  APIController -->|calls| ProductService
  APIController -->|returns JSON| user

  ProductService -.->|throws| ResourceNotFoundException[ResourceNotFoundException]
  ResourceNotFoundException -->|handled by| GlobalExceptionHandler[GlobalExceptionHandler<br/>@ControllerAdvice]
  GlobalExceptionHandler -->|returns HTML or JSON| user

  classDef infra fill:#f9f,stroke:#333,stroke-width:1px;
  class Repo,DB infra;
```

## 在 VS Code 中查看 Mermaid 图（推荐方式）

1. 打开 `docs/ARCHITECTURE.md`（或把上面的 Mermaid 段粘到任意 `.md` 文件）。
2. 安装一个支持 Mermaid 的扩展（任选其一）：
   - 推荐：Markdown Preview Enhanced（扩展 ID: `shd101wyy.markdown-preview-enhanced`）
   - 轻量：Mermaid Preview（扩展 ID: `vstirbu.vscode-mermaid-preview`）
3. 在 VS Code 命令面板（Ctrl+Shift+P）输入并运行：
   - `Markdown: Open Preview to the Side`（打开 Markdown 预览）。
   - 或者使用扩展提供的 `Mermaid Preview` 命令来渲染 `.mmd` / Mermaid 块。

如果你想通过命令行安装扩展（Windows PowerShell），可以运行：

```powershell
code --install-extension shd101wyy.markdown-preview-enhanced
code --install-extension vstirbu.vscode-mermaid-preview
```

（注：安装扩展需要本地有 `code` 命令可用；也可在 VS Code 扩展商店中搜索并安装。）

## 备注与实践建议

- 持续使用 DTO 层，避免在 Controller/视图中直接暴露 JPA 实体。
- 在 Service 层添加 `@Transactional`（若操作包含多步写入或需回滚）。
- 为 Controller 的输入使用 `@Valid` + Jakarta Validation，并在 `GlobalExceptionHandler` 中处理校验错误以返回 400。
- 若需要可视化更复杂的架构（sequence diagrams、component diagrams），可以在 docs 下添加更多 Mermaid 图或导出 PNG 存档。

---

文件创建：`docs/ARCHITECTURE.md` — 包含分层说明、调用流程、Mermaid 架构图，以及在 VS Code 中安装与预览的说明。
