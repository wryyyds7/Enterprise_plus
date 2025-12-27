-- 学习路径相关表结构

-- 1. 技能表
CREATE TABLE IF NOT EXISTS skill (
    skill_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '技能ID',
    skill_name VARCHAR(100) NOT NULL COMMENT '技能名称',
    description VARCHAR(500) COMMENT '技能描述',
    skill_type VARCHAR(50) COMMENT '技能类型',
    parent_skill_id BIGINT COMMENT '父技能ID',
    level INT DEFAULT 1 COMMENT '技能级别',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (skill_id),
    INDEX idx_parent_skill (parent_skill_id),
    INDEX idx_skill_type (skill_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能表';

-- 2. 学习资源表
CREATE TABLE IF NOT EXISTS learning_resource (
    resource_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '资源ID',
    resource_title VARCHAR(200) NOT NULL COMMENT '资源标题',
    resource_type VARCHAR(50) NOT NULL COMMENT '资源类型（文章、视频、课程等）',
    resource_url TEXT NOT NULL COMMENT '资源链接',
    thumbnail_url VARCHAR(500) COMMENT '缩略图链接',
    description TEXT COMMENT '资源描述',
    skill_id BIGINT NOT NULL COMMENT '关联技能ID',
    difficulty VARCHAR(20) DEFAULT 'medium' COMMENT '难度级别（easy, medium, hard）',
    duration INT COMMENT '资源时长（分钟）',
    view_count BIGINT DEFAULT 0 COMMENT '浏览次数',
    like_count BIGINT DEFAULT 0 COMMENT '点赞次数',
    status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (resource_id),
    INDEX idx_skill_id (skill_id),
    INDEX idx_resource_type (resource_type),
    INDEX idx_difficulty (difficulty),
    INDEX idx_status (status),
    FOREIGN KEY (skill_id) REFERENCES skill(skill_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资源表';

-- 3. 技能依赖关系表
CREATE TABLE IF NOT EXISTS skill_dependency (
    dependency_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '依赖ID',
    skill_id BIGINT NOT NULL COMMENT '技能ID',
    prerequisite_skill_id BIGINT NOT NULL COMMENT '前置技能ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (dependency_id),
    UNIQUE KEY uk_skill_prerequisite (skill_id, prerequisite_skill_id),
    INDEX idx_skill_id (skill_id),
    INDEX idx_prerequisite_skill (prerequisite_skill_id),
    FOREIGN KEY (skill_id) REFERENCES skill(skill_id) ON DELETE CASCADE,
    FOREIGN KEY (prerequisite_skill_id) REFERENCES skill(skill_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能依赖关系表';

-- 4. 用户学习进度表
CREATE TABLE IF NOT EXISTS user_learning_progress (
    progress_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '进度ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    completion_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '完成率（0-100）',
    is_completed CHAR(1) DEFAULT '0' COMMENT '是否完成（0未完成 1已完成）',
    last_learn_time DATETIME COMMENT '最后学习时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (progress_id),
    UNIQUE KEY uk_user_resource (user_id, resource_id),
    INDEX idx_user_id (user_id),
    INDEX idx_resource_id (resource_id),
    INDEX idx_is_completed (is_completed),
    FOREIGN KEY (resource_id) REFERENCES learning_resource(resource_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户学习进度表';

-- 5. 用户技能掌握表
CREATE TABLE IF NOT EXISTS user_skill_mastery (
    mastery_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '掌握ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    skill_id BIGINT NOT NULL COMMENT '技能ID',
    mastery_level INT DEFAULT 1 COMMENT '掌握程度（1-5）',
    is_mastered CHAR(1) DEFAULT '0' COMMENT '是否掌握（0未掌握 1已掌握）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (mastery_id),
    UNIQUE KEY uk_user_skill (user_id, skill_id),
    INDEX idx_user_id (user_id),
    INDEX idx_skill_id (skill_id),
    INDEX idx_mastery_level (mastery_level),
    FOREIGN KEY (skill_id) REFERENCES skill(skill_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户技能掌握表';

-- 6. 用户头像表（增强现有avatar功能）
CREATE TABLE IF NOT EXISTS user_avatar (
    avatar_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '头像ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    avatar_url VARCHAR(500) NOT NULL COMMENT '头像链接',
    avatar_type VARCHAR(50) COMMENT '头像类型',
    file_size BIGINT COMMENT '文件大小（字节）',
    width INT COMMENT '宽度（像素）',
    height INT COMMENT '高度（像素）',
    is_default CHAR(1) DEFAULT '0' COMMENT '是否默认头像（0否 1是）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (avatar_id),
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户头像表';

-- 7. 学习路径表
CREATE TABLE IF NOT EXISTS learning_path (
    path_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '路径ID',
    path_name VARCHAR(200) NOT NULL COMMENT '路径名称',
    description TEXT COMMENT '路径描述',
    career_path VARCHAR(100) COMMENT '职业路径',
    total_duration INT COMMENT '预计总时长（分钟）',
    total_resources INT COMMENT '资源总数',
    status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (path_id),
    INDEX idx_career_path (career_path),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习路径表';

-- 8. 学习路径技能关联表
CREATE TABLE IF NOT EXISTS path_skill_relation (
    relation_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    path_id BIGINT NOT NULL COMMENT '路径ID',
    skill_id BIGINT NOT NULL COMMENT '技能ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (relation_id),
    UNIQUE KEY uk_path_skill (path_id, skill_id),
    INDEX idx_path_id (path_id),
    INDEX idx_skill_id (skill_id),
    FOREIGN KEY (path_id) REFERENCES learning_path(path_id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skill(skill_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习路径技能关联表';

-- 9. 用户学习路径表
CREATE TABLE IF NOT EXISTS user_learning_path (
    user_path_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户路径ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    path_id BIGINT NOT NULL COMMENT '路径ID',
    progress DECIMAL(5,2) DEFAULT 0.00 COMMENT '学习进度（0-100）',
    start_time DATETIME COMMENT '开始学习时间',
    end_time DATETIME COMMENT '完成学习时间',
    status VARCHAR(20) DEFAULT 'not_started' COMMENT '状态（not_started, in_progress, completed）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (user_path_id),
    UNIQUE KEY uk_user_path (user_id, path_id),
    INDEX idx_user_id (user_id),
    INDEX idx_path_id (path_id),
    INDEX idx_status (status),
    FOREIGN KEY (path_id) REFERENCES learning_path(path_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户学习路径表';
