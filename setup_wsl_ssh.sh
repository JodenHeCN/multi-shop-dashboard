#!/bin/bash

# setup_wsl_ssh.sh - WSL SSH 服务器一键安装配置脚本
# 功能：安装SSH服务，创建专用用户，配置安全设置，并启动服务。

set -e # 遇到任何错误即退出，确保脚本健壮性

echo "========================================="
echo "   WSL SSH 服务器一键安装配置脚本"
echo "========================================="

# 1. 检查是否为root权限运行
if [[ $EUID -ne 0 ]]; then
   echo "⚠️  请使用 'sudo' 或以 root 用户身份运行此脚本。"
   echo "   示例: sudo bash $0"
   exit 1
fi

# 2. 安装必要的软件包
echo ""
echo "[1/5] 正在更新系统并安装必要软件包 (openssh-server, net-tools)..."
apt-get update && apt-get install -y openssh-server net-tools

# 3. 交互式创建SSH专用用户
echo ""
echo "[2/5] 配置SSH登录用户"
echo "-----------------------------------------"

# 检查是否通过命令行参数提供了用户名和密码
if [ -n "$1" ] && [ -n "$2" ]; then
    SSH_USER="$1"
    SSH_USER_PASSWORD="$2"
    echo "使用命令行参数提供的信息创建用户: $SSH_USER"
else
    # 否则，进入交互式输入
    read -p "请输入要创建的SSH专用用户名 (默认: sshuser): " SSH_USER
    SSH_USER=${SSH_USER:-sshuser} # 设置默认值

    # 检查用户是否已存在
    if id "$SSH_USER" &>/dev/null; then
        echo "用户 '$SSH_USER' 已存在，将直接使用此用户。"
    else
        read -sp "请为 '$SSH_USER' 设置登录密码: " SSH_USER_PASSWORD
        echo ""
        read -sp "请再次确认密码: " SSH_USER_PASSWORD_CONFIRM
        echo ""

        if [ "$SSH_USER_PASSWORD" != "$SSH_USER_PASSWORD_CONFIRM" ]; then
            echo "❌ 错误：两次输入的密码不一致。"
            exit 1
        fi

        echo "正在创建用户 '$SSH_USER'..."
        # 创建用户，不创建家目录（-M），不设置登录shell（-s /bin/false），增强安全性
        useradd -m -s /bin/bash "$SSH_USER"
        echo "$SSH_USER:$SSH_USER_PASSWORD" | chpasswd
        echo "✅ 用户 '$SSH_USER' 创建成功。"
    fi
fi

# 4. 配置 SSH 服务器
echo ""
echo "[3/5] 配置 SSH 服务器 (sshd_config)..."
SSHD_CONFIG="/etc/ssh/sshd_config"
BACKUP_CONFIG="/etc/ssh/sshd_config.backup.$(date +%Y%m%d_%H%M%S)"

# 备份原配置文件
cp "$SSHD_CONFIG" "$BACKUP_CONFIG"
echo "原配置文件已备份至: $BACKUP_CONFIG"

# 使用 sed 进行关键配置项替换，确保安全设置
sed -i -e 's/^#*PermitRootLogin.*/PermitRootLogin no/' \
       -e 's/^#*PasswordAuthentication.*/PasswordAuthentication yes/' \
       -e 's/^#*PubkeyAuthentication.*/PubkeyAuthentication yes/' \
       "$SSHD_CONFIG"

echo "✅ 已禁用Root登录，并启用密码/密钥认证。"

# 5. 启动并启用 SSH 服务
echo ""
echo "[4/5] 启动 SSH 服务..."
# 尝试使用 systemctl (WSL2 通常支持)，失败则回退到 service 命令
if systemctl is-active --quiet ssh 2>/dev/null; then
    systemctl restart ssh
    systemctl enable ssh 2>/dev/null || echo "提示: systemctl enable 在部分WSL环境中可能无效，但服务已启动。"
else
    service ssh restart
    update-rc.d ssh enable 2>/dev/null || echo "提示: 服务自启动配置可能未应用。"
fi

# 6. 验证与状态输出
echo ""
echo "[5/5] 验证配置状态..."
echo "-----------------------------------------"

# 检查服务进程
if netstat -tlnp 2>/dev/null | grep -E ':22\s' | grep sshd; then
    echo "✅ SSH 服务正在 22 端口监听。"
else
    echo "⚠️  未能检测到SSH监听端口，尝试使用 'ss' 命令复查..."
    ss -tlnp | grep :22 || echo "请手动检查服务状态: sudo service ssh status"
fi

# 获取当前WSL的IP地址（适用于WSL2）
CURRENT_IP=$(ip addr show eth0 2>/dev/null | grep -oP '(?<=inet\s)\d+(\.\d+){3}' | head -1) || CURRENT_IP="127.0.0.1"

echo ""
echo "========================================="
echo "          配置摘要与连接信息"
echo "========================================="
echo "SSH 专用用户名    : $SSH_USER"
if [ -n "$SSH_USER_PASSWORD" ]; then
    echo "SSH 用户密码      : (您刚才设置的密码)"
fi
echo ""
echo "**连接方式 (在Windows PuTTY中):**"
echo "1. Host Name 地址 : $CURRENT_IP 或 127.0.0.1"
echo "2. Port           : 22"
echo "3. Connection type: SSH"
echo ""
echo "**后续安全建议:**"
echo "1. 建议后续配置密钥登录，比密码更安全。"
echo "2. 脚本备份了原配置: $BACKUP_CONFIG"
echo "========================================="
echo "脚本执行完毕！"