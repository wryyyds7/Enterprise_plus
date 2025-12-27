# 学习路径推荐模块配置文件

import os
from dotenv import load_dotenv

# 加载环境变量
load_dotenv()


class Config:
    """配置类"""
    
    # Flask 配置
    FLASK_HOST = os.environ.get('FLASK_HOST', '0.0.0.0')
    FLASK_PORT = int(os.environ.get('FLASK_PORT', 5001))
    DEBUG = os.environ.get('DEBUG', 'True').lower() in ['true', '1', 'yes']
    
    # Neo4j 数据库配置
    NEO4J_URI = os.environ.get('NEO4J_URI', 'bolt://localhost:7687')
    NEO4J_USER = os.environ.get('NEO4J_USER', 'neo4j')
    NEO4J_PASSWORD = os.environ.get('NEO4J_PASSWORD', 'password')
    
    # Redis 缓存配置
    REDIS_URL = os.environ.get('REDIS_URL', 'redis://localhost:6379/1')
    CACHE_EXPIRE = int(os.environ.get('CACHE_EXPIRE', 3600))  # 缓存过期时间（秒）
    
    # Java 后端 API 配置
    JAVA_API_BASE_URL = os.environ.get('JAVA_API_BASE_URL', 'http://localhost:8080')
    
    # API 端点配置
    SKILL_API = f"{JAVA_API_BASE_URL}/api/skill"
    SKILL_LIST_API = f"{SKILL_API}/list"
    SKILL_BY_ID_API = f"{SKILL_API}/{{skillId}}"
    
    RESOURCE_API = f"{JAVA_API_BASE_URL}/api/resource"
    RESOURCE_LIST_API = f"{RESOURCE_API}/list"
    RESOURCE_BY_ID_API = f"{RESOURCE_API}/{{resourceId}}"
    RESOURCE_BY_SKILL_API = f"{RESOURCE_API}/by-skill/{{skillId}}"
    
    TAG_API = f"{JAVA_API_BASE_URL}/entity/tag"
    TAG_BY_ENTITY_API = f"{TAG_API}/byEntity/{{entityType}}/{{entityId}}"
    
    USER_API = f"{JAVA_API_BASE_URL}/api/user"
    USER_SKILLS_API = f"{USER_API}/{{userId}}/skills"
    
    # 路径规划配置
    PATH_PLANNING_ALGORITHM = os.environ.get('PATH_PLANNING_ALGORITHM', 'a_star')  # dijkstra, a_star
    MAX_PATH_LENGTH = int(os.environ.get('MAX_PATH_LENGTH', 10))  # 最大路径长度
    MAX_PATHS_TO_CONSIDER = int(os.environ.get('MAX_PATHS_TO_CONSIDER', 50))  # 考虑的最大路径数
    
    # 推荐配置
    DEFAULT_LEARNING_PATH_LENGTH = int(os.environ.get('DEFAULT_LEARNING_PATH_LENGTH', 8))
    MAX_RESOURCES_PER_SKILL = int(os.environ.get('MAX_RESOURCES_PER_SKILL', 3))
    
    # 权重配置
    DIFFICULTY_WEIGHT = float(os.environ.get('DIFFICULTY_WEIGHT', 0.3))
    TIME_WEIGHT = float(os.environ.get('TIME_WEIGHT', 0.3))
    RELEVANCE_WEIGHT = float(os.environ.get('RELEVANCE_WEIGHT', 0.4))
    
    # 日志配置
    LOG_LEVEL = os.environ.get('LOG_LEVEL', 'INFO')
    LOG_FILE = os.environ.get('LOG_FILE', 'app.log')
    
    # 学习风格配置
    LEARNING_STYLES = ['理论型', '实践型', '混合型']
    RESOURCE_TYPES = ['视频教程', '在线课程', '文档', '实战项目', '练习']
    
    # 技能预测配置
    SKILL_PREDICTION_MODEL = os.environ.get('SKILL_PREDICTION_MODEL', 'decision_tree')  # decision_tree, random_forest
    MIN_LEARNING_SUCCESS_RATE = float(os.environ.get('MIN_LEARNING_SUCCESS_RATE', 0.6))  # 最低学习成功率
    MAX_LEARNING_SUCCESS_RATE = float(os.environ.get('MAX_LEARNING_SUCCESS_RATE', 0.9))  # 最高学习成功率
    
    # 缓存键前缀
    CACHE_KEY_PREFIX = 'learning_path:'
    
    def __repr__(self):
        return f"Config(FLASK_PORT={self.FLASK_PORT}, DEBUG={self.DEBUG}, NEO4J_URI={self.NEO4J_URI})"


def get_config():
    """
    获取配置实例
    :return: 配置实例
    """
    return Config()