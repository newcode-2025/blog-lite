# 阶段一接口 Postman 验证说明

> 文档日期：2026-10-04
> 项目：blog-lite
> 阶段：阶段一 文章公开阅读
> 说明：阶段一共 8 个 GET 接口，全部不需要登录，也没有 JSON 请求体。

## 一、验证前准备

### 1. 启动后端

确保 MySQL 已启动，然后启动 blog-lite，端口为：

```text
8081
```

浏览器或 Postman 可以访问：

```text
http://localhost:8081/api/v1/health
```

如果返回：

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

说明后端已经启动。

### 2. 在 Postman 中设置环境变量

建议创建一个 Postman 环境变量：

| 变量名 | 初始值 |
|---|---|
| `baseUrl` | `http://localhost:8081` |

后面所有 URL 都可以写成：

```text
{{baseUrl}}/api/v1/接口路径
```

### 3. 阶段一通用设置

阶段一所有接口统一使用以下 Postman 设置：

| 设置项 | 设置方式 |
|---|---|
| Request Method | `GET` |
| Authorization | `No Auth` |
| Headers | 不需要 `Authorization`，不需要 `Content-Type` |
| Body | 选择 `none` |
| JSON 请求体 | 无 |
| 登录要求 | 不需要登录 |

注意：

- 阶段一的请求参数放在 URL 或 Postman 的 `Params` 中。
- 不要选择 `raw -> JSON`。
- 不需要设置 `Content-Type: application/json`。
- 不需要设置 `Authorization: Bearer ...`。
- 如果额外携带了 token，也不会影响这些公开接口，但 token 不是验证阶段一的必要条件。

---

## 二、接口 1：分页查询公开文章

### 1. Postman 基本信息

| 项目 | 内容 |
|---|---|
| 请求方法 | `GET` |
| 接口路径 | `/api/v1/articles` |
| 是否需要登录 | 否 |
| 是否需要请求头 | 否 |
| 是否需要 JSON 请求体 | 否 |
| 是否分页 | 是 |

### 2. URL

```text
{{baseUrl}}/api/v1/articles
```

### 3. Params 设置

在 Postman 的 `Params` 中填写：

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `current` | `1` | 否 | 当前页码，默认 1 |
| `size` | `10` | 否 | 每页数量，默认 10，最大 50 |

也可以直接使用：

```text
{{baseUrl}}/api/v1/articles?current=1&size=10
```

### 4. Body 设置

选择：

```text
Body -> none
```

不需要传入 JSON。

### 5. Headers 设置

不添加任何请求头。

### 6. 发送步骤

1. 新建一个 `GET` 请求。
2. URL 填写 `{{baseUrl}}/api/v1/articles`。
3. 在 `Params` 中添加 `current=1`、`size=10`。
4. Body 选择 `none`。
5. 点击 `Send`。

### 7. 预期结果

HTTP 状态码：

```text
200
```

业务返回码：

```text
20000
```

重点检查：

- `data.records` 是数组。
- `data.count` 是公开文章总数。
- `data.current = 1`。
- `data.size = 10`。
- 每篇文章包含标题、摘要、分类、标签、置顶和推荐状态。

### 8. 可选边界验证

设置：

```text
current=0&size=100
```

预期：

- `current` 被规范化为 `1`。
- `size` 被限制为 `50`。

---

## 三、接口 2：查询文章详情

### 1. Postman 基本信息

| 项目 | 内容 |
|---|---|
| 请求方法 | `GET` |
| 接口路径 | `/api/v1/articles/{id}` |
| 是否需要登录 | 否 |
| 是否需要请求头 | 否 |
| 是否需要 JSON 请求体 | 否 |
| 是否分页 | 否 |

### 2. URL

先使用已经存在的公开文章 ID，例如：

```text
{{baseUrl}}/api/v1/articles/1
```

### 3. Path 参数设置

| 参数 | 示例值 | 是否必填 | 说明 |
|---|---|---|---|
| `id` | `1` | 是 | 文章 ID，直接替换 URL 中的 `1` |

不要保留 `{id}` 原样发送，需要换成实际 ID。

### 4. Query 参数设置

无。

### 5. Body 设置

选择：

```text
Body -> none
```

不需要传入 JSON。

### 6. Headers 设置

不添加任何请求头。

### 7. 发送步骤

1. 新建 `GET` 请求。
2. URL 填写 `{{baseUrl}}/api/v1/articles/1`。
3. Body 选择 `none`。
4. 点击 `Send`。

### 8. 预期结果

HTTP 状态码：

```text
200
```

业务返回码：

```text
20000
```

重点检查：

- `data.id` 是文章 ID。
- `data.content` 返回完整正文。
- `data.tags` 返回标签数组。
- `data.viewCount` 返回当前阅读量。
- 每次成功发送该请求，数据库中的 `view_count` 都会增加 1。

### 9. 可选边界验证

测试不存在的文章 ID：

```text
{{baseUrl}}/api/v1/articles/999999
```

预期业务返回码：

```text
40400
```

测试非法文章 ID：

```text
{{baseUrl}}/api/v1/articles/abc
```

预期业务返回码：

```text
40000
```

---

## 四、接口 3：搜索公开文章

### 1. Postman 基本信息

| 项目 | 内容 |
|---|---|
| 请求方法 | `GET` |
| 接口路径 | `/api/v1/articles/search` |
| 是否需要登录 | 否 |
| 是否需要请求头 | 否 |
| 是否需要 JSON 请求体 | 否 |
| 是否分页 | 是 |

### 2. URL

```text
{{baseUrl}}/api/v1/articles/search
```

### 3. Params 设置

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `keywords` | `Java` | 否 | 搜索关键词 |
| `current` | `1` | 否 | 当前页码 |
| `size` | `10` | 否 | 每页数量 |

也可以直接使用：

```text
{{baseUrl}}/api/v1/articles/search?keywords=Java&current=1&size=10
```

### 4. Body 设置

选择：

```text
Body -> none
```

不需要传入 JSON。

### 5. Headers 设置

不添加任何请求头。

### 6. 发送步骤

1. 新建 `GET` 请求。
2. URL 填写 `{{baseUrl}}/api/v1/articles/search`。
3. 在 `Params` 中添加 `keywords=Java`、`current=1`、`size=10`。
4. Body 选择 `none`。
5. 点击 `Send`。

### 7. 预期结果

HTTP 状态码：

```text
200
```

业务返回码：

```text
20000
```

重点检查：

- `data.records` 返回匹配文章。
- `data.count` 是匹配文章总数。
- 搜索范围包含标题、摘要、正文、分类名称和标签名称。

### 8. 可选边界验证

不填写 `keywords`：

```text
{{baseUrl}}/api/v1/articles/search
```

预期：

- `code = 20000`
- `count = 0`
- `records = []`

---

## 五、接口 4：查询分类列表

### 1. Postman 基本信息

| 项目 | 内容 |
|---|---|
| 请求方法 | `GET` |
| 接口路径 | `/api/v1/categories` |
| 是否需要登录 | 否 |
| 是否需要请求头 | 否 |
| 是否需要 JSON 请求体 | 否 |
| 是否分页 | 否 |

### 2. URL

```text
{{baseUrl}}/api/v1/categories
```

### 3. Params 设置

无。

### 4. Body 设置

选择：

```text
Body -> none
```

不需要传入 JSON。

### 5. Headers 设置

不添加任何请求头。

### 6. 发送步骤

1. 新建 `GET` 请求。
2. URL 填写 `{{baseUrl}}/api/v1/categories`。
3. Params 留空。
4. Body 选择 `none`。
5. 点击 `Send`。

### 7. 预期结果

HTTP 状态码：

```text
200
```

业务返回码：

```text
20000
```

重点检查：

- `data` 是数组。
- 每个分类包含分类名称和公开文章数量。
- 空分类也会返回，文章数量为 0。

---

## 六、接口 5：分页查询分类文章

### 1. Postman 基本信息

| 项目 | 内容 |
|---|---|
| 请求方法 | `GET` |
| 接口路径 | `/api/v1/categories/{categoryId}/articles` |
| 是否需要登录 | 否 |
| 是否需要请求头 | 否 |
| 是否需要 JSON 请求体 | 否 |
| 是否分页 | 是 |

### 2. URL

使用实际分类 ID，例如：

```text
{{baseUrl}}/api/v1/categories/1/articles
```

### 3. Path 参数设置

| 参数 | 示例值 | 是否必填 | 说明 |
|---|---|---|---|
| `categoryId` | `1` | 是 | 分类 ID，直接替换 URL 中的 `1` |

### 4. Query 参数设置

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `current` | `1` | 否 | 当前页码 |
| `size` | `10` | 否 | 每页数量 |

完整 URL 示例：

```text
{{baseUrl}}/api/v1/categories/1/articles?current=1&size=10
```

### 5. Body 设置

选择：

```text
Body -> none
```

不需要传入 JSON。

### 6. Headers 设置

不添加任何请求头。

### 7. 发送步骤

1. 新建 `GET` 请求。
2. URL 填写分类文章接口。
3. 在 `Params` 中填写 `current=1`、`size=10`。
4. Body 选择 `none`。
5. 点击 `Send`。

### 8. 预期结果

HTTP 状态码：

```text
200
```

业务返回码：

```text
20000
```

重点检查：

- `data.records` 是文章数组。
- 文章全部属于指定分类。
- 返回文章包含完整的标签列表。
- 空分类返回 `count = 0` 和空 `records`。

### 9. 可选边界验证

使用不存在的分类 ID：

```text
{{baseUrl}}/api/v1/categories/999999/articles
```

预期：

```text
code = 20000
count = 0
records = []
```

---

## 七、接口 6：查询标签列表

### 1. Postman 基本信息

| 项目 | 内容 |
|---|---|
| 请求方法 | `GET` |
| 接口路径 | `/api/v1/tags` |
| 是否需要登录 | 否 |
| 是否需要请求头 | 否 |
| 是否需要 JSON 请求体 | 否 |
| 是否分页 | 否 |

### 2. URL

```text
{{baseUrl}}/api/v1/tags
```

### 3. Params 设置

无。

### 4. Body 设置

选择：

```text
Body -> none
```

不需要传入 JSON。

### 5. Headers 设置

不添加任何请求头。

### 6. 发送步骤

1. 新建 `GET` 请求。
2. URL 填写 `{{baseUrl}}/api/v1/tags`。
3. Params 留空。
4. Body 选择 `none`。
5. 点击 `Send`。

### 7. 预期结果

HTTP 状态码：

```text
200
```

业务返回码：

```text
20000
```

重点检查：

- `data` 是数组。
- 每个标签包含标签名称和公开文章数量。
- 无公开文章的标签也会返回，文章数量为 0。

---

## 八、接口 7：分页查询标签文章

### 1. Postman 基本信息

| 项目 | 内容 |
|---|---|
| 请求方法 | `GET` |
| 接口路径 | `/api/v1/tags/{tagId}/articles` |
| 是否需要登录 | 否 |
| 是否需要请求头 | 否 |
| 是否需要 JSON 请求体 | 否 |
| 是否分页 | 是 |

### 2. URL

使用实际标签 ID，例如：

```text
{{baseUrl}}/api/v1/tags/1/articles
```

### 3. Path 参数设置

| 参数 | 示例值 | 是否必填 | 说明 |
|---|---|---|---|
| `tagId` | `1` | 是 | 标签 ID，直接替换 URL 中的 `1` |

### 4. Query 参数设置

| Key | Value | 是否必填 | 说明 |
|---|---|---|---|
| `current` | `1` | 否 | 当前页码 |
| `size` | `10` | 否 | 每页数量 |

完整 URL 示例：

```text
{{baseUrl}}/api/v1/tags/1/articles?current=1&size=10
```

### 5. Body 设置

选择：

```text
Body -> none
```

不需要传入 JSON。

### 6. Headers 设置

不添加任何请求头。

### 7. 发送步骤

1. 新建 `GET` 请求。
2. URL 填写标签文章接口。
3. 在 `Params` 中填写 `current=1`、`size=10`。
4. Body 选择 `none`。
5. 点击 `Send`。

### 8. 预期结果

HTTP 状态码：

```text
200
```

业务返回码：

```text
20000
```

重点检查：

- `data.records` 是文章数组。
- 文章包含用于筛选的标签。
- 每篇文章的 `tags` 包含文章的全部标签。
- 不存在的标签返回 `count = 0` 和空 `records`。

### 9. 可选边界验证

使用不存在的标签 ID：

```text
{{baseUrl}}/api/v1/tags/999999/articles
```

预期：

```text
code = 20000
count = 0
records = []
```

---

## 九、接口 8：查询文章归档

### 1. Postman 基本信息

| 项目 | 内容 |
|---|---|
| 请求方法 | `GET` |
| 接口路径 | `/api/v1/archives` |
| 是否需要登录 | 否 |
| 是否需要请求头 | 否 |
| 是否需要 JSON 请求体 | 否 |
| 是否分页 | 否 |

### 2. URL

```text
{{baseUrl}}/api/v1/archives
```

### 3. Params 设置

无。

### 4. Body 设置

选择：

```text
Body -> none
```

不需要传入 JSON。

### 5. Headers 设置

不添加任何请求头。

### 6. 发送步骤

1. 新建 `GET` 请求。
2. URL 填写 `{{baseUrl}}/api/v1/archives`。
3. Params 留空。
4. Body 选择 `none`。
5. 点击 `Send`。

### 7. 预期结果

HTTP 状态码：

```text
200
```

业务返回码：

```text
20000
```

重点检查：

- `data` 是数组。
- 每个元素包含 `year`、`month`、`articles`。
- 同一月份的文章聚合到同一个数组中。
- 月份使用两位字符串，例如 `09`。

---

## 十、阶段一 Postman 验证总结

### 1. 请求方式

阶段一 8 个接口全部使用：

```text
GET
```

### 2. 请求体

阶段一 8 个接口全部不需要 JSON：

```text
Body -> none
```

### 3. 请求头

阶段一 8 个接口全部不需要：

```text
Authorization
Content-Type
```

### 4. 登录

阶段一 8 个接口全部不需要登录。

### 5. 参数位置

- 页码和每页数量：放在 `Params` 中。
- 文章 ID、分类 ID、标签 ID：直接放在 URL 路径中。
- 搜索关键词：放在 `Params` 中。

### 6. 正常结果判断

正常情况下：

```text
HTTP Status = 200
响应 code = 20000
```

### 7. 注意

文章详情接口虽然使用 GET，但每次成功访问都会增加文章阅读量。重复点击 `Send` 时，`viewCount` 会持续增加。
