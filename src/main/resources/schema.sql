DROP TABLE IF EXISTS `user`;

CREATE TABLE IF NOT EXISTS `user` (
    `user_id`         varchar(30)  NOT NULL COMMENT '사용자아이디',
    `password`        varchar(255) NOT NULL COMMENT '비밀번호(ENC)',
    `is_verified`     char(1)      NOT NULL COMMENT 'T or F',
    `update_date`     timestamp    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자정보';
