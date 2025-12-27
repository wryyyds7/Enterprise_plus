-- 论坛板块表
CREATE TABLE IF NOT EXISTS forum_section (
    section_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '板块ID',
    section_name VARCHAR(100) NOT NULL COMMENT '板块名称',
    section_desc VARCHAR(500) COMMENT '板块描述',
    parent_section_id BIGINT COMMENT '父板块ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    topic_count BIGINT DEFAULT 0 COMMENT '主题数',
    post_count BIGINT DEFAULT 0 COMMENT '帖子数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (section_id),
    INDEX idx_parent_section (parent_section_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛板块表';

-- 论坛主题表
CREATE TABLE IF NOT EXISTS forum_topic (
    topic_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主题ID',
    section_id BIGINT NOT NULL COMMENT '板块ID',
    user_id BIGINT NOT NULL COMMENT '发帖人ID',
    topic_title VARCHAR(200) NOT NULL COMMENT '主题标题',
    topic_content LONGTEXT NOT NULL COMMENT '主题内容',
    topic_type VARCHAR(50) DEFAULT 'normal' COMMENT '主题类型',
    view_count BIGINT DEFAULT 0 COMMENT '浏览次数',
    reply_count BIGINT DEFAULT 0 COMMENT '回复次数',
    like_count BIGINT DEFAULT 0 COMMENT '点赞次数',
    favorite_count BIGINT DEFAULT 0 COMMENT '收藏次数',
    status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用 2精华 3置顶）',
    last_reply_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后回复时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (topic_id),
    INDEX idx_section_id (section_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_last_reply (last_reply_time),
    FOREIGN KEY (section_id) REFERENCES forum_section(section_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛主题表';

-- 论坛回复表
CREATE TABLE IF NOT EXISTS forum_reply (
    reply_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '回复ID',
    topic_id BIGINT NOT NULL COMMENT '主题ID',
    user_id BIGINT NOT NULL COMMENT '回复人ID',
    parent_reply_id BIGINT COMMENT '父回复ID',
    reply_content LONGTEXT NOT NULL COMMENT '回复内容',
    like_count BIGINT DEFAULT 0 COMMENT '点赞次数',
    status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (reply_id),
    INDEX idx_topic_id (topic_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_reply (parent_reply_id),
    INDEX idx_status (status),
    FOREIGN KEY (topic_id) REFERENCES forum_topic(topic_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_reply_id) REFERENCES forum_reply(reply_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛回复表';

-- 论坛收藏表
CREATE TABLE IF NOT EXISTS forum_favorite (
    favorite_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    topic_id BIGINT NOT NULL COMMENT '主题ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (favorite_id),
    UNIQUE KEY uk_topic_user (topic_id, user_id),
    INDEX idx_topic_id (topic_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (topic_id) REFERENCES forum_topic(topic_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛收藏表';