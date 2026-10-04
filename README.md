# blog-lite

blog-lite 是一个用于学习和实践 Spring Boot 后端开发的单体博客项目。项目从公开文章阅读开始，逐步实现了用户认证、评论互动、后台内容管理、RBAC 权限、媒体上传、网站配置、访问统计、日志、定时任务和邮件通知。

项目不包含前端页面，主要通过 Postman 或 HTTP 客户端调用接口。

## 技术栈

- Java 17 编译目标，推荐使用 JDK 21 运行
- Maven
- Spring Boot 4.1.1
- Spring Security + JWT
- MyBatis-Plus 3.5.16
- MySQL 8.0
- Quartz
- Spring Mail + Thymeleaf
- 本地文件上传

## 已实现功能

- 文章公开阅读、搜索、分类、标签和归档
- 用户注册、登录、退出和资料维护
- 文章评论、回复、审核和删除
- 后台文章、分类、标签、评论、说说、友链和相册管理
- 用户、角色、菜单、接口资源和权限分配
- 图片上传、批量上传和用户头像上传
- 网站配置和公开站点概览
- PV/UV 访问统计
- 操作日志和异常日志
- 邮件发送和评论邮件通知
- 动态定时任务、日志清理和通知重试

## 项目结构

```text
blog-lite
├── database/
│   ├── blog_lite_schema.sql
│   └── blog_lite_seed.sql
├── report/                    # 各阶段接口和 Postman 文档
├── src/main/java/             # Java 源码
├── src/main/resources/        # 配置、Mapper XML 和邮件模板
├── pom.xml
└── README.md
```

上传文件会在应用启动时由代码自动创建到项目工作目录下的 `uploads/`。

## 环境要求

- JDK 17 或 JDK 21
- Maven 3.8+
- MySQL 8.0+

## 启动项目

### 1. 创建数据库和表

在项目根目录执行：

```bash
mysql -u your_mysql_user -p < database/blog_lite_schema.sql
```

脚本会创建数据库：

```text
blog_lite
```

并创建项目所需的全部表结构。

### 2. 初始化基础数据

执行：

```bash
mysql -u your_mysql_user -p blog_lite < database/blog_lite_seed.sql
```

该脚本会初始化：

- 管理员和普通用户角色
- 默认网站配置
- 基础接口资源
- 管理员和普通用户的接口权限
- 用户头像上传权限

脚本不包含默认管理员账号、文章和评论数据。

### 3. 修改数据库连接

编辑：

```text
src/main/resources/application.yaml
```

按本机 MySQL 环境修改：

```yaml
spring:
  datasource:
    url: "jdbc:mysql://127.0.0.1:3306/blog_lite?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
    username: "${BLOG_DB_USERNAME:}"
    password: "${BLOG_DB_PASSWORD:}"
```

生产或公开环境不要直接使用默认密码和默认 JWT 密钥。

### 4. 编译项目

项目自带 Maven Wrapper 在当前开发环境中存在问题，建议直接使用系统 Maven：

```bash
mvn clean package -DskipTests
```

### 5. 启动项目

方式一，使用打包后的 Jar：

```bash
java -jar target/blog-lite-0.0.1-SNAPSHOT.jar
```

方式二，使用 Maven：

```bash
mvn spring-boot:run
```

默认服务地址：

```text
http://localhost:8081
```

### 6. 验证启动

访问：

```http
GET http://localhost:8081/api/v1/health
```

正常情况下返回：

```json
{
  "code": 20000,
  "message": "操作成功",
  "data": {
    "status": "UP",
    "application": "blog-lite"
  }
}
```

## 创建管理员账号

先在系统注册一个普通用户：

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "admin",
  "password": "Password123",
  "nickname": "管理员",
  "email": "admin@example.com"
}
```

然后在数据库中给该用户增加管理员角色，将下面的 `admin` 替换为实际用户名：

```sql
USE blog_lite;

INSERT IGNORE INTO user_role (user_id, role_id, created_at)
SELECT u.id, r.id, NOW()
FROM user_account u
JOIN rbac_role r ON r.role_code = 'ADMIN'
WHERE u.username = 'admin';
```

之后通过：

```http
POST /api/v1/auth/login
```

获取管理员 JWT。

## 常用配置

项目支持通过环境变量覆盖部分敏感配置：

| 环境变量 | 作用 |
|---|---|
| `BLOG_JWT_SECRET` | JWT 签名密钥 |
| `BLOG_STATS_VISITOR_HASH_SALT` | 访问统计访客哈希盐值 |
| `BLOG_MAIL_ENABLED` | 是否启用邮件 |
| `BLOG_MAIL_HOST` | SMTP 主机 |
| `BLOG_MAIL_PORT` | SMTP 端口 |
| `BLOG_MAIL_USERNAME` | SMTP 用户名 |
| `BLOG_MAIL_PASSWORD` | SMTP 密码或授权码 |
| `BLOG_MAIL_FROM` | 发件人邮箱 |
| `BLOG_MAIL_FROM_NAME` | 发件人显示名称 |
| `BLOG_MAIL_SMTP_AUTH` | SMTP 是否启用认证 |
| `BLOG_MAIL_STARTTLS` | SMTP 是否启用 STARTTLS |

默认邮件功能关闭。启用邮件前需要配置有效的 SMTP 信息。

## 接口文档

各阶段的接口详情和 Postman 验证说明位于：

```text
report/阶段一/
report/阶段二/
report/阶段三/
report/阶段四/
report/阶段五/
report/阶段六/
report/阶段七/
```

每个阶段目录包含：

- `*后端接口详情.md`
- `postman验证.md`

## 说明

- 项目没有前端页面。
- 服务默认端口是 `8081`。
- 上传目录会在应用启动时自动创建。
- 数据库脚本位于 `database/`，不依赖旧项目中的 `sql/` 目录。
