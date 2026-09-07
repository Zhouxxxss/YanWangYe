-- 研王爷微服务 · 建库脚本（MySQL 8）已由 docker-entrypoint 已建库 ywy，此处建表
USE `ywy`;

-- ============ 认证与用户模块 ywy-auth / ywy-user（共用单表 user）============
-- 凭据(auth 主权)与档案(user 主权)合并为单表，避免 auth_account / user_profile 双表字段重复。
-- 公众号关注自动注册：account = 公众号 openid，username/password 为空，用户在「我的」页补充。
-- 账号密码注册：account 与 username 取同一值。
CREATE TABLE IF NOT EXISTS `user` (
  `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `account`          VARCHAR(64)  NOT NULL COMMENT '账号(公众号openid)；账号密码注册时与username同值',
  `username`         VARCHAR(64)  DEFAULT NULL COMMENT '登录名(可后置设置，公众号注册为空)',
  `password`         VARCHAR(128) DEFAULT NULL COMMENT 'BCrypt 散列(公众号注册为空)',
  `nickname`         VARCHAR(64)  DEFAULT NULL,
  `avatar`           VARCHAR(255) DEFAULT NULL,
  `email`            VARCHAR(128) DEFAULT NULL,
  `bio`              VARCHAR(500) DEFAULT NULL,
  `style_token`      VARCHAR(64)  DEFAULT NULL,
  `target_school_id` BIGINT       DEFAULT NULL COMMENT '二期择校库',
  `status`           VARCHAR(16)  NOT NULL DEFAULT 'NORMAL' COMMENT 'NORMAL/DISABLED',
  `role`             VARCHAR(32)  DEFAULT 'USER',
  `create_time`      DATETIME     DEFAULT NULL,
  `update_time`      DATETIME     DEFAULT NULL,
  `deleted`          TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account`  (`account`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户好友关系（user_id 引用 user.id）
CREATE TABLE IF NOT EXISTS `user_friend` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT   NOT NULL COMMENT '发起方',
  `friend_id`   BIGINT   NOT NULL COMMENT '好友',
  `status`      VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/ACCEPTED/REJECTED/BLOCKED',
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  `deleted`     TINYINT  NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_relation` (`user_id`,`friend_id`),
  KEY `idx_friend` (`friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============ 学习模块 ywy-study ============
CREATE TABLE IF NOT EXISTS `wrong_question` (
  `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`          BIGINT  NOT NULL,
  `subject`          VARCHAR(32)  DEFAULT NULL,
  `chapter`          VARCHAR(128) DEFAULT NULL,
  `content`          TEXT,
  `my_answer`        TEXT,
  `correct_answer`   TEXT,
  `error_reason`     VARCHAR(16)  DEFAULT NULL COMMENT 'careless/knowledge/method/other',
  `image_url`        VARCHAR(255) DEFAULT NULL,
  `repetition`       INT      NOT NULL DEFAULT 0,
  `interval_days`    INT      NOT NULL DEFAULT 0,
  `ease`             DOUBLE   NOT NULL DEFAULT 2.5,
  `next_review_date` DATE     DEFAULT NULL,
  `create_time`      DATETIME DEFAULT NULL,
  `update_time`      DATETIME DEFAULT NULL,
  `deleted`          TINYINT  NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_subject` (`user_id`,`subject`),
  KEY `idx_user_due` (`user_id`,`next_review_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `recite_card` (
  `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`          BIGINT  NOT NULL,
  `subject`          VARCHAR(32)  DEFAULT NULL,
  `chapter`          VARCHAR(128) DEFAULT NULL,
  `content`          TEXT,
  `status`           VARCHAR(16)  DEFAULT 'LEARNING' COMMENT 'LEARNING/MASTERED',
  `repetition`       INT      NOT NULL DEFAULT 0,
  `interval_days`    INT      NOT NULL DEFAULT 0,
  `ease`             DOUBLE   NOT NULL DEFAULT 2.5,
  `next_review_date` DATE     DEFAULT NULL,
  `create_time`      DATETIME DEFAULT NULL,
  `update_time`      DATETIME DEFAULT NULL,
  `deleted`          TINYINT  NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_subject` (`user_id`,`subject`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `study_session` (
  `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`          BIGINT  NOT NULL,
  `subject`          VARCHAR(32)  DEFAULT 'other',
  `start_time`       DATETIME DEFAULT NULL,
  `end_time`         DATETIME DEFAULT NULL,
  `duration_seconds` INT      NOT NULL DEFAULT 0,
  `source`           VARCHAR(16)  DEFAULT 'TIMER',
  `create_time`      DATETIME DEFAULT NULL,
  `update_time`      DATETIME DEFAULT NULL,
  `deleted`          TINYINT  NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_start` (`user_id`,`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============ RAG 知识库模块 ywy-rag ============
CREATE TABLE IF NOT EXISTS `knowledge_doc` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT   NOT NULL,
  `title`       VARCHAR(255) NOT NULL,
  `source_type` VARCHAR(16)  DEFAULT 'manual' COMMENT 'manual/community/school',
  `visibility`  VARCHAR(16)  DEFAULT 'private' COMMENT 'public/private/org',
  `object_path` VARCHAR(255) DEFAULT NULL,
  `status`      VARCHAR(16)  DEFAULT 'PROCESSING' COMMENT 'PROCESSING/READY/FAILED',
  `chunk_count` INT      NOT NULL DEFAULT 0,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  `deleted`     TINYINT  NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `knowledge_chunk` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `doc_id`      BIGINT NOT NULL,
  `chunk_index` INT    NOT NULL DEFAULT 0,
  `content`     TEXT,
  `vector_id`   VARCHAR(128) DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  `deleted`     TINYINT  NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_doc` (`doc_id`),
  UNIQUE KEY `uk_vector` (`vector_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;