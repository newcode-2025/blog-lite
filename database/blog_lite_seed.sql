-- blog-lite 最小初始化数据
-- 生成日期：2026-10-04
-- 适用数据库：MySQL 8.0+
-- 说明：本脚本只初始化运行所需的角色、网站配置和基础接口权限。
-- 不包含管理员账号、文章、评论等业务测试数据。
-- 执行方式：mysql -u your_mysql_user -p blog_lite < database/blog_lite_seed.sql

USE `blog_lite`;

START TRANSACTION;

-- 基础角色
INSERT INTO `rbac_role` (`role_code`, `role_name`, `description`, `status`)
VALUES
    ('ADMIN', '管理员', '拥有后台全局管理权限', 1),
    ('USER', '普通用户', '拥有登录、资料维护和本人文章管理权限', 1)
ON DUPLICATE KEY UPDATE
    `role_name` = VALUES(`role_name`),
    `description` = VALUES(`description`),
    `status` = VALUES(`status`);

-- 默认网站配置
INSERT INTO `website_config` (`id`, `config_json`, `version`)
VALUES (
    1,
    '{"siteName":"blog-lite","siteDescription":"","authorName":"","authorAvatar":"","authorIntro":"","logo":"","notice":"","websiteCreateTime":"","icpNumber":"","github":"","gitee":"","qq":"","weChat":"","weibo":"","csdn":"","zhihu":"","juejin":"","twitter":"","stackOverflow":"","email":"","commentReviewEnabled":1,"emailNotificationEnabled":0}',
    0
)
ON DUPLICATE KEY UPDATE `id` = `id`;

-- 后台接口基础资源
INSERT INTO `api_resource`
    (`resource_name`, `url_pattern`, `http_method`, `parent_id`, `is_anonymous`, `status`, `sort_order`)
VALUES
    ('后台接口 GET', '/api/v1/admin/**', 'GET', NULL, 0, 1, 10),
    ('后台接口 POST', '/api/v1/admin/**', 'POST', NULL, 0, 1, 11),
    ('后台接口 PUT', '/api/v1/admin/**', 'PUT', NULL, 0, 1, 12),
    ('后台接口 PATCH', '/api/v1/admin/**', 'PATCH', NULL, 0, 1, 13),
    ('后台接口 DELETE', '/api/v1/admin/**', 'DELETE', NULL, 0, 1, 14),
    ('文章管理查询', '/api/v1/admin/articles/**', 'GET', NULL, 0, 1, 20),
    ('文章管理新增', '/api/v1/admin/articles/**', 'POST', NULL, 0, 1, 21),
    ('文章管理编辑', '/api/v1/admin/articles/**', 'PUT', NULL, 0, 1, 22),
    ('文章管理删除', '/api/v1/admin/articles/**', 'DELETE', NULL, 0, 1, 23),
    ('后台媒体上传', '/api/v1/admin/media/**', 'POST', NULL, 0, 1, 24),
    ('用户头像上传', '/api/v1/users/me/avatar', 'POST', NULL, 0, 1, 25),
    ('公开站点信息', '/api/v1/site/**', 'GET', NULL, 1, 1, 30),
    ('公开访问上报', '/api/v1/stats/report', 'POST', NULL, 1, 1, 31)
ON DUPLICATE KEY UPDATE
    `resource_name` = VALUES(`resource_name`),
    `parent_id` = VALUES(`parent_id`),
    `is_anonymous` = VALUES(`is_anonymous`),
    `status` = VALUES(`status`),
    `sort_order` = VALUES(`sort_order`);

-- 管理员拥有全部后台接口权限
INSERT IGNORE INTO `role_resource` (`role_id`, `resource_id`)
SELECT r.`id`, ar.`id`
FROM `rbac_role` r
JOIN `api_resource` ar
  ON ar.`is_anonymous` = 0
 AND ar.`url_pattern` LIKE '/api/v1/admin/%'
WHERE r.`role_code` = 'ADMIN' AND r.`status` = 1;

-- 普通用户可以管理本人文章，并可以上传头像
INSERT IGNORE INTO `role_resource` (`role_id`, `resource_id`)
SELECT r.`id`, ar.`id`
FROM `rbac_role` r
JOIN `api_resource` ar
  ON (
      ar.`url_pattern` = '/api/v1/admin/articles/**'
      OR ar.`url_pattern` = '/api/v1/users/me/avatar'
  )
WHERE r.`role_code` = 'USER'
  AND r.`status` = 1
  AND ar.`status` = 1;

-- 管理员也可以上传头像
INSERT IGNORE INTO `role_resource` (`role_id`, `resource_id`)
SELECT r.`id`, ar.`id`
FROM `rbac_role` r
JOIN `api_resource` ar
  ON ar.`url_pattern` = '/api/v1/users/me/avatar'
WHERE r.`role_code` = 'ADMIN'
  AND r.`status` = 1
  AND ar.`status` = 1;

COMMIT;
