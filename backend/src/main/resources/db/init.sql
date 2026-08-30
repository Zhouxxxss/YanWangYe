-- =====================================================================
-- 研王爷-考研伴学系统 数据库初始化
-- MySQL 8.0 / utf8mb4
-- 一期：账号权限、学习计时/日程、错题Anki、计划模板、RAG答疑、背诵、数据大盘
-- 二期预埋（空表+预留字段）：社区、搭子小组、择校库
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `yanyan` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `yanyan`;

-- ---------------------------------------------------------------------
-- 一、RBAC 权限体系（五表）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username`        VARCHAR(64)  NOT NULL COMMENT '登录名',
  `password`        VARCHAR(128) NOT NULL COMMENT 'BCrypt 密文',
  `nickname`        VARCHAR(64)  DEFAULT NULL,
  `avatar`          VARCHAR(512) DEFAULT NULL,
  `email`           VARCHAR(128) DEFAULT NULL,
  `style_token`     VARCHAR(64)  DEFAULT NULL COMMENT '头像动效 token(一期前端强交互)',
  `target_school_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '二期预留:择校目标院校',
  `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`         TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB COMMENT='用户';

CREATE TABLE IF NOT EXISTS `sys_role` (
  `id`   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(64) NOT NULL COMMENT '角色码,如 USER/ADMIN',
  `name` VARCHAR(64) DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`code`)
) ENGINE=InnoDB COMMENT='角色';

CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id`   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(128) NOT NULL COMMENT '权限码',
  `name` VARCHAR(64) DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`code`)
) ENGINE=InnoDB COMMENT='权限点(二期待分配)';

CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` BIGINT UNSIGNED NOT NULL,
  `role_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB COMMENT='用户-角色';

CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `role_id` BIGINT UNSIGNED NOT NULL,
  `perm_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`role_id`,`perm_id`)
) ENGINE=InnoDB COMMENT='角色-权限';

-- 二期预留权限点(默认不分配给任何角色,社区/小组/择校 Controller 直接 @PreAuthorize 引用)
INSERT IGNORE INTO `sys_permission` (`code`,`name`) VALUES
  ('community:post','社区发帖'),
  ('community:comment','社区评论'),
  ('community:like','社区点赞'),
  ('group:create','创建小组'),
  ('group:join','加入小组'),
  ('school:view','查看择校库');

-- ---------------------------------------------------------------------
-- 二、学习管理（一期）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `study_session` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`      BIGINT UNSIGNED NOT NULL,
  `subject`      VARCHAR(32) DEFAULT NULL,
  `start_time`   DATETIME DEFAULT NULL,
  `end_time`     DATETIME DEFAULT NULL,
  `duration_seconds` INT DEFAULT 0,
  `source`       VARCHAR(20) DEFAULT 'TIMER' COMMENT 'TIMER/POMODORO/REVIEW/RECITE',
  `create_time`  DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`      TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`,`start_time`)
) ENGINE=InnoDB COMMENT='学习时段(计时/番茄钟)';

CREATE TABLE IF NOT EXISTS `schedule_task` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`      BIGINT UNSIGNED NOT NULL,
  `title`        VARCHAR(255) NOT NULL,
  `subject`      VARCHAR(32) DEFAULT NULL,
  `task_date`    DATE NOT NULL,
  `done`         TINYINT NOT NULL DEFAULT 0,
  `source_plan`  VARCHAR(64) DEFAULT NULL COMMENT '来源模板/自建',
  `create_time`  DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`      TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`,`task_date`)
) ENGINE=InnoDB COMMENT='日程/今日任务(计划模板导入落点)';

CREATE TABLE IF NOT EXISTS `checkin_record` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT UNSIGNED NOT NULL,
  `checkin_date` DATE NOT NULL,
  `biz_type`    VARCHAR(20) DEFAULT 'CHECKIN',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date_type` (`user_id`,`checkin_date`,`biz_type`)
) ENGINE=InnoDB COMMENT='签到/打卡记录';

-- ---------------------------------------------------------------------
-- 三、错题 + Anki（一期）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `wrong_question` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`       BIGINT UNSIGNED NOT NULL,
  `subject`       VARCHAR(32) DEFAULT NULL,
  `chapter`       VARCHAR(128) DEFAULT NULL,
  `content`       TEXT,
  `my_answer`     TEXT,
  `correct_answer` TEXT,
  `error_reason`  VARCHAR(20) DEFAULT 'other' COMMENT 'careless/knowledge/method/other',
  `image_url`     VARCHAR(512) DEFAULT NULL,
  `repetition`    INT NOT NULL DEFAULT 0,
  `interval_days` INT NOT NULL DEFAULT 0,
  `ease`          DOUBLE NOT NULL DEFAULT 2.5,
  `next_review_date` DATE DEFAULT NULL,
  `create_time`   DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`       TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_subject` (`user_id`,`subject`)
) ENGINE=InnoDB COMMENT='错题(含SM-2复习字段)';

CREATE TABLE IF NOT EXISTS `review_record` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`    BIGINT UNSIGNED NOT NULL,
  `biz_key`    VARCHAR(64) NOT NULL COMMENT 'wrong_question:12 / recite_card:7',
  `quality`    INT NOT NULL,
  `interval_days` INT DEFAULT 0,
  `review_date` DATE DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`,`review_date`)
) ENGINE=InnoDB COMMENT='复习记录(Anki调度落账)';

-- ---------------------------------------------------------------------
-- 四、背诵打卡（一期）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `recite_card` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`       BIGINT UNSIGNED NOT NULL,
  `subject`       VARCHAR(32) DEFAULT NULL,
  `chapter`       VARCHAR(128) DEFAULT NULL,
  `content`       TEXT,
  `status`        VARCHAR(20) DEFAULT 'LEARNING' COMMENT 'LEARNING/MASTERED',
  `repetition`    INT NOT NULL DEFAULT 0,
  `interval_days` INT NOT NULL DEFAULT 0,
  `ease`          DOUBLE NOT NULL DEFAULT 2.5,
  `next_review_date` DATE DEFAULT NULL,
  `create_time`   DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`       TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_subject` (`user_id`,`subject`)
) ENGINE=InnoDB COMMENT='背诵卡片';

-- ---------------------------------------------------------------------
-- 五、RAG 溯源答疑（一期；向量库/Embedding 二期接入真实实现）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `document` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT UNSIGNED NOT NULL,
  `title`       VARCHAR(255) NOT NULL,
  `source_type` VARCHAR(20) DEFAULT 'manual' COMMENT 'manual/import/community(二期)',
  `visible`     VARCHAR(20) DEFAULT 'private' COMMENT 'private/public/org',
  `file_url`    VARCHAR(512) DEFAULT NULL,
  `chunk_num`   INT DEFAULT 0,
  `size_bytes`  BIGINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB COMMENT='知识库文档(一期手动/RAG答疑数据源)';

CREATE TABLE IF NOT EXISTS `document_chunk` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `doc_id`      BIGINT UNSIGNED NOT NULL,
  `chunk_index` INT NOT NULL,
  `content`     TEXT,
  `embedding`   BLOB COMMENT '二期:向量(BINARY/JSON占位)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_doc` (`doc_id`)
) ENGINE=InnoDB COMMENT='文档分块(二期向量化)';

-- ---------------------------------------------------------------------
-- 六、数据大盘（一期）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `study_stat_daily` (
  `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`         BIGINT UNSIGNED NOT NULL,
  `stat_date`       DATE NOT NULL,
  `study_seconds`   INT DEFAULT 0,
  `task_total`      INT DEFAULT 0,
  `task_done`       INT DEFAULT 0,
  `wrong_add`       INT DEFAULT 0,
  `wrong_review`    INT DEFAULT 0,
  `recite_done`     INT DEFAULT 0,
  `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`,`stat_date`)
) ENGINE=InnoDB COMMENT='学习日统计(预聚合,大盘数据源)';

-- =====================================================================
-- 七、二期预埋（仅建表,业务逻辑二期实现）
-- =====================================================================
CREATE TABLE IF NOT EXISTS `post` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `title` VARCHAR(255), `content` LONGTEXT,
  `school_id` BIGINT UNSIGNED DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP, `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, `deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`), KEY `idx_user` (`user_id`)
) ENGINE=InnoDB COMMENT='社区帖子(二期)';

CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `content` VARCHAR(2000),
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP, `deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`), KEY `idx_post` (`post_id`)
) ENGINE=InnoDB COMMENT='社区评论(二期)';

CREATE TABLE IF NOT EXISTS `likes` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `target_type` VARCHAR(20), `target_id` BIGINT UNSIGNED, `user_id` BIGINT UNSIGNED,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='点赞(二期)';

CREATE TABLE IF NOT EXISTS `group` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(128), `owner_id` BIGINT UNSIGNED, `intro` VARCHAR(512),
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP, `deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='搭子小组(二期)';

CREATE TABLE IF NOT EXISTS `group_member` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_id` BIGINT UNSIGNED, `user_id` BIGINT UNSIGNED, `join_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='小组成员(二期)';

CREATE TABLE IF NOT EXISTS `group_doc` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_id` BIGINT UNSIGNED, `doc_id` BIGINT UNSIGNED,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='小组共享文档(二期)';

CREATE TABLE IF NOT EXISTS `school` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(128), `province` VARCHAR(64), `category` VARCHAR(32), `level` VARCHAR(32) COMMENT '985/211/双一流',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='院校(二期,研招网同步)';

CREATE TABLE IF NOT EXISTS `major` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `school_id` BIGINT UNSIGNED, `name` VARCHAR(255), `code` VARCHAR(32),
  `exams` VARCHAR(512) COMMENT '四门考试科目',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='专业(二期)';

CREATE TABLE IF NOT EXISTS `admission_line` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `school_id` BIGINT UNSIGNED, `major_id` BIGINT UNSIGNED, `year` INT,
  `line` INT, `plan_num` INT, `records_num` INT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='复试线/招生/复录比(二期)';

CREATE TABLE IF NOT EXISTS `user_target` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `school_id` BIGINT UNSIGNED, `major_id` BIGINT UNSIGNED,
  `bind_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`), KEY `idx_user` (`user_id`)
) ENGINE=InnoDB COMMENT='目标院校绑定(二期衔接)';