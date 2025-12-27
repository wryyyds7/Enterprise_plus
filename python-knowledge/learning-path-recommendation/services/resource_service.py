# 学习资源服务模块

import requests
from config import get_config
from utils.cache import cache


class ResourceService:
    """学习资源服务类"""
    
    def __init__(self):
        """
        初始化学习资源服务
        """
        self.config = get_config()
        self.session = requests.Session()
        self.session.headers.update({
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        })
    
    def get_all_resources(self):
        """
        获取所有学习资源
        :return: 学习资源列表，如果失败返回None
        """
        # 尝试从缓存获取
        cache_key = "all_resources"
        cached_data = cache.get(cache_key)
        if cached_data:
            return cached_data
        
        try:
            response = self.session.get(self.config.RESOURCE_LIST_API)
            response.raise_for_status()
            resources = response.json().get('data', [])
            
            # 缓存结果
            cache.set(cache_key, resources)
            
            return resources
        except Exception as e:
            print(f"获取所有学习资源失败: {str(e)}")
            return None
    
    def get_resource_by_id(self, resource_id):
        """
        根据ID获取学习资源详情
        :param resource_id: 资源ID
        :return: 资源详情，如果失败返回None
        """
        # 尝试从缓存获取
        cache_key = f"resource:{resource_id}"
        cached_data = cache.get(cache_key)
        if cached_data:
            return cached_data
        
        try:
            url = self.config.RESOURCE_BY_ID_API.format(resourceId=resource_id)
            response = self.session.get(url)
            response.raise_for_status()
            resource = response.json().get('data')
            
            # 缓存结果
            cache.set(cache_key, resource)
            
            return resource
        except Exception as e:
            print(f"获取学习资源详情失败: {str(e)}")
            return None
    
    def get_resources_by_skill(self, skill_id):
        """
        获取与技能相关的学习资源
        :param skill_id: 技能ID
        :return: 资源列表，如果失败返回None
        """
        # 尝试从缓存获取
        cache_key = f"resources_by_skill:{skill_id}"
        cached_data = cache.get(cache_key)
        if cached_data:
            return cached_data
        
        try:
            url = self.config.RESOURCE_BY_SKILL_API.format(skillId=skill_id)
            response = self.session.get(url)
            response.raise_for_status()
            resources = response.json().get('data', [])
            
            # 缓存结果
            cache.set(cache_key, resources)
            
            return resources
        except Exception as e:
            print(f"获取技能相关资源失败: {str(e)}")
            return None
    
    def get_resources_by_type(self, resource_type):
        """
        根据类型获取学习资源
        :param resource_type: 资源类型（如：视频教程、在线课程、文档等）
        :return: 资源列表，如果失败返回None
        """
        # 尝试从缓存获取
        cache_key = f"resources_by_type:{resource_type}"
        cached_data = cache.get(cache_key)
        if cached_data:
            return cached_data
        
        try:
            response = self.session.get(
                self.config.RESOURCE_LIST_API,
                params={'type': resource_type}
            )
            response.raise_for_status()
            resources = response.json().get('data', [])
            
            # 缓存结果
            cache.set(cache_key, resources)
            
            return resources
        except Exception as e:
            print(f"根据类型获取资源失败: {str(e)}")
            return None
    
    def search_resources(self, keyword):
        """
        搜索学习资源
        :param keyword: 搜索关键词
        :return: 资源列表，如果失败返回None
        """
        # 尝试从缓存获取
        cache_key = f"resource_search:{keyword}"
        cached_data = cache.get(cache_key)
        if cached_data:
            return cached_data
        
        try:
            response = self.session.get(
                self.config.RESOURCE_LIST_API,
                params={'keyword': keyword}
            )
            response.raise_for_status()
            resources = response.json().get('data', [])
            
            # 缓存结果
            cache.set(cache_key, resources)
            
            return resources
        except Exception as e:
            print(f"搜索学习资源失败: {str(e)}")
            return None
    
    def filter_resources(self, filters):
        """
        过滤学习资源
        :param filters: 过滤条件字典（如：{'difficulty': '中级', 'duration': 10}）
        :return: 资源列表，如果失败返回None
        """
        try:
            response = self.session.get(
                self.config.RESOURCE_LIST_API,
                params=filters
            )
            response.raise_for_status()
            return response.json().get('data', [])
        except Exception as e:
            print(f"过滤学习资源失败: {str(e)}")
            return None
    
    def get_relevant_resources(self, skill_id, learning_style=None, limit=None):
        """
        获取与技能相关的推荐资源
        :param skill_id: 技能ID
        :param learning_style: 学习风格（如：理论型、实践型、混合型）
        :param limit: 限制返回数量
        :return: 资源列表，如果失败返回None
        """
        try:
            resources = self.get_resources_by_skill(skill_id)
            if not resources:
                return []
            
            # 根据学习风格过滤和排序资源
            if learning_style:
                filtered = []
                for resource in resources:
                    resource_style = resource.get('style', '混合型')
                    if resource_style == learning_style or resource_style == '混合型':
                        filtered.append(resource)
                resources = filtered
            
            # 按相关性排序
            resources.sort(key=lambda x: x.get('relevance', 0), reverse=True)
            
            # 限制返回数量
            if limit:
                resources = resources[:limit]
            
            return resources
        except Exception as e:
            print(f"获取推荐资源失败: {str(e)}")
            return None
    
    def refresh_resource_cache(self, resource_id):
        """
        刷新资源相关缓存
        :param resource_id: 资源ID
        :return: True表示成功，False表示失败
        """
        try:
            # 删除相关缓存
            cache.delete(f"resource:{resource_id}")
            cache.delete("all_resources")
            
            # 删除相关技能资源缓存
            skill_id = self.get_resource_by_id(resource_id).get('skillId')
            if skill_id:
                cache.delete(f"resources_by_skill:{skill_id}")
            
            return True
        except Exception as e:
            print(f"刷新资源缓存失败: {str(e)}")
            return False


# 创建全局学习资源服务实例
resource_service = ResourceService()