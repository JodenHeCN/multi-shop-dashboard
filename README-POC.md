# PoC 快速启动说明

此 README 描述如何在本地使用 Docker Compose 启动 M1 PoC（包含 Postgres + 应用）。

先决条件
- Docker + Docker Compose 已安装（或 Docker Desktop）
- 项目已克隆到本地

快速运行（在项目根目录）

1. 构建并启动服务

```bash
# 从仓库根目录
docker compose up --build
```

2. 访问
- 应用: http://localhost:8080

## 本地优先开发流程（推荐）

建议先在本地（IDE + 本地或容器化 Postgres）跑通服务，再进行容器化验证。以下是推荐的本地优先步骤：

1) 启动 Postgres（推荐使用 Docker）：

```bash
docker run -d --name multishop-dev-db \
	-e POSTGRES_USER=postgres \
	-e POSTGRES_PASSWORD=postgres \
	-e POSTGRES_DB=multishop \
	-p 5432:5432 postgres:14
```

2) 使用 IDE（IntelliJ / VS Code Remote - WSL）运行后端并在 IDE 中调试：

```bash
# 在项目根
mvn -Dspring-boot.run.profiles=docker -Dfile.encoding=UTF-8 spring-boot:run
```

3) 本地验证 API 与 DB

- 访问： http://localhost:8080
- 验证表结构：使用 psql 或 GUI 工具连接到本地 Postgres（端口 5432），检查表是否存在。

4) 打包并在 Docker 中运行（容器化验证）

```bash
mvn -Dfile.encoding=UTF-8 package -DskipTests
docker build -t multi-shop-dashboard:local .
docker run -d --name multi-shop -p 8080:8080 \
	--env SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/multishop \
	--env SPRING_DATASOURCE_USERNAME=postgres \
	--env SPRING_DATASOURCE_PASSWORD=postgres \
	multi-shop-dashboard:local
```

说明：在 Windows/Docker Desktop + WSL 场景下，容器连接宿主 DB 可通过 `host.docker.internal`，如果 DB 也在容器中，则使用 `docker-compose` 网络服务名（例如 `jdbc:postgresql://db:5432/multishop`）。

5) 一键本地启动（docker-compose）

```bash
docker compose up --build
```

6) 常见问题排查
- 如果容器不能连到 DB，请检查 `SPRING_DATASOURCE_URL` 与网络；
- 如果端口被占用，使用 `ss -ltnp | grep 8080`（WSL）或 `netstat -ano | findstr 8080`（Windows）查找并释放。

如果你希望，我可以把上面的脚本片段写成 `scripts/dev-up.sh` / `scripts/dev-up.ps1` 并提交到仓库，方便一键启动。

说明
- Spring profile `docker` 会读取 `application-docker.properties`，默认连接到 `jdbc:postgresql://db:5432/multishop`（由 docker-compose 提供）
- 为 PoC 我们使用 `spring.jpa.hibernate.ddl-auto=update` 来方便演示；生产环境请使用 Flyway/Liquibase 并禁用 `update`

下一步
- 如果你希望我在仓库内创建 `feature/m1-poc` 分支并把这些文件提交，我可以继续（需要你确认允许我修改仓库）。
