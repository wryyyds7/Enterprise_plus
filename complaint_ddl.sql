-- 创建投诉表
CREATE TABLE `complaint` (
  `complaint_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '投诉ID',
  `user_id` BIGINT NOT NULL COMMENT '投诉用户ID',
  `complained_type` VARCHAR(50) NOT NULL COMMENT '投诉对象类型（enterprise/position/user/event/activity/forum_topic/forum_reply）',
  `complained_id` BIGINT NOT NULL COMMENT '投诉对象ID',
  `complaint_title` VARCHAR(200) NOT NULL COMMENT '投诉标题',
  `complaint_content` TEXT NOT NULL COMMENT '投诉内容',
  `complaint_status` VARCHAR(50) NOT NULL DEFAULT 'PENDING' COMMENT '投诉状态（PENDING/IN_PROGRESS/RESOLVED/REJECTED）',
  `handle_result` TEXT COMMENT '处理结果',
  `handler_id` BIGINT COMMENT '处理人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`complaint_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_complained` (`complained_type`, `complained_id`),
  KEY `idx_status` (`complaint_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投诉表';

-- 创建投诉附件表
CREATE TABLE `complaint_attachment` (
  `attachment_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `complaint_id` BIGINT NOT NULL COMMENT '投诉ID',
  `file_name` VARCHAR(200) NOT NULL COMMENT '文件名',
  `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
  `file_size` BIGINT NOT NULL COMMENT '文件大小（字节）',
  `file_type` VARCHAR(100) NOT NULL COMMENT '文件类型',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`attachment_id`),
  KEY `idx_complaint_id` (`complaint_id`),
  CONSTRAINT `fk_complaint_attachment` FOREIGN KEY (`complaint_id`) REFERENCES `complaint` (`complaint_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投诉附件表';