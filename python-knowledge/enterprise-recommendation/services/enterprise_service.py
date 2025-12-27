# 企业数据服务

import requests
from config import get_config
from utils.cache import cache, invalidate_enterprise_cache
from services.tag_service import TagService

# 获取配置
config = get_config()


class EnterpriseService:
    """企业服务类"""
    
    @staticmethod
    @cache("enterprise:all", expire=config.CACHE_EXPIRE)
    def get_all_enterprises():
        """
        获取所有企业
        :return: 企业列表
        """
        try:
            response = requests.get(config.ENTERPRISE_API, timeout=10)
            response.raise_for_status()
            
            result = response.json()
            if result.get("code") == 200:
                return result.get("data", [])
            return []
        except requests.RequestException as e:
            print(f"获取所有企业失败: {str(e)}")
            return []
    
    @staticmethod
    @cache("enterprise:{enterpriseId}", expire=config.CACHE_EXPIRE)
    def get_enterprise_by_id(enterprise_id):
        """
        根据ID获取企业
        :param enterprise_id: 企业ID
        :return: 企业信息
        """
        try:
            url = config.ENTERPRISE_API_BY_ID.format(enterpriseId=enterprise_id)
            response = requests.get(url, timeout=10)
            response.raise_for_status()
            
            result = response.json()
            if result.get("code") == 200:
                return result.get("data")
            return None
        except requests.RequestException as e:
            print(f"获取企业失败 (enterpriseId={enterprise_id}): {str(e)}")
            return None
    
    @staticmethod
    def get_enterprise_with_tags(enterprise_id):
        """
        获取企业及其标签
        :param enterprise_id: 企业ID
        :return: 包含标签的企业信息
        """
        enterprise = EnterpriseService.get_enterprise_by_id(enterprise_id)
        if not enterprise:
            return None
        
        # 获取企业标签
        tags = TagService.get_enterprise_tags(enterprise_id)
        enterprise["tags"] = tags
        
        return enterprise
    
    @staticmethod
    def get_enterprises_with_tags(enterprise_ids):
        """
        批量获取企业及其标签
        :param enterprise_ids: 企业ID列表
        :return: 包含标签的企业信息列表
        """
        enterprises = []
        for enterprise_id in enterprise_ids:
            enterprise = EnterpriseService.get_enterprise_with_tags(enterprise_id)
            if enterprise:
                enterprises.append(enterprise)
        return enterprises
    
    @staticmethod
    @cache("enterprises:top:{top_n}", expire=config.CACHE_EXPIRE)
    def get_top_enterprises(top_n=10):
        """
        获取热门企业
        :param top_n: 企业数量
        :return: 企业列表
        """
        enterprises = EnterpriseService.get_all_enterprises()
        # 按规模或其他指标排序（这里简化处理）
        sorted_enterprises = sorted(enterprises, key=lambda x: x.get("size", 0), reverse=True)
        return sorted_enterprises[:top_n]
    
    @staticmethod
    def refresh_enterprise(enterprise_id):
        """
        刷新企业缓存
        :param enterprise_id: 企业ID
        """
        invalidate_enterprise_cache(enterprise_id)
        return EnterpriseService.get_enterprise_by_id(enterprise_id)
    
    @staticmethod
    def search_enterprises(keyword, page=1, page_size=10):
        """
        搜索企业
        :param keyword: 搜索关键词
        :param page: 页码
        :param page_size: 每页大小
        :return: 企业列表
        """
        try:
            params = {
                "keyword": keyword,
                "page": page,
                "pageSize": page_size
            }
            response = requests.get(config.ENTERPRISE_API, params=params, timeout=10)
            response.raise_for_status()
            
            result = response.json()
            if result.get("code") == 200:
                return result.get("data", {})
            return {}
        except requests.RequestException as e:
            print(f"搜索企业失败 (keyword={keyword}): {str(e)}")
            return {}