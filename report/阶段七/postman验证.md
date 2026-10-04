# 阶段七：站点运维与系统保障 Postman 验证说明

> 文档日期：2026-10-04
> 阶段七包含网站配置、访问统计、操作日志、异常日志、邮件通知和定时任务。

统一基础地址：

```text
http://localhost:8081
```

建议设置 Postman 环境变量：

| 变量名 | 值 |
|---|---|
| `baseUrl` | `http://localhost:8081` |

> 本文档按接口顺序介绍，每个接口的 Postman 设置都写在该接口小节内。

## 接口 1：查询公开网站配置

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/site/config`
- 所在控制器文件：`SiteController.java`
- 是否需要登录：不需要
- 过滤器处理：会经过两个过滤器；公开站点路径命中匿名规则后放行。
- URL 示例：`{{baseUrl}}/api/v1/site/config`
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

## 接口 2：查询公开站点概览

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/site/overview`
- 所在控制器文件：`SiteController.java`
- 是否需要登录：不需要
- 过滤器处理：会经过两个过滤器；公开站点路径命中匿名规则后放行。
- URL 示例：`{{baseUrl}}/api/v1/site/overview`
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

## 接口 3：匿名上报一次网站访问

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/stats/report`
- 所在控制器文件：`SiteVisitController.java`
- 是否需要登录：不需要
- 过滤器处理：会经过两个过滤器；访问上报接口配置为匿名放行。
- URL 示例：`{{baseUrl}}/api/v1/stats/report`
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

## 接口 4：管理员查询网站配置

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/site/config`
- 所在控制器文件：`AdminSiteConfigController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/site/config`
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

## 接口 5：修改网站配置

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/site/config`
- 所在控制器文件：`AdminSiteConfigController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/site/config`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "version": 1,
  "siteName": "blog-lite",
  "siteDescription": "博客描述",
  "authorName": "Postman用户",
  "authorAvatar": "",
  "authorIntro": "",
  "logo": "",
  "notice": "",
  "websiteCreateTime": "2026-10-04",
  "icpNumber": "",
  "github": "",
  "gitee": "",
  "qq": "",
  "weChat": "",
  "weibo": "",
  "csdn": "",
  "zhihu": "",
  "juejin": "",
  "twitter": "",
  "stackOverflow": "",
  "email": "postman@example.com",
  "commentReviewEnabled": 1,
  "emailNotificationEnabled": 0
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

## 接口 6：查询后台统计总览

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/stats/overview`
- 所在控制器文件：`AdminStatsController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/stats/overview`
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

## 接口 7：查询最近访问趋势

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/stats/visits`
- 所在控制器文件：`AdminStatsController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/stats/visits?days=30`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `days` | `30` | 否 | 默认值 30 |
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

## 接口 8：分页查询操作日志

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/operation-logs`
- 所在控制器文件：`AdminOperationLogController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/operation-logs?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `module` | `` | 否 | 默认空字符串 |
| `action` | `` | 否 | 默认空字符串 |
| `operatorName` | `` | 否 | 默认空字符串 |
| `success` | `请填写` | 否 | 可选参数 |
| `startTime` | `请填写` | 否 | 可选参数 |
| `endTime` | `请填写` | 否 | 可选参数 |
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

## 接口 9：批量删除操作日志

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/operation-logs`
- 所在控制器文件：`AdminOperationLogController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/operation-logs`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "ids": [
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

## 接口 10：分页查询异常日志

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/exception-logs`
- 所在控制器文件：`AdminExceptionLogController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/exception-logs?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `exceptionType` | `` | 否 | 默认空字符串 |
| `requestUri` | `` | 否 | 默认空字符串 |
| `startTime` | `请填写` | 否 | 可选参数 |
| `endTime` | `请填写` | 否 | 可选参数 |
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

## 接口 11：批量删除异常日志

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/exception-logs`
- 所在控制器文件：`AdminExceptionLogController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/exception-logs`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "ids": [
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

## 接口 12：发送邮件配置测试

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/mail/test`
- 所在控制器文件：`AdminMailController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/mail/test`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "to": "postman@example.com",
  "subject": "blog-lite Postman测试"
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

## 接口 13：分页查询定时任务

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/jobs`
- 所在控制器文件：`AdminScheduledJobController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/jobs?current=1&size=10`
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

## 接口 14：新增定时任务

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/jobs`
- 所在控制器文件：`AdminScheduledJobController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/jobs`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "taskKey": "systemHeartbeat",
  "jobName": "Postman测试任务",
  "jobGroup": "TEST",
  "cronExpression": "0 0 0 * * ?",
  "status": 1,
  "concurrent": 0,
  "misfirePolicy": 0,
  "description": "示例值"
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

## 接口 15：查询可用的白名单任务处理器

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/jobs/handlers`
- 所在控制器文件：`AdminScheduledJobController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/jobs/handlers`
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

## 接口 16：查询定时任务详情

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/jobs/{jobId}`
- 所在控制器文件：`AdminScheduledJobController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/jobs/1`
- Query 参数：无
- Path 参数：`jobId`
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

## 接口 17：修改定时任务

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/jobs/{jobId}`
- 所在控制器文件：`AdminScheduledJobController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/jobs/1`
- Query 参数：无
- Path 参数：`jobId`
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "taskKey": "systemHeartbeat",
  "jobName": "Postman测试任务",
  "jobGroup": "TEST",
  "cronExpression": "0 0 0 * * ?",
  "status": 1,
  "concurrent": 0,
  "misfirePolicy": 0,
  "description": "示例值"
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

## 接口 18：删除定时任务

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/jobs/{jobId}`
- 所在控制器文件：`AdminScheduledJobController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/jobs/1`
- Query 参数：无
- Path 参数：`jobId`
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

## 接口 19：立即执行一次定时任务

### Postman 设置

- 请求方法：`POST`
- 接口路径：`/api/v1/admin/jobs/{jobId}/run`
- 所在控制器文件：`AdminScheduledJobController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/jobs/1/run`
- Query 参数：无
- Path 参数：`jobId`
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

## 接口 20：启用或暂停定时任务

### Postman 设置

- 请求方法：`PUT`
- 接口路径：`/api/v1/admin/jobs/{jobId}/status`
- 所在控制器文件：`AdminScheduledJobController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/jobs/1/status`
- Query 参数：无
- Path 参数：`jobId`
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

## 接口 21：分页查询任务执行日志

### Postman 设置

- 请求方法：`GET`
- 接口路径：`/api/v1/admin/job-logs`
- 所在控制器文件：`AdminScheduledJobLogController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/job-logs?current=1&size=10`
- Query 参数：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `jobId` | `1` | 否 | 可选参数 |
| `taskKey` | `` | 否 | 默认空字符串 |
| `status` | `请填写` | 否 | 可选参数 |
| `startTime` | `请填写` | 否 | 可选参数 |
| `endTime` | `请填写` | 否 | 可选参数 |
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

## 接口 22：批量删除任务执行日志

### Postman 设置

- 请求方法：`DELETE`
- 接口路径：`/api/v1/admin/job-logs`
- 所在控制器文件：`AdminScheduledJobLogController.java`
- 是否需要登录：需要（ADMIN）
- 过滤器处理：会经过 JwtAuthenticationFilter 和 ResourceAuthorizationFilter；未登录 401，非管理员 403。
- URL 示例：`{{baseUrl}}/api/v1/admin/job-logs`
- Query 参数：无
- Body：选择 `raw -> JSON`，发送以下 JSON：

```json
{
  "ids": [
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
