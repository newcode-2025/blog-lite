# 阶段四：后台内容管理 Postman 验证说明

> 文档日期：2026-10-04
> 阶段四包含文章、分类、标签、评论审核、说说、友链和相册管理，以及部分公开内容读取接口。

统一基础地址：

```text
http://localhost:8081
```

建议设置 Postman 环境变量：

| 变量名 | 值 |
|---|---|
| `baseUrl` | `http://localhost:8081` |

> 本文档按接口顺序介绍，每个接口的 Postman 设置都写在该接口小节内。

## 接口 1：后台分页查询文章

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/articles`
- 所在控制器文件：`AdminArticleController.java`
- 是否需要登录：需要（ADMIN 或 USER）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，普通用户可进入但业务层只允许本人文章，越权返回 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/articles?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `status` | `请填写` | 否 | 可选参数 |
| `keywords` | `` | 否 | 默认空字符串 |
| `current` | `1` | 否 | 默认值 1 |
| `size` | `10` | 否 | 默认值 10 |
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <普通用户或管理员 token>`

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

## 接口 2：新建文章

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/articles`
- 所在控制器文件：`AdminArticleController.java`
- 是否需要登录：需要（ADMIN 或 USER）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，普通用户可进入但业务层只允许本人文章，越权返回 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/articles`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "title": "Postman测试文章",
  "summary": "Postman测试摘要",
  "content": "Postman测试正文",
  "coverUrl": "示例值",
  "categoryId": 1,
  "tagIds": [
    1
  ],
  "status": 1,
  "accessPassword": "Password123"
}
```
- Headers：需要 `Authorization: Bearer <普通用户或管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 3：查询文章编辑详情

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/articles/{articleId}`
- 所在控制器文件：`AdminArticleController.java`
- 是否需要登录：需要（ADMIN 或 USER）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，普通用户可进入但业务层只允许本人文章，越权返回 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/articles/1`
- Query 参数：无
- Path 参数：`articleId`
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <普通用户或管理员 token>`

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

## 接口 4：更新文章内容

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/articles/{articleId}`
- 所在控制器文件：`AdminArticleController.java`
- 是否需要登录：需要（ADMIN 或 USER）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，普通用户可进入但业务层只允许本人文章，越权返回 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/articles/1`
- Query 参数：无
- Path 参数：`articleId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "title": "Postman测试文章",
  "summary": "Postman测试摘要",
  "content": "Postman测试正文",
  "coverUrl": "示例值",
  "categoryId": 1,
  "tagIds": [
    1
  ],
  "status": 1,
  "accessPassword": "Password123"
}
```
- Headers：需要 `Authorization: Bearer <普通用户或管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 5：查询文章回收站

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/articles/recycle-bin`
- 所在控制器文件：`AdminArticleLifecycleController.java`
- 是否需要登录：需要（ADMIN 或 USER）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，普通用户可进入但业务层只允许本人文章，越权返回 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/articles/recycle-bin?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `keywords` | `` | 否 | 默认空字符串 |
| `current` | `1` | 否 | 默认值 1 |
| `size` | `10` | 否 | 默认值 10 |
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <普通用户或管理员 token>`

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

## 接口 6：软删除文章

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/articles/{articleId}`
- 所在控制器文件：`AdminArticleLifecycleController.java`
- 是否需要登录：需要（ADMIN 或 USER）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，普通用户可进入但业务层只允许本人文章，越权返回 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/articles/1`
- Query 参数：无
- Path 参数：`articleId`
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <普通用户或管理员 token>`

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

## 接口 7：修改文章置顶和推荐状态

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/articles/{articleId}/flags`
- 所在控制器文件：`AdminArticleLifecycleController.java`
- 是否需要登录：需要（ADMIN 或 USER）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，普通用户可进入但业务层只允许本人文章，越权返回 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/articles/1/flags`
- Query 参数：无
- Path 参数：`articleId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "isTop": 0,
  "isFeatured": 0
}
```
- Headers：需要 `Authorization: Bearer <普通用户或管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 8：恢复已删除文章

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/articles/{articleId}/restore`
- 所在控制器文件：`AdminArticleLifecycleController.java`
- 是否需要登录：需要（ADMIN 或 USER）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，普通用户可进入但业务层只允许本人文章，越权返回 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/articles/1/restore`
- Query 参数：无
- Path 参数：`articleId`
- Body：选择 `none`。
- 不需要 JSON 请求体。
- Headers：需要 `Authorization: Bearer <普通用户或管理员 token>`

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

## 接口 9：修改文章状态和发布时间

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/articles/{articleId}/status`
- 所在控制器文件：`AdminArticleLifecycleController.java`
- 是否需要登录：需要（ADMIN 或 USER）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，普通用户可进入但业务层只允许本人文章，越权返回 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/articles/1/status`
- Query 参数：无
- Path 参数：`articleId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "status": 1,
  "accessPassword": "Password123"
}
```
- Headers：需要 `Authorization: Bearer <普通用户或管理员 token>`；设置 `Content-Type: application/json`。

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

## 接口 10：查询后台分类列表

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/categories`
- 所在控制器文件：`AdminCategoryController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/categories`
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

## 接口 11：新增分类

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/categories`
- 所在控制器文件：`AdminCategoryController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/categories`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "name": "Postman测试名称",
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

## 接口 12：修改分类

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/categories/{categoryId}`
- 所在控制器文件：`AdminCategoryController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/categories/1`
- Query 参数：无
- Path 参数：`categoryId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "name": "Postman测试名称",
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

## 接口 13：删除分类

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/categories/{categoryId}`
- 所在控制器文件：`AdminCategoryController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/categories/1`
- Query 参数：无
- Path 参数：`categoryId`
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

## 接口 14：查询后台标签列表

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/tags`
- 所在控制器文件：`AdminTagController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/tags`
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

## 接口 15：新增标签

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/tags`
- 所在控制器文件：`AdminTagController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/tags`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "name": "Postman测试名称"
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

## 接口 16：修改标签

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/tags/{tagId}`
- 所在控制器文件：`AdminTagController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/tags/1`
- Query 参数：无
- Path 参数：`tagId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "name": "Postman测试名称"
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

## 接口 17：删除标签

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/tags/{tagId}`
- 所在控制器文件：`AdminTagController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/tags/1`
- Query 参数：无
- Path 参数：`tagId`
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

## 接口 18：后台分页查询文章评论

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/comments`
- 所在控制器文件：`AdminCommentController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/comments?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `articleId` | `1` | 否 | 可选参数 |
| `status` | `请填写` | 否 | 可选参数 |
| `keywords` | `` | 否 | 默认空字符串 |
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

## 接口 19：批量审核评论

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/comments/status`
- 所在控制器文件：`AdminCommentController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/comments/status`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "commentIds": [],
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

## 接口 20：后台分页查询说说

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/talks`
- 所在控制器文件：`TalkController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/talks?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `status` | `请填写` | 否 | 可选参数 |
| `keywords` | `` | 否 | 默认空字符串 |
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

## 接口 21：新增说说

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/talks`
- 所在控制器文件：`TalkController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/talks`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "content": "Postman测试正文",
  "images": [],
  "isTop": 0,
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

## 接口 22：修改说说

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/talks/{talkId}`
- 所在控制器文件：`TalkController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/talks/1`
- Query 参数：无
- Path 参数：`talkId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "content": "Postman测试正文",
  "images": [],
  "isTop": 0,
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

## 接口 23：删除说说

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/talks/{talkId}`
- 所在控制器文件：`TalkController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/talks/1`
- Query 参数：无
- Path 参数：`talkId`
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

## 接口 24：分页查询公开说说

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/talks`
- 所在控制器文件：`TalkController.java`
- 是否需要登录：不需要
- 过滤器处理：会经过两个过滤器；公开内容 GET 路径命中公开规则后放行。
- URL 示例：`{{baseUrl}}/api/v1/talks?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `current` | `1` | 否 | 默认值 1 |
| `size` | `10` | 否 | 默认值 10 |
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
- 检查 `data.records`、`data.count`、`data.current`、`data.size`。

---

## 接口 25：后台分页查询友链

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/links`
- 所在控制器文件：`FriendLinkController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/links?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `status` | `请填写` | 否 | 可选参数 |
| `keywords` | `` | 否 | 默认空字符串 |
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

## 接口 26：新增友链

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/links`
- 所在控制器文件：`FriendLinkController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/links`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "name": "Postman测试名称",
  "avatarUrl": "示例值",
  "address": "https://example.com",
  "description": "示例值",
  "sortOrder": 0,
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

## 接口 27：修改友链

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/links/{linkId}`
- 所在控制器文件：`FriendLinkController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/links/1`
- Query 参数：无
- Path 参数：`linkId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "name": "Postman测试名称",
  "avatarUrl": "示例值",
  "address": "https://example.com",
  "description": "示例值",
  "sortOrder": 0,
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

## 接口 28：删除友链

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/links/{linkId}`
- 所在控制器文件：`FriendLinkController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/links/1`
- Query 参数：无
- Path 参数：`linkId`
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

## 接口 29：查询公开友链

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/links`
- 所在控制器文件：`FriendLinkController.java`
- 是否需要登录：不需要
- 过滤器处理：会经过两个过滤器；公开内容 GET 路径命中公开规则后放行。
- URL 示例：`{{baseUrl}}/api/v1/links`
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

## 接口 30：后台分页查询相册

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/albums`
- 所在控制器文件：`PhotoAlbumController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/albums?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `status` | `请填写` | 否 | 可选参数 |
| `keywords` | `` | 否 | 默认空字符串 |
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

## 接口 31：新增相册

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/albums`
- 所在控制器文件：`PhotoAlbumController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/albums`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "name": "Postman测试名称",
  "description": "示例值",
  "coverUrl": "示例值",
  "sortOrder": 0,
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

## 接口 32：修改相册

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/albums/{albumId}`
- 所在控制器文件：`PhotoAlbumController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/albums/1`
- Query 参数：无
- Path 参数：`albumId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "name": "Postman测试名称",
  "description": "示例值",
  "coverUrl": "示例值",
  "sortOrder": 0,
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

## 接口 33：删除相册

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/albums/{albumId}`
- 所在控制器文件：`PhotoAlbumController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/albums/1`
- Query 参数：无
- Path 参数：`albumId`
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

## 接口 34：查询公开相册

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/albums`
- 所在控制器文件：`PhotoAlbumController.java`
- 是否需要登录：不需要
- 过滤器处理：会经过两个过滤器；公开内容 GET 路径命中公开规则后放行。
- URL 示例：`{{baseUrl}}/api/v1/albums`
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

## 接口 35：后台分页查询相册图片

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/albums/{albumId}/photos`
- 所在控制器文件：`PhotoController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/albums/1/photos?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `current` | `1` | 否 | 默认值 1 |
| `size` | `10` | 否 | 默认值 10 |
- Path 参数：`albumId`
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

## 接口 36：新增相册图片

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/albums/{albumId}/photos`
- 所在控制器文件：`PhotoController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/albums/1/photos`
- Query 参数：无
- Path 参数：`albumId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "name": "Postman测试名称",
  "description": "示例值",
  "photoUrl": "/uploads/album/2026/10/postman.png",
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

## 接口 37：修改相册图片

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/photos/{photoId}`
- 所在控制器文件：`PhotoController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/photos/1`
- Query 参数：无
- Path 参数：`photoId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "name": "Postman测试名称",
  "description": "示例值",
  "photoUrl": "/uploads/album/2026/10/postman.png",
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

## 接口 38：删除相册图片

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/photos/{photoId}`
- 所在控制器文件：`PhotoController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/photos/1`
- Query 参数：无
- Path 参数：`photoId`
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

## 接口 39：分页查询公开相册图片

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/albums/{albumId}/photos`
- 所在控制器文件：`PhotoController.java`
- 是否需要登录：不需要
- 过滤器处理：会经过两个过滤器；公开内容 GET 路径命中公开规则后放行。
- URL 示例：`{{baseUrl}}/api/v1/albums/1/photos?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `current` | `1` | 否 | 默认值 1 |
| `size` | `10` | 否 | 默认值 10 |
- Path 参数：`albumId`
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
- 检查 `data.records`、`data.count`、`data.current`、`data.size`。

---
