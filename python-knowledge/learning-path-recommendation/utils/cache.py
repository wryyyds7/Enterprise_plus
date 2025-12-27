# 缓存工具类

import json
import redis
from config import get_config


class Cache:
    """Redis缓存类"""
    
    def __init__(self):
        """
        初始化Redis连接
        """
        self.config = get_config()
        self.redis_client = redis.from_url(
            self.config.REDIS_URL,
            decode_responses=True
        )
        self.expire_time = self.config.CACHE_EXPIRE
        self.key_prefix = self.config.CACHE_KEY_PREFIX
    
    def _get_full_key(self, key):
        """
        获取带前缀的完整键名
        :param key: 原始键名
        :return: 带前缀的完整键名
        """
        return f"{self.key_prefix}{key}"
    
    def get(self, key):
        """
        获取缓存数据
        :param key: 缓存键名
        :return: 缓存的数据，如果不存在返回None
        """
        full_key = self._get_full_key(key)
        try:
            data = self.redis_client.get(full_key)
            if data:
                return json.loads(data)
            return None
        except Exception as e:
            print(f"获取缓存失败: {str(e)}")
            return None
    
    def set(self, key, value, expire_time=None):
        """
        设置缓存数据
        :param key: 缓存键名
        :param value: 缓存数据
        :param expire_time: 过期时间（秒），如果为None则使用默认过期时间
        :return: True表示成功，False表示失败
        """
        full_key = self._get_full_key(key)
        expire = expire_time or self.expire_time
        try:
            self.redis_client.setex(
                full_key,
                expire,
                json.dumps(value, ensure_ascii=False)
            )
            return True
        except Exception as e:
            print(f"设置缓存失败: {str(e)}")
            return False
    
    def delete(self, key):
        """
        删除缓存
        :param key: 缓存键名
        :return: True表示成功，False表示失败
        """
        full_key = self._get_full_key(key)
        try:
            self.redis_client.delete(full_key)
            return True
        except Exception as e:
            print(f"删除缓存失败: {str(e)}")
            return False
    
    def exists(self, key):
        """
        检查缓存是否存在
        :param key: 缓存键名
        :return: True表示存在，False表示不存在
        """
        full_key = self._get_full_key(key)
        try:
            return self.redis_client.exists(full_key) > 0
        except Exception as e:
            print(f"检查缓存是否存在失败: {str(e)}")
            return False
    
    def flush_all(self):
        """
        清空所有带前缀的缓存
        :return: True表示成功，False表示失败
        """
        try:
            keys = self.redis_client.keys(f"{self.key_prefix}*")
            if keys:
                self.redis_client.delete(*keys)
            return True
        except Exception as e:
            print(f"清空缓存失败: {str(e)}")
            return False
    
    def get_user_skills_cache_key(self, user_id):
        """
        获取用户技能缓存键
        :param user_id: 用户ID
        :return: 缓存键名
        """
        return f"user_skills:{user_id}"
    
    def get_skill_graph_cache_key(self):
        """
        获取技能图谱缓存键
        :return: 缓存键名
        """
        return "skill_graph"
    
    def get_learning_path_cache_key(self, user_id, target_id):
        """
        获取学习路径缓存键
        :param user_id: 用户ID
        :param target_id: 目标ID
        :return: 缓存键名
        """
        return f"learning_path:{user_id}:{target_id}"
    
    def get_resource_cache_key(self, skill_id):
        """
        获取资源缓存键
        :param skill_id: 技能ID
        :return: 缓存键名
        """
        return f"resources:{skill_id}"


# 创建全局缓存实例
cache = Cache()