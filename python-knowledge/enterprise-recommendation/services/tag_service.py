# 标签服务（调用Java后端API）

import requests
from config import get_config
from utils.cache import cache, invalidate_tag_cache

# 获取配置
config = get_config()


class TagService:
    """标签服务类"""
    
    @staticmethod
    @cache("tags:enterprise:{enterpriseId}", expire=config.CACHE_EXPIRE)
    def get_enterprise_tags(enterprise_id):
        """
        获取企业标签
        :param enterprise_id: 企业ID
        :return: 企业标签列表
        """
        try:
            url = config.TAG_API_ENTERPRISE.format(enterpriseId=enterprise_id)
            response = requests.get(url, timeout=10)
            response.raise_for_status()
            
            result = response.json()
            if result.get("code") == 200:
                tags = result.get("data", [])
                # 提取标签名称列表
                tag_names = [tag.get("name") for tag in tags if tag.get("name")]
                return tag_names
            return []
        except requests.RequestException as e:
            print(f"获取企业标签失败 (enterpriseId={enterprise_id}): {str(e)}")
            return []
    
    @staticmethod
    @cache("tags:position:{positionId}", expire=config.CACHE_EXPIRE)
    def get_position_tags(position_id):
        """
        获取职位标签
        :param position_id: 职位ID
        :return: 职位标签列表
        """
        try:
            url = config.TAG_API_POSITION.format(positionId=position_id)
            response = requests.get(url, timeout=10)
            response.raise_for_status()
            
            result = response.json()
            if result.get("code") == 200:
                tags = result.get("data", [])
                # 提取标签名称列表
                tag_names = [tag.get("name") for tag in tags if tag.get("name")]
                return tag_names
            return []
        except requests.RequestException as e:
            print(f"获取职位标签失败 (positionId={position_id}): {str(e)}")
            return []
    
    @staticmethod
    def get_entity_tags(entity_type, entity_id):
        """
        获取实体标签（通用方法）
        :param entity_type: 实体类型 (enterprise, position)
        :param entity_id: 实体ID
        :return: 实体标签列表
        """
        if entity_type == "enterprise":
            return TagService.get_enterprise_tags(entity_id)
        elif entity_type == "position":
            return TagService.get_position_tags(entity_id)
        else:
            print(f"不支持的实体类型: {entity_type}")
            return []
    
    @staticmethod
    def refresh_tags(entity_type, entity_id):
        """
        刷新实体标签缓存
        :param entity_type: 实体类型
        :param entity_id: 实体ID
        """
        invalidate_tag_cache(entity_type, entity_id)
        return TagService.get_entity_tags(entity_type, entity_id)
    
    @staticmethod
    def batch_get_enterprise_tags(enterprise_ids):
        """
        批量获取企业标签
        :param enterprise_ids: 企业ID列表
        :return: 企业ID到标签列表的映射字典
        """
        result = {}
        for enterprise_id in enterprise_ids:
            result[enterprise_id] = TagService.get_enterprise_tags(enterprise_id)
        return result
    
    @staticmethod
    def batch_get_position_tags(position_ids):
        """
        批量获取职位标签
        :param position_ids: 职位ID列表
        :return: 职位ID到标签列表的映射字典
        """
        result = {}
        for position_id in position_ids:
            result[position_id] = TagService.get_position_tags(position_id)
        return result