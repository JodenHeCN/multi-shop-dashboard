# syntax=docker/dockerfile:1.4
# 使用官方 Maven 镜像作为构建阶段
FROM maven:3.9 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 并下载依赖（利用 Docker 缓存）
COPY pom.xml .
# 使用 BuildKit 的 cache mount 缓存 Maven 本地仓库，避免每次重新下载依赖
# 需要在构建时启用 BuildKit（DOCKER_BUILDKIT=1）
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline

# 复制源码并打包
COPY src ./src
# 使用相同的 cache mount 在打包阶段重用已缓存的依赖
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests package

# 运行阶段：使用 Microsoft Container Registry 上的 Ubuntu-based OpenJDK 镜像（避免 Docker Hub 拉取超时）
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

# 安装 tzdata（避免时区警告）
# 使用 Debian/Ubuntu-based slim 镜像时要用 apt 而不是 apk
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
	&& apt-get install -y --no-install-recommends tzdata \
	&& rm -rf /var/lib/apt/lists/* \
	&& dpkg-reconfigure --frontend noninteractive tzdata || true

# 设置时区（可选）
ENV TZ=Asia/Shanghai

WORKDIR /app

# 从 builder 阶段复制 JAR
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "/app/app.jar"]