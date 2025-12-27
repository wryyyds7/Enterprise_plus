-- 事件表
CREATE TABLE IF NOT EXISTS event (
    event_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '事件ID',
    event_title VARCHAR(200) NOT NULL COMMENT '事件标题',
    event_type VARCHAR(50) NOT NULL COMMENT '事件类型（轮播图、弹窗等）',
    event_content TEXT COMMENT '事件内容',
    target_url VARCHAR(500) COMMENT '目标链接',
    thumbnail_url VARCHAR(500) COMMENT '缩略图链接',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    display_position VARCHAR(100) NOT NULL COMMENT '展示位置',
    display_order INT DEFAULT 0 COMMENT '展示顺序',
    status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    click_count BIGINT DEFAULT 0 COMMENT '点击次数',
    view_count BIGINT DEFAULT 0 COMMENT '浏览次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    PRIMARY KEY (event_id),
    INDEX idx_event_type (event_type),
    INDEX idx_display_position (display_position),
    INDEX idx_status (status),
    INDEX idx_create_by (create_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件表';