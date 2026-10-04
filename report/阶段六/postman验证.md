# 阶段六：媒体上传 Postman 验证说明

> 文档日期：2026-10-04
> 阶段六包含后台图片上传、批量上传和用户头像上传。

统一基础地址：

```text
http://localhost:8081
```

建议设置 Postman 环境变量：

| 变量名 | 值 |
|---|---|
| `baseUrl` | `http://localhost:8081` |

> 本文档按接口顺序介绍，每个接口的 Postman 设置都写在该接口小节内。

## 接口 1：上传单张图片

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/media/images`
- 所在控制器文件：`MediaController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/media/images`
- Query 参数：无
- Body：选择 `form-data`。
- Form-data 字段：`file`（File）、`scene`（Text）
- Headers：需要 `Authorization: Bearer <管理员 token>`；Postman 自动设置 multipart Content-Type。

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

## 接口 2：批量上传图片

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/media/images/batch`
- 所在控制器文件：`MediaController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/media/images/batch`
- Query 参数：无
- Body：选择 `form-data`。
- Form-data 字段：`files`（File）、`scene`（Text）
- Headers：需要 `Authorization: Bearer <管理员 token>`；Postman 自动设置 multipart Content-Type。

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

## 接口 3：上传并更新当前用户头像

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/users/me/avatar`
- 所在控制器文件：`UserController.java`
- 是否需要登录：需要
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录返回 401，登录用户可访问。
- URL 示例：`{{baseUrl}}/api/v1/users/me/avatar`
- Query 参数：无
- Body：选择 `form-data`。
- Form-data 字段：`file`（File）
- Headers：需要 `Authorization: Bearer <登录用户 token>`；Postman 自动设置 multipart Content-Type。

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
