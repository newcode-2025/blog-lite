# 阶段二：用户认证与资料 Postman 验证说明

> 文档日期：2026-10-04
> 阶段二主要包含注册、登录、退出、当前用户资料。

统一基础地址：

```text
http://localhost:8081
```

建议设置 Postman 环境变量：

| 变量名 | 值 |
|---|---|
| `baseUrl` | `http://localhost:8081` |

> 本文档按接口顺序介绍，每个接口的 Postman 设置都写在该接口小节内。

## 接口 1：用户登录并签发 JWT

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/auth/login`
- 所在控制器文件：`AuthController.java`
- 是否需要登录：不需要
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；`/api/v1/auth/**` 匿名放行，不会被拒绝。
- URL 示例：`{{baseUrl}}/api/v1/auth/login`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "username": "postman_user",
  "password": "Password123"
}
```
- Headers：不需要 Authorization；设置 `Content-Type: application/json`。

### 验证步骤

1. 按上面的方法创建请求。
2. 填写对应 URL 和参数。
3. 设置 Body 和请求头。
4. 点击 `Send`。

### 预期结果

- HTTP 状态码：`200`。
- 业务返回码：`20000`。
- 检查 `data` 是否符合对应 DTO 的结构。

---

## 接口 2：用户退出登录

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/auth/logout`
- 所在控制器文件：`AuthController.java`
- 是否需要登录：不需要（业务上用于已登录客户端）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；`/api/v1/auth/**` 匿名放行，不会被拒绝。
- URL 示例：`{{baseUrl}}/api/v1/auth/logout`
- Query 参数：无
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：不需要 Authorization，不需要 Content-Type。

### 验证步骤

1. 按上面的方法创建请求。
2. 填写对应 URL 和参数。
3. 设置 Body 和请求头。
4. 点击 `Send`。

### 预期结果

- HTTP 状态码：`200`。
- 业务返回码：`20000`。
- 检查 `data` 是否符合对应 DTO 的结构。

---

## 接口 3：用户注册

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/auth/register`
- 所在控制器文件：`AuthController.java`
- 是否需要登录：不需要
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；`/api/v1/auth/**` 匿名放行，不会被拒绝。
- URL 示例：`{{baseUrl}}/api/v1/auth/register`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "username": "postman_user",
  "password": "Password123",
  "nickname": "Postman用户",
  "email": "postman@example.com"
}
```
- Headers：不需要 Authorization；设置 `Content-Type: application/json`。

### 验证步骤

1. 按上面的方法创建请求。
2. 填写对应 URL 和参数。
3. 设置 Body 和请求头。
4. 点击 `Send`。

### 预期结果

- HTTP 状态码：`200`。
- 业务返回码：`20000`。
- 检查 `data` 是否符合对应 DTO 的结构。

---

## 接口 4：查询当前登录用户资料

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/users/me`
- 所在控制器文件：`UserController.java`
- 是否需要登录：需要
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录返回 401，登录用户可访问。
- URL 示例：`{{baseUrl}}/api/v1/users/me`
- Query 参数：无
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <登录用户 token>`

### 验证步骤

1. 按上面的方法创建请求。
2. 填写对应 URL 和参数。
3. 设置 Body 和请求头。
4. 点击 `Send`。

### 预期结果

- HTTP 状态码：`200`。
- 业务返回码：`20000`。
- 检查 `data` 是否符合对应 DTO 的结构。

---

## 接口 5：修改当前登录用户资料

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/users/me`
- 所在控制器文件：`UserController.java`
- 是否需要登录：需要
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录返回 401，登录用户可访问。
- URL 示例：`{{baseUrl}}/api/v1/users/me`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "nickname": "Postman用户",
  "email": "postman@example.com",
  "avatarUrl": "示例值"
}
```
- Headers：需要 `Authorization: Bearer <登录用户 token>`；设置 `Content-Type: application/json`。

### 验证步骤

1. 按上面的方法创建请求。
2. 填写对应 URL 和参数。
3. 设置 Body 和请求头。
4. 点击 `Send`。

### 预期结果

- HTTP 状态码：`200`。
- 业务返回码：`20000`。
- 检查 `data` 是否符合对应 DTO 的结构。

---
