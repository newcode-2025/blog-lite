-- blog-lite 数据库建库建表脚本
-- 生成日期：2026-10-04
-- 适用数据库：MySQL 8.0+
-- 说明：本脚本只创建数据库和表结构，不插入业务数据。
-- 执行方式：mysql -u your_mysql_user -p < database/blog_lite_schema.sql

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `blog_lite` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `blog_lite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `api_resource` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '接口资源 ID',
  `resource_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源名称',
  `url_pattern` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Ant 风格 URL 规则；根节点为空',
  `http_method` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'HTTP 方法；根节点为空',
  `parent_id` bigint unsigned DEFAULT NULL COMMENT '父资源 ID，根资源为空',
  `is_anonymous` tinyint NOT NULL DEFAULT '0' COMMENT '是否匿名：0 否，1 是',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1 启用，0 禁用',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '同组排序值',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_api_resource_method_url` (`http_method`,`url_pattern`),
  KEY `idx_api_resource_parent_sort` (`parent_id`,`sort_order`),
  KEY `idx_api_resource_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='RBAC 接口资源表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章标题',
  `summary` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章摘要',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章正文',
  `cover_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面地址',
  `category_id` bigint unsigned DEFAULT NULL COMMENT '分类ID',
  `author_id` bigint unsigned DEFAULT NULL COMMENT '作者ID，用户表后续创建',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '1公开，2密码，3草稿',
  `access_password` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章访问密码',
  `is_top` tinyint NOT NULL DEFAULT '0' COMMENT '是否置顶',
  `is_featured` tinyint NOT NULL DEFAULT '0' COMMENT '是否推荐',
  `view_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '阅读量',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  `published_at` datetime DEFAULT NULL COMMENT '发布时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_category_id` (`category_id`),
  KEY `idx_article_author_id` (`author_id`),
  KEY `idx_article_status_deleted` (`status`,`is_deleted`),
  KEY `idx_article_top_featured` (`is_top`,`is_featured`),
  CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_comment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '评论 ID',
  `article_id` bigint unsigned NOT NULL COMMENT '文章 ID',
  `user_id` bigint NOT NULL COMMENT '评论用户 ID',
  `parent_id` bigint unsigned DEFAULT NULL COMMENT '顶级评论 ID；顶级评论为 NULL，回复保存所属顶级评论 ID',
  `reply_to_user_id` bigint DEFAULT NULL COMMENT '被回复用户 ID；顶级评论为 NULL',
  `content` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0 待审核，1 已通过，2 已拒绝',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否软删除：0 否，1 是',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_comment_public` (`article_id`,`status`,`parent_id`,`is_deleted`,`created_at`),
  KEY `idx_article_comment_parent` (`parent_id`,`status`,`is_deleted`),
  KEY `idx_article_comment_user` (`user_id`,`created_at`),
  KEY `idx_article_comment_reply_to` (`reply_to_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章评论表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `article_id` bigint unsigned NOT NULL COMMENT '文章ID',
  `tag_id` bigint unsigned NOT NULL COMMENT '标签ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_tag` (`article_id`,`tag_id`),
  KEY `idx_article_tag_tag_id` (`tag_id`),
  CONSTRAINT `fk_article_tag_article` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_article_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exception_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '日志 ID',
  `request_method` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'HTTP 请求方法',
  `request_uri` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求路径',
  `handler_method` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '控制器处理方法',
  `request_params` text COLLATE utf8mb4_unicode_ci COMMENT '脱敏并截断后的请求参数',
  `exception_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常类型',
  `exception_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '异常消息',
  `stack_trace` text COLLATE utf8mb4_unicode_ci COMMENT '异常堆栈',
  `operator_id` bigint DEFAULT NULL COMMENT '当前用户 ID',
  `operator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '当前用户名称',
  `ip_address` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求 IP',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_exception_log_created_at` (`created_at`),
  KEY `idx_exception_log_exception_type` (`exception_type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统异常日志表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friend_link` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '友链名称',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '友链头像地址',
  `address` varchar(500) NOT NULL COMMENT '友链地址',
  `description` varchar(500) DEFAULT NULL COMMENT '友链介绍',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1 显示，0 隐藏',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_friend_link_address` (`address`),
  KEY `idx_friend_link_public` (`status`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='友情链接表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '通知记录 ID',
  `notification_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知类型',
  `business_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务类型',
  `business_id` bigint unsigned NOT NULL COMMENT '业务 ID',
  `recipient_user_id` bigint NOT NULL COMMENT '接收人用户 ID',
  `recipient_email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接收人邮箱快照',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '发送状态：0 待发送，1 成功，2 失败',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '重试次数',
  `last_error` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最近一次错误',
  `sent_at` datetime DEFAULT NULL COMMENT '成功发送时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notification_event_recipient` (`notification_type`,`business_type`,`business_id`,`recipient_user_id`),
  KEY `idx_notification_log_status` (`status`),
  KEY `idx_notification_log_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件通知记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '日志 ID',
  `module` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作模块',
  `operation_action` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作动作',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作描述',
  `request_method` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'HTTP 请求方法',
  `request_uri` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求路径',
  `handler_method` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '控制器处理方法',
  `request_params` text COLLATE utf8mb4_unicode_ci COMMENT '脱敏并截断后的请求参数',
  `response_code` int DEFAULT NULL COMMENT '业务响应码',
  `success` tinyint NOT NULL DEFAULT '1' COMMENT '是否成功：0 失败，1 成功',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人 ID',
  `operator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人名称',
  `ip_address` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作 IP',
  `duration_ms` bigint NOT NULL DEFAULT '0' COMMENT '执行耗时毫秒',
  `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '失败原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_operation_log_operator_id` (`operator_id`),
  KEY `idx_operation_log_created_at` (`created_at`),
  KEY `idx_operation_log_request_uri` (`request_uri`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `photo` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `album_id` bigint unsigned NOT NULL COMMENT '所属相册 ID',
  `name` varchar(100) DEFAULT NULL COMMENT '图片名称',
  `description` varchar(500) DEFAULT NULL COMMENT '图片描述',
  `photo_url` varchar(500) NOT NULL COMMENT '图片 URL',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否软删除：0 否，1 是',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_photo_album_id` (`album_id`,`is_deleted`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='相册图片表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `photo_album` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '相册名称',
  `description` varchar(500) DEFAULT NULL COMMENT '相册描述',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '相册封面 URL',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1 公开，2 私密',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否软删除：0 否，1 是',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_photo_album_name` (`name`),
  KEY `idx_photo_album_public` (`status`,`is_deleted`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='相册表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rbac_menu` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '菜单 ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '前端路由地址',
  `component` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '前端组件标识',
  `icon` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单图标',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '同级排序值',
  `parent_id` bigint unsigned DEFAULT NULL COMMENT '父菜单 ID，根菜单为空',
  `is_hidden` tinyint NOT NULL DEFAULT '0' COMMENT '是否隐藏：0 显示，1 隐藏',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1 启用，0 禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rbac_menu_parent_name` (`parent_id`,`name`),
  KEY `idx_rbac_menu_parent_sort` (`parent_id`,`sort_order`),
  KEY `idx_rbac_menu_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='RBAC 后台菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rbac_role` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '角色 ID',
  `role_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码，例如 ADMIN、USER',
  `role_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色说明',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1 启用，0 禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rbac_role_code` (`role_code`),
  UNIQUE KEY `uk_rbac_role_name` (`role_name`),
  KEY `idx_rbac_role_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='RBAC 角色表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_menu` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '角色菜单 ID',
  `role_id` bigint unsigned NOT NULL COMMENT 'rbac_role.id',
  `menu_id` bigint unsigned NOT NULL COMMENT 'rbac_menu.id',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `idx_role_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_resource` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '角色接口资源 ID',
  `role_id` bigint unsigned NOT NULL COMMENT 'rbac_role.id',
  `resource_id` bigint unsigned NOT NULL COMMENT 'api_resource.id',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_resource` (`role_id`,`resource_id`),
  KEY `idx_role_resource_resource_id` (`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色接口资源关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scheduled_job` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '任务 ID',
  `task_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '白名单任务键',
  `job_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务分组',
  `cron_expression` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Cron 表达式',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0 暂停，1 运行',
  `concurrent` tinyint NOT NULL DEFAULT '0' COMMENT '是否允许并发：0 否，1 是',
  `misfire_policy` tinyint NOT NULL DEFAULT '0' COMMENT '错失执行策略',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务说明',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scheduled_job_task_key` (`task_key`),
  KEY `idx_scheduled_job_group_status` (`job_group`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='定时任务定义表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scheduled_job_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '日志 ID',
  `job_id` bigint unsigned DEFAULT NULL COMMENT '任务 ID',
  `task_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '白名单任务键',
  `job_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务分组',
  `trigger_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发方式',
  `status` tinyint NOT NULL COMMENT '执行状态：0 失败，1 成功',
  `job_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '执行消息',
  `exception_info` text COLLATE utf8mb4_unicode_ci COMMENT '异常信息',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration_ms` bigint DEFAULT NULL COMMENT '执行耗时毫秒',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_scheduled_job_log_job_id` (`job_id`),
  KEY `idx_scheduled_job_log_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='定时任务执行日志表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `site_visit_daily` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '汇总 ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `pv_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '页面访问量',
  `uv_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '独立访客数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_visit_daily_date` (`stat_date`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站每日访问汇总表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `site_visitor_daily` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '访客记录 ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `visitor_hash` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加盐访客哈希',
  `first_seen_at` datetime NOT NULL COMMENT '当日首次访问时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_visitor_daily_date_hash` (`stat_date`,`visitor_hash`),
  KEY `idx_site_visitor_daily_date` (`stat_date`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站每日访客去重表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `talk` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '发布用户 ID',
  `content` text NOT NULL COMMENT '说说正文',
  `images` json DEFAULT NULL COMMENT '图片 URL 数组',
  `is_top` tinyint NOT NULL DEFAULT '0' COMMENT '是否置顶：0 否，1 是',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1 公开，2 私密',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否软删除：0 否，1 是',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_talk_user_id` (`user_id`),
  KEY `idx_talk_public` (`status`,`is_deleted`,`is_top`,`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='说说表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` varchar(32) NOT NULL COMMENT '登录用户名',
  `password_hash` varchar(100) NOT NULL COMMENT 'BCrypt 密码哈希',
  `nickname` varchar(50) NOT NULL COMMENT '用户昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像地址',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1 正常，0 禁用',
  `last_login_at` datetime DEFAULT NULL COMMENT '最后登录时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_account_username` (`username`),
  UNIQUE KEY `uk_user_account_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户账号表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户角色 ID',
  `user_id` bigint NOT NULL COMMENT 'user_account.id',
  `role_id` bigint unsigned NOT NULL COMMENT 'rbac_role.id',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_role_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `website_config` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '配置 ID',
  `config_json` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站配置 JSON',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
