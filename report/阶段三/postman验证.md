# 阶段三：评论互动 Postman 验证说明

> 文档日期：2026-10-04
> 阶段三包含评论发表、公开评论读取、回复、删除和审核。

统一基础地址：

```text
http://localhost:8081
```

建议设置 Postman 环境变量：

| 变量名 | 值 |
|---|---|
| `baseUrl` | `http://localhost:8081` |

> 本文档按接口顺序介绍，每个接口的 Postman 设置都写在该接口小节内。

## 接口 1：分页查询公开顶级评论

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/articles/{articleId}/comments`
- 所在控制器文件：`ArticleCommentController.java`
- 是否需要登录：不需要
- 过滤器处理：会经过两个过滤器；GET 公开评论路径命中公开规则后放行。
- URL 示例：`{{baseUrl}}/api/v1/articles/1/comments?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `current` | `1` | 否 | 默认值 1 |
| `size` | `10` | 否 | 默认值 10 |
- Path 参数：`articleId`
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

## 接口 2：在文章下发表顶级评论

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/articles/{articleId}/comments`
- 所在控制器文件：`ArticleCommentController.java`
- 是否需要登录：需要
- 过滤器处理：会经过两个过滤器；未登录由 ResourceAuthorizationFilter 返回 401。
- URL 示例：`{{baseUrl}}/api/v1/articles/1/comments`
- Query 参数：无
- Path 参数：`articleId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "content": "Postman测试正文"
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

## 接口 3：查询文章评论审核列表

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/comments/review`
- 所在控制器文件：`CommentController.java`
- 是否需要登录：需要
- 过滤器处理：会经过两个过滤器；GET 公开规则会放行到业务层，但业务层 UserContext 仍要求登录，实际需要 JWT。
- URL 示例：`{{baseUrl}}/api/v1/comments/review?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `articleId` | `1` | 是 | 必填参数 |
| `status` | `请填写` | 否 | 可选参数 |
| `current` | `1` | 否 | 默认值 1 |
| `size` | `10` | 否 | 默认值 10 |
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
- 检查 `data.records`、`data.count`、`data.current`、`data.size`。

---

## 接口 4：删除评论或级联删除回复

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/comments/{commentId}`
- 所在控制器文件：`CommentController.java`
- 是否需要登录：需要
- 过滤器处理：会经过两个过滤器；未登录由 ResourceAuthorizationFilter 返回 401。
- URL 示例：`{{baseUrl}}/api/v1/comments/1`
- Query 参数：无
- Path 参数：`commentId`
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

## 接口 5：分页查询公开回复

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/comments/{commentId}/replies`
- 所在控制器文件：`CommentController.java`
- 是否需要登录：不需要
- 过滤器处理：会经过两个过滤器；公开回复 GET 路径命中公开规则后放行。
- URL 示例：`{{baseUrl}}/api/v1/comments/1/replies?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `current` | `1` | 否 | 默认值 1 |
| `size` | `10` | 否 | 默认值 10 |
- Path 参数：`commentId`
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

## 接口 6：审核单条评论

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/comments/{commentId}/status`
- 所在控制器文件：`CommentController.java`
- 是否需要登录：需要
- 过滤器处理：会经过两个过滤器；未登录由 ResourceAuthorizationFilter 返回 401。
- URL 示例：`{{baseUrl}}/api/v1/comments/1/status`
- Query 参数：无
- Path 参数：`commentId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "status": 1
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

## 接口 7：回复指定评论

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/comments/{targetCommentId}/replies`
- 所在控制器文件：`CommentController.java`
- 是否需要登录：需要
- 过滤器处理：会经过两个过滤器；未登录由 ResourceAuthorizationFilter 返回 401。
- URL 示例：`{{baseUrl}}/api/v1/comments/1/replies`
- Query 参数：无
- Path 参数：`targetCommentId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "content": "Postman测试正文"
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
