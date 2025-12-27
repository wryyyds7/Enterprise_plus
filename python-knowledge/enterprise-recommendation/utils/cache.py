# 缓存工具

import redis
import json
from functools import wraps
from config import get_config

# 获取配置
config = get_config()

# 初始化Redis连接
redis_client = redis.from_url(config.REDIS_URL)


def cache(key_pattern, expire=config.CACHE_EXPIRE):
    """
    缓存装饰器
    :param key_pattern: 缓存键模式，可以包含{param}占位符
    :param expire: 缓存过期时间（秒）
    :return: 装饰器函数
    """
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            # 生成缓存键
            key = key_pattern
            for i, arg in enumerate(args):
                if isinstance(arg, str) or isinstance(arg, int):
                    key = key.replace(f"{{{i}}}", str(arg))
            
            for k, v in kwargs.items():
                if isinstance(v, str) or isinstance(v, int):
                    key = key.replace(f"{{{k}}}", str(v))
            
            # 尝试从缓存获取数据
            cached_data = redis_client.get(key)
            if cached_data:
                return json.loads(cached_data)
            
            # 调用原函数
            result = func(*args, **kwargs)
            
            # 将结果存入缓存
            redis_client.setex(key, expire, json.dumps(result))
            
            return result
        return wrapper
    return decorator


def set_cache(key, value, expire=config.CACHE_EXPIRE):
    """
    设置缓存
    :param key: 缓存键
    :param value: 缓存值
    :param expire: 缓存过期时间（秒）
    """
    redis_client.setex(key, expire, json.dumps(value))


def get_cache(key):
    """
    获取缓存
    :param key: 缓存键
    :return: 缓存值，如果不存在返回None
    """
    cached_data = redis_client.get(key)
    if cached_data:
        return json.loads(cached_data)
    return None


def delete_cache(key):
    """
    删除缓存
    :param key: 缓存键
    """
    redis_client.delete(key)


def clear_cache(pattern):
    """
    清除匹配模式的所有缓存
    :param pattern: 键模式（支持通配符*）
    """
    keys = redis_client.keys(pattern)
    if keys:
        redis_client.delete(*keys)


def cache_graph(graph_name, graph_data, expire=config.CACHE_EXPIRE):
    """
    缓存知识图谱数据
    :param graph_name: 图谱名称
    :param graph_data: 图谱数据
    :param expire: 缓存过期时间（秒）
    """
    key = f"graph:{graph_name}"
    set_cache(key, graph_data, expire)


def get_cached_graph(graph_name):
    """
    获取缓存的知识图谱数据
    :param graph_name: 图谱名称
    :return: 图谱数据，如果不存在返回None
    """
    key = f"graph:{graph_name}"
    return get_cache(key)


def cache_recommendation(user_id, recommendation, expire=config.CACHE_EXPIRE):
    """
    缓存推荐结果
    :param user_id: 用户ID
    :param recommendation: 推荐结果
    :param expire: 缓存过期时间（秒）
    """
    key = f"recommendation:user:{user_id}"
    set_cache(key, recommendation, expire)


def get_cached_recommendation(user_id):
    """
    获取缓存的推荐结果
    :param user_id: 用户ID
    :return: 推荐结果，如果不存在返回None
    """
    key = f"recommendation:user:{user_id}"
    return get_cache(key)


def update_entity_cache(entity_type, entity_id, entity_data, expire=config.CACHE_EXPIRE):
    """
    更新实体缓存
    :param entity_type: 实体类型（enterprise, position, tag等）
    :param entity_id: 实体ID
    :param entity_data: 实体数据
    :param expire: 缓存过期时间（秒）
    """
    key = f"entity:{entity_type}:{entity_id}"
    set_cache(key, entity_data, expire)


def get_entity_cache(entity_type, entity_id):
    """
    获取实体缓存
    :param entity_type: 实体类型
    :param entity_id: 实体ID
    :return: 实体数据，如果不存在返回None
    """
    key = f"entity:{entity_type}:{entity_id}"
    return get_cache(key)


def invalidate_enterprise_cache(enterprise_id):
    """
    使企业相关缓存失效
    :param enterprise_id: 企业ID
    """
    # 删除企业缓存
    delete_cache(f"entity:enterprise:{enterprise_id}")
    # 删除企业标签缓存
    delete_cache(f"tags:enterprise:{enterprise_id}")
    # 删除企业职位缓存
    delete_cache(f"positions:enterprise:{enterprise_id}")
    # 清除所有用户的推荐缓存（因为企业信息变化可能影响推荐结果）
    clear_cache("recommendation:user:*")


def invalidate_position_cache(position_id):
    """
    使职位相关缓存失效
    :param position_id: 职位ID
    """
    # 删除职位缓存
    delete_cache(f"entity:position:{position_id}")
    # 删除职位标签缓存
    delete_cache(f"tags:position:{position_id}")
    # 清除所有用户的推荐缓存
    clear_cache("recommendation:user:*")


def invalidate_tag_cache(entity_type, entity_id):
    """
    使标签相关缓存失效
    :param entity_type: 实体类型
    :param entity_id: 实体ID
    """
    # 删除标签缓存
    delete_cache(f"tags:{entity_type}:{entity_id}")
    # 清除所有用户的推荐缓存
    clear_cache("recommendation:user:*")