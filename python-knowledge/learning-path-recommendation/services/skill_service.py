# 技能服务模块

import requests
from config import get_config
from utils.cache import cache


class SkillService:
    """技能服务类"""
    
    def __init__(self):
        """
        初始化技能服务
        """
        self.config = get_config()
        self.session = requests.Session()
        self.session.headers.update({
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        })
    
    def get_all_skills(self):
        """
        获取所有技能列表
        :return: 技能列表，如果失败返回None
        """
        # 尝试从缓存获取
        cache_key = "all_skills"
        cached_data = cache.get(cache_key)
        if cached_data:
            return cached_data
        
        try:
            response = self.session.get(self.config.SKILL_LIST_API)
            response.raise_for_status()
            skills = response.json().get('data', [])
            
            # 缓存结果
            cache.set(cache_key, skills)
            
            return skills
        except Exception as e:
            print(f"获取技能列表失败: {str(e)}")
            return None
    
    def get_skill_by_id(self, skill_id):
        """
        根据ID获取技能详情
        :param skill_id: 技能ID
        :return: 技能详情，如果失败返回None
        """
        # 尝试从缓存获取
        cache_key = f"skill:{skill_id}"
        cached_data = cache.get(cache_key)
        if cached_data:
            return cached_data
        
        try:
            url = self.config.SKILL_BY_ID_API.format(skillId=skill_id)
            response = self.session.get(url)
            response.raise_for_status()
            skill = response.json().get('data')
            
            # 缓存结果
            cache.set(cache_key, skill)
            
            return skill
        except Exception as e:
            print(f"获取技能详情失败: {str(e)}")
            return None
    
    def get_skill_tags(self, skill_id):
        """
        获取技能的标签
        :param skill_id: 技能ID
        :return: 标签列表，如果失败返回None
        """
        # 尝试从缓存获取
        cache_key = f"skill_tags:{skill_id}"
        cached_data = cache.get(cache_key)
        if cached_data:
            return cached_data
        
        try:
            url = self.config.TAG_BY_ENTITY_API.format(
                entityType="skill",
                entityId=skill_id
            )
            response = self.session.get(url)
            response.raise_for_status()
            tags = response.json().get('data', [])
            
            # 缓存结果
            cache.set(cache_key, tags)
            
            return tags
        except Exception as e:
            print(f"获取技能标签失败: {str(e)}")
            return None
    
    def get_skill_dependencies(self, skill_id):
        """
        获取技能的前置依赖
        :param skill_id: 技能ID
        :return: 依赖技能列表，如果失败返回None
        """
        # 尝试从缓存获取
        cache_key = f"skill_dependencies:{skill_id}"
        cached_data = cache.get(cache_key)
        if cached_data:
            return cached_data
        
        try:
            skill = self.get_skill_by_id(skill_id)
            if skill and 'dependencies' in skill:
                dependencies = skill['dependencies']
                
                # 缓存结果
                cache.set(cache_key, dependencies)
                
                return dependencies
            return []
        except Exception as e:
            print(f"获取技能依赖失败: {str(e)}")
            return None
    
    def search_skills(self, keyword):
        """
        搜索技能
        :param keyword: 搜索关键词
        :return: 技能列表，如果失败返回None
        """
        # 尝试从缓存获取
        cache_key = f"skill_search:{keyword}"
        cached_data = cache.get(cache_key)
        if cached_data:
            return cached_data
        
        try:
            response = self.session.get(
                self.config.SKILL_LIST_API,
                params={'keyword': keyword}
            )
            response.raise_for_status()
            skills = response.json().get('data', [])
            
            # 缓存结果
            cache.set(cache_key, skills)
            
            return skills
        except Exception as e:
            print(f"搜索技能失败: {str(e)}")
            return None
    
    def get_skills_by_category(self, category):
        """
        根据分类获取技能
        :param category: 技能分类
        :return: 技能列表，如果失败返回None
        """
        # 尝试从缓存获取
        cache_key = f"skills_by_category:{category}"
        cached_data = cache.get(cache_key)
        if cached_data:
            return cached_data
        
        try:
            response = self.session.get(
                self.config.SKILL_LIST_API,
                params={'category': category}
            )
            response.raise_for_status()
            skills = response.json().get('data', [])
            
            # 缓存结果
            cache.set(cache_key, skills)
            
            return skills
        except Exception as e:
            print(f"根据分类获取技能失败: {str(e)}")
            return None
    
    def get_skill_difficulty(self, skill_id):
        """
        获取技能难度
        :param skill_id: 技能ID
        :return: 难度值，如果失败返回None
        """
        skill = self.get_skill_by_id(skill_id)
        if skill and 'difficulty' in skill:
            return skill['difficulty']
        return None
    
    def refresh_skill_cache(self, skill_id):
        """
        刷新技能相关缓存
        :param skill_id: 技能ID
        :return: True表示成功，False表示失败
        """
        try:
            # 删除相关缓存
            cache.delete(f"skill:{skill_id}")
            cache.delete(f"skill_tags:{skill_id}")
            cache.delete(f"skill_dependencies:{skill_id}")
            cache.delete("all_skills")
            return True
        except Exception as e:
            print(f"刷新技能缓存失败: {str(e)}")
            return False


# 创建全局技能服务实例
skill_service = SkillService()