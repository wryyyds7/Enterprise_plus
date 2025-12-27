# 企业推荐知识图谱配置文件

import os
from dotenv import load_dotenv

# 加载环境变量
load_dotenv()


class Config:
    """基础配置类"""
    # Flask配置
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'your-secret-key'
    DEBUG = os.environ.get('DEBUG', 'True').lower() == 'true'
    HOST = os.environ.get('HOST', '0.0.0.0')
    PORT = int(os.environ.get('PORT', 5000))
    
    # Neo4j配置
    NEO4J_URI = os.environ.get('NEO4J_URI') or 'bolt://localhost:7687'
    NEO4J_USER = os.environ.get('NEO4J_USER') or 'neo4j'
    NEO4J_PASSWORD = os.environ.get('NEO4J_PASSWORD') or 'wry5054755'
    
    # Redis配置
    REDIS_URL = os.environ.get('REDIS_URL') or 'redis://localhost:6379/0'
    CACHE_EXPIRE = int(os.environ.get('CACHE_EXPIRE', 3600))  # 缓存过期时间（秒）
    
    # Java后端API配置
    JAVA_API_BASE_URL = os.environ.get('JAVA_API_BASE_URL') or 'http://localhost:8089'
    
    # TagClient API
    TAG_API_ENTERPRISE = JAVA_API_BASE_URL + '/entity/tag/byEntity/enterprise/{enterpriseId}'
    TAG_API_POSITION = JAVA_API_BASE_URL + '/entity/tag/byEntity/position/{positionId}'
    
    # 企业API
    ENTERPRISE_API = JAVA_API_BASE_URL + '/enterprise'
    ENTERPRISE_API_BY_ID = JAVA_API_BASE_URL + '/enterprise/{enterpriseId}'
    
    # 职位API
    POSITION_API = JAVA_API_BASE_URL + '/position'
    POSITION_API_BY_ID = JAVA_API_BASE_URL + '/position/{positionId}'
    POSITION_API_BY_ENTERPRISE = JAVA_API_BASE_URL + '/position/enterprise/{enterpriseId}'
    
    # 推荐算法配置
    RECOMMENDATION_TOP_N = int(os.environ.get('RECOMMENDATION_TOP_N', 10))  # 推荐企业数量
    SIMILARITY_THRESHOLD = float(os.environ.get('SIMILARITY_THRESHOLD', 0.5))  # 相似度阈值
    GRAPH_UPDATE_INTERVAL = int(os.environ.get('GRAPH_UPDATE_INTERVAL', 3600))  # 图谱更新间隔（秒）
    
    # 日志配置
    LOG_LEVEL = os.environ.get('LOG_LEVEL') or 'INFO'
    LOG_FILE = os.environ.get('LOG_FILE') or 'app.log'


class DevelopmentConfig(Config):
    """开发环境配置"""
    DEBUG = True
    CACHE_EXPIRE = 300  # 开发环境缓存时间较短


class ProductionConfig(Config):
    """生产环境配置"""
    DEBUG = False
    CACHE_EXPIRE = 7200  # 生产环境缓存时间较长


# 根据环境选择配置
config = {
    'development': DevelopmentConfig,
    'production': ProductionConfig,
    'default': DevelopmentConfig
}


def get_config():
    """获取配置实例"""
    env = os.environ.get('FLASK_ENV') or 'default'
    return config[env]