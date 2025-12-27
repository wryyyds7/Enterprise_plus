# 职位数据服务

import requests
from config import get_config
from utils.cache import cache, invalidate_position_cache
from services.tag_service import TagService

# 获取配置
config = get_config()


class PositionService:
    """职位服务类"""
    
    @staticmethod
    @cache("position:{positionId}", expire=config.CACHE_EXPIRE)
    def get_position_by_id(position_id):
        """
        根据ID获取职位
        :param position_id: 职位ID
        :return: 职位信息
        """
        try:
            url = config.POSITION_API_BY_ID.format(positionId=position_id)
            response = requests.get(url, timeout=10)
            response.raise_for_status()
            
            result = response.json()
            if result.get("code") == 200:
                return result.get("data")
            return None
        except requests.RequestException as e:
            print(f"获取职位失败 (positionId={position_id}): {str(e)}")
            return None
    
    @staticmethod
    @cache("positions:enterprise:{enterpriseId}", expire=config.CACHE_EXPIRE)
    def get_positions_by_enterprise(enterprise_id):
        """
        获取企业的所有职位
        :param enterprise_id: 企业ID
        :return: 职位列表
        """
        try:
            url = config.POSITION_API_BY_ENTERPRISE.format(enterpriseId=enterprise_id)
            response = requests.get(url, timeout=10)
            response.raise_for_status()
            
            result = response.json()
            if result.get("code") == 200:
                return result.get("data", [])
            return []
        except requests.RequestException as e:
            print(f"获取企业职位失败 (enterpriseId={enterprise_id}): {str(e)}")
            return []
    
    @staticmethod
    def get_position_with_tags(position_id):
        """
        获取职位及其标签
        :param position_id: 职位ID
        :return: 包含标签的职位信息
        """
        position = PositionService.get_position_by_id(position_id)
        if not position:
            return None
        
        # 获取职位标签
        tags = TagService.get_position_tags(position_id)
        position["tags"] = tags
        
        return position
    
    @staticmethod
    def get_positions_with_tags(position_ids):
        """
        批量获取职位及其标签
        :param position_ids: 职位ID列表
        :return: 包含标签的职位信息列表
        """
        positions = []
        for position_id in position_ids:
            position = PositionService.get_position_with_tags(position_id)
            if position:
                positions.append(position)
        return positions
    
    @staticmethod
    def get_enterprise_positions_with_tags(enterprise_id):
        """
        获取企业的所有职位及其标签
        :param enterprise_id: 企业ID
        :return: 包含标签的职位信息列表
        """
        positions = PositionService.get_positions_by_enterprise(enterprise_id)
        if not positions:
            return []
        
        # 获取所有职位ID
        position_ids = [position.get("positionId") for position in positions if position.get("positionId")]
        
        # 获取职位及其标签
        positions_with_tags = PositionService.get_positions_with_tags(position_ids)
        
        return positions_with_tags
    
    @staticmethod
    def refresh_position(position_id):
        """
        刷新职位缓存
        :param position_id: 职位ID
        """
        invalidate_position_cache(position_id)
        return PositionService.get_position_by_id(position_id)
    
    @staticmethod
    def search_positions(keyword, page=1, page_size=10):
        """
        搜索职位
        :param keyword: 搜索关键词
        :param page: 页码
        :param page_size: 每页大小
        :return: 职位列表
        """
        try:
            params = {
                "keyword": keyword,
                "page": page,
                "pageSize": page_size
            }
            response = requests.get(config.POSITION_API, params=params, timeout=10)
            response.raise_for_status()
            
            result = response.json()
            if result.get("code") == 200:
                return result.get("data", {})
            return {}
        except requests.RequestException as e:
            print(f"搜索职位失败 (keyword={keyword}): {str(e)}")
            return {}