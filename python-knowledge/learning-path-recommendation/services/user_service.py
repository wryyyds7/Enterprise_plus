  # 用户服务模块

import requests
from config import get_config
from utils.cache import cache


class UserService:
    """用户服务类"""
    
    def __init__(self):
        """
        初始化用户服务  
        """
        self.config = get_config()
        self.session = requests.Session()
        self.session.headers.update({
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        })
    
    def get_user_skills(self, user_id):
        """
        获取用户的技能信息
        :param user_id: 用户ID
        :return: 用户技能信息字典，如果失败返回None
        """
        # 尝试从缓存获取
        cache_key = cache.get_user_skills_cache_key(user_id)
        cached_data = cache.get(cache_key)
        if cached_data:
            return cached_data
        
        try:
            url = self.config.USER_SKILLS_API.format(userId=user_id)
            response = self.session.get(url)
            response.raise_for_status()
            user_skills = response.json().get('data', {})
            
            # 缓存结果
            cache.set(cache_key, user_skills)
            
            return user_skills
        except Exception as e:
            print(f"获取用户技能信息失败: {str(e)}")
            return None
    
    def get_user_skill_levels(self, user_id):
        """
        获取用户的技能掌握程度
        :param user_id: 用户ID
        :return: 技能掌握程度字典（如：{'Java基础': 3, 'SQL入门': 2}），如果失败返回None
        """
        try:
            user_skills = self.get_user_skills(user_id)
            if user_skills and 'skillLevels' in user_skills:
                return user_skills['skillLevels']
            return {}
        except Exception as e:
            print(f"获取用户技能掌握程度失败: {str(e)}")
            return None
    
    def get_user_current_skills(self, user_id):
        """
        获取用户当前掌握的技能列表
        :param user_id: 用户ID
        :return: 技能列表，如果失败返回None
        """
        try:
            user_skills = self.get_user_skills(user_id)
            if user_skills and 'currentSkills' in user_skills:
                return user_skills['currentSkills']
            return []
        except Exception as e:
            print(f"获取用户当前技能列表失败: {str(e)}")
            return None
    
    def get_user_learning_style(self, user_id):
        """
        获取用户的学习风格
        :param user_id: 用户ID
        :return: 学习风格字符串（如："理论+实践"），如果失败返回默认值
        """
        try:
            user_skills = self.get_user_skills(user_id)
            if user_skills and 'learningStyle' in user_skills:
                return user_skills['learningStyle']
            return "混合型"
        except Exception as e:
            print(f"获取用户学习风格失败: {str(e)}")
            return "混合型"
    
    def update_user_skills(self, user_id, skills_data):
        """
        更新用户的技能信息
        :param user_id: 用户ID
        :param skills_data: 技能数据字典（如：{'currentSkills': ['Java基础'], 'skillLevels': {'Java基础': 3}}）
        :return: 更新结果，如果失败返回None
        """
        try:
            url = self.config.USER_SKILLS_API.format(userId=user_id)
            response = self.session.put(url, json=skills_data)
            response.raise_for_status()
            
            # 清除缓存
            cache_key = cache.get_user_skills_cache_key(user_id)
            cache.delete(cache_key)
            
            return response.json().get('data')
        except Exception as e:
            print(f"更新用户技能信息失败: {str(e)}")
            return None
    
    def add_user_skill(self, user_id, skill_name, level=1):
        """
        为用户添加技能
        :param user_id: 用户ID
        :param skill_name: 技能名称
        :param level: 技能掌握程度
        :return: 更新结果，如果失败返回None
        """
        try:
            # 获取当前技能信息
            user_skills = self.get_user_skills(user_id) or {
                'currentSkills': [],
                'skillLevels': {}
            }
            
            # 更新技能信息
            if skill_name not in user_skills['currentSkills']:
                user_skills['currentSkills'].append(skill_name)
            user_skills['skillLevels'][skill_name] = level
            
            # 更新到后端
            return self.update_user_skills(user_id, user_skills)
        except Exception as e:
            print(f"添加用户技能失败: {str(e)}")
            return None
    
    def update_skill_level(self, user_id, skill_name, level):
        """
        更新用户的技能掌握程度
        :param user_id: 用户ID
        :param skill_name: 技能名称
        :param level: 新的技能掌握程度
        :return: 更新结果，如果失败返回None
        """
        try:
            # 获取当前技能信息
            user_skills = self.get_user_skills(user_id)
            if not user_skills:
                return None
            
            # 更新技能掌握程度
            if 'skillLevels' in user_skills:
                user_skills['skillLevels'][skill_name] = level
            else:
                user_skills['skillLevels'] = {skill_name: level}
            
            # 确保技能在当前技能列表中
            if 'currentSkills' in user_skills and skill_name not in user_skills['currentSkills']:
                user_skills['currentSkills'].append(skill_name)
            
            # 更新到后端
            return self.update_user_skills(user_id, user_skills)
        except Exception as e:
            print(f"更新技能掌握程度失败: {str(e)}")
            return None
    
    def get_user_skill_proficiency(self, user_id, skill_name):
        """
        获取用户对特定技能的掌握程度
        :param user_id: 用户ID
        :param skill_name: 技能名称
        :return: 技能掌握程度（1-5），如果失败返回0
        """
        try:
            skill_levels = self.get_user_skill_levels(user_id)
            if skill_levels and skill_name in skill_levels:
                return skill_levels[skill_name]
            return 0
        except Exception as e:
            print(f"获取用户特定技能掌握程度失败: {str(e)}")
            return 0
    
    def refresh_user_skills_cache(self, user_id):
        """
        刷新用户技能相关缓存
        :param user_id: 用户ID
        :return: True表示成功，False表示失败
        """
        try:
            cache_key = cache.get_user_skills_cache_key(user_id)
            cache.delete(cache_key)
            return True
        except Exception as e:
            print(f"刷新用户技能缓存失败: {str(e)}")
            return False


# 创建全局用户服务实例
user_service = UserService()