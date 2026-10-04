# 阶段五：RBAC 权限 Postman 验证说明

> 文档日期：2026-10-04
> 阶段五包含用户、角色、菜单、接口资源和权限分配管理，以及当前用户角色查询。

统一基础地址：

```text
http://localhost:8081
```

建议设置 Postman 环境变量：

| 变量名 | 值 |
|---|---|
| `baseUrl` | `http://localhost:8081` |

> 本文档按接口顺序介绍，每个接口的 Postman 设置都写在该接口小节内。

## 接口 1：分页查询用户

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/users`
- 所在控制器文件：`AdminUserController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/users?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `keywords` | `` | 否 | 默认空字符串 |
| `status` | `请填写` | 否 | 可选参数 |
| `current` | `1` | 否 | 默认值 1 |
| `size` | `10` | 否 | 默认值 10 |
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

### 验证步骤

1. 按上面的方法创建请求。
2. 填写对应 URL 和参数。
3. 设置 Body 和请求头。
4. 点击 `Send`。

### 预期结果

- HTTP 状态码：`200`。
- 业务返回码：`20000`。
- 检查 `data.records`、`data.count`、`data.current`、`data.size`。

---

## 接口 2：查询用户详情

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/users/{userId}`
- 所在控制器文件：`AdminUserController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/users/1`
- Query 参数：无
- Path 参数：`userId`
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

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

## 接口 3：分配用户角色

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/users/{userId}/roles`
- 所在控制器文件：`AdminUserController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/users/1/roles`
- Query 参数：无
- Path 参数：`userId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "roleIds": [
    2
  ]
}
```
- Headers：需要 `Authorization: Bearer <管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 4：修改用户启用状态

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/users/{userId}/status`
- 所在控制器文件：`AdminUserController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/users/1/status`
- Query 参数：无
- Path 参数：`userId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "status": 1
}
```
- Headers：需要 `Authorization: Bearer <管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 5：分页查询角色

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/roles`
- 所在控制器文件：`AdminRoleController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/roles?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `keywords` | `` | 否 | 默认空字符串 |
| `status` | `请填写` | 否 | 可选参数 |
| `current` | `1` | 否 | 默认值 1 |
| `size` | `10` | 否 | 默认值 10 |
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

### 验证步骤

1. 按上面的方法创建请求。
2. 填写对应 URL 和参数。
3. 设置 Body 和请求头。
4. 点击 `Send`。

### 预期结果

- HTTP 状态码：`200`。
- 业务返回码：`20000`。
- 检查 `data.records`、`data.count`、`data.current`、`data.size`。

---

## 接口 6：新增角色

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/roles`
- 所在控制器文件：`AdminRoleController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/roles`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "roleCode": "POSTMAN_ROLE",
  "roleName": "Postman测试角色",
  "description": "示例值",
  "status": 1
}
```
- Headers：需要 `Authorization: Bearer <管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 7：查询角色选项

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/roles/options`
- 所在控制器文件：`AdminRoleController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/roles/options`
- Query 参数：无
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

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

## 接口 8：查询角色详情

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/roles/{roleId}`
- 所在控制器文件：`AdminRoleController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/roles/1`
- Query 参数：无
- Path 参数：`roleId`
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

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

## 接口 9：修改角色

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/roles/{roleId}`
- 所在控制器文件：`AdminRoleController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/roles/1`
- Query 参数：无
- Path 参数：`roleId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "roleName": "Postman测试角色",
  "description": "示例值",
  "status": 1
}
```
- Headers：需要 `Authorization: Bearer <管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 10：删除角色

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/roles/{roleId}`
- 所在控制器文件：`AdminRoleController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/roles/1`
- Query 参数：无
- Path 参数：`roleId`
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

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

## 接口 11：给角色分配菜单

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/roles/{roleId}/menus`
- 所在控制器文件：`AdminRolePermissionController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/roles/1/menus`
- Query 参数：无
- Path 参数：`roleId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "menuIds": [
    1
  ]
}
```
- Headers：需要 `Authorization: Bearer <管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 12：查询角色权限详情

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/roles/{roleId}/permissions`
- 所在控制器文件：`AdminRolePermissionController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/roles/1/permissions`
- Query 参数：无
- Path 参数：`roleId`
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

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

## 接口 13：给角色分配接口资源

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/roles/{roleId}/resources`
- 所在控制器文件：`AdminRolePermissionController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/roles/1/resources`
- Query 参数：无
- Path 参数：`roleId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "resourceIds": [
    1
  ]
}
```
- Headers：需要 `Authorization: Bearer <管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 14：查询菜单树

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/menus`
- 所在控制器文件：`AdminMenuController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/menus`
- Query 参数：无
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

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

## 接口 15：新增菜单

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/menus`
- 所在控制器文件：`AdminMenuController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/menus`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "name": "Postman测试名称",
  "path": "/postman-test",
  "component": "postman/component",
  "icon": "postman",
  "sortOrder": 0,
  "parentId": null,
  "isHidden": 0,
  "status": 1
}
```
- Headers：需要 `Authorization: Bearer <管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 16：查询菜单详情

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/menus/{menuId}`
- 所在控制器文件：`AdminMenuController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/menus/1`
- Query 参数：无
- Path 参数：`menuId`
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

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

## 接口 17：修改菜单

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/menus/{menuId}`
- 所在控制器文件：`AdminMenuController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/menus/1`
- Query 参数：无
- Path 参数：`menuId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "name": "Postman测试名称",
  "path": "/postman-test",
  "component": "postman/component",
  "icon": "postman",
  "sortOrder": 0,
  "parentId": null,
  "isHidden": 0,
  "status": 1
}
```
- Headers：需要 `Authorization: Bearer <管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 18：删除菜单

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/menus/{menuId}`
- 所在控制器文件：`AdminMenuController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/menus/1`
- Query 参数：无
- Path 参数：`menuId`
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

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

## 接口 19：查询接口资源树

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/resources`
- 所在控制器文件：`AdminResourceController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/resources`
- Query 参数：无
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

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

## 接口 20：新增接口资源

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/resources`
- 所在控制器文件：`AdminResourceController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/resources`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "resourceName": "Postman测试资源",
  "urlPattern": "/api/v1/postman/**",
  "httpMethod": "GET",
  "parentId": null,
  "isAnonymous": 0,
  "status": 1,
  "sortOrder": 0
}
```
- Headers：需要 `Authorization: Bearer <管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 21：查询接口资源详情

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/resources/{resourceId}`
- 所在控制器文件：`AdminResourceController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/resources/1`
- Query 参数：无
- Path 参数：`resourceId`
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

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

## 接口 22：修改接口资源

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/resources/{resourceId}`
- 所在控制器文件：`AdminResourceController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/resources/1`
- Query 参数：无
- Path 参数：`resourceId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "resourceName": "Postman测试资源",
  "urlPattern": "/api/v1/postman/**",
  "httpMethod": "GET",
  "parentId": null,
  "isAnonymous": 0,
  "status": 1,
  "sortOrder": 0
}
```
- Headers：需要 `Authorization: Bearer <管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 23：删除接口资源

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/resources/{resourceId}`
- 所在控制器文件：`AdminResourceController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/resources/1`
- Query 参数：无
- Path 参数：`resourceId`
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <管理员 token>`

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

## 接口 24：查询当前登录用户的角色

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/users/me/roles`
- 所在控制器文件：`CurrentUserRoleController.java`
- 是否需要登录：需要
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录返回 401。
- URL 示例：`{{baseUrl}}/api/v1/users/me/roles`
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
