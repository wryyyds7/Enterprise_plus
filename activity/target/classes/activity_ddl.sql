-- 活动表
CREATE TABLE IF NOT EXISTS activity (
    activity_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    activity_title VARCHAR(200) NOT NULL COMMENT '活动标题',
    activity_type VARCHAR(50) NOT NULL COMMENT '活动类型',
    activity_desc TEXT COMMENT '活动描述',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    location VARCHAR(200) COMMENT '活动地点',
    organizer VARCHAR(100) COMMENT '组织者',
    contact_info VARCHAR(100) COMMENT '联系信息',
    poster_url VARCHAR(500) COMMENT '海报链接',
    status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    view_count BIGINT DEFAULT 0 COMMENT '浏览次数',
    like_count BIGINT DEFAULT 0 COMMENT '点赞次数',
    favorite_count BIGINT DEFAULT 0 COMMENT '收藏次数',
    registration_count BIGINT DEFAULT 0 COMMENT '报名人数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    PRIMARY KEY (activity_id),
    INDEX idx_activity_type (activity_type),
    INDEX idx_status (status),
    INDEX idx_create_by (create_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- 活动报名记录表
CREATE TABLE IF NOT EXISTS activity_registration (
    registration_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '报名ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    registration_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '报名状态',
    PRIMARY KEY (registration_id),
    UNIQUE KEY uk_activity_user (activity_id, user_id),
    INDEX idx_activity_id (activity_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (activity_id) REFERENCES activity(activity_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名记录表';

-- 活动收藏表
CREATE TABLE IF NOT EXISTS activity_favorite (
    favorite_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (favorite_id),
    UNIQUE KEY uk_activity_user (activity_id, user_id),
    INDEX idx_activity_id (activity_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (activity_id) REFERENCES activity(activity_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动收藏表';