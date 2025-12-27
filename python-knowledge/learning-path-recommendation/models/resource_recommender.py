# 学习资源推荐模块

from config import get_config
from services.resource_service import resource_service
from services.user_service import user_service
from utils.cache import cache
from utils.similarity import calculate_jaccard_similarity, calculate_cosine_similarity
import numpy as np
from datetime import datetime


class ResourceRecommender:
    """学习资源推荐类，用于根据用户需求推荐合适的学习资源"""
    
    def __init__(self):
        """
        初始化资源推荐器
        """
        self.config = get_config()
    
    def recommend_resources(self, user_id, skill_id, limit=5):
        """
        根据用户和技能推荐学习资源
        :param user_id: 用户ID
        :param skill_id: 技能ID
        :param limit: 推荐资源数量
        :return: 推荐的学习资源列表
        """
        try:
            # 尝试从缓存获取推荐结果
            cache_key = f"resource_recommendation:{user_id}:{skill_id}"
            cached_recommendation = cache.get(cache_key)
            if cached_recommendation:
                return cached_recommendation
            
            # 获取用户信息
            user_info = user_service.get_user_skills(user_id)
            learning_style = user_info.get("learningStyle", "混合型") if user_info else "混合型"
            
            # 获取技能信息
            skill_info = resource_service.get_skill_info(skill_id)
            if not skill_info:
                return []
            
            # 获取所有相关资源
            all_resources = resource_service.get_relevant_resources(skill_id, limit=50)
            if not all_resources:
                return []
            
            # 计算资源评分
            scored_resources = []
            for resource in all_resources:
                score = self._calculate_resource_score(resource, skill_info, learning_style)
                scored_resources.append((resource, score))
            
            # 按评分排序
            scored_resources.sort(key=lambda x: x[1], reverse=True)
            
            # 平衡资源类型
            balanced_resources = self._balance_resource_types([res for res, _ in scored_resources], limit)
            
            # 缓存推荐结果
            cache.set(cache_key, balanced_resources)
            
            return balanced_resources
            
        except Exception as e:
            print(f"推荐学习资源失败: {str(e)}")
            return []
    
    def _calculate_resource_score(self, resource, skill_info, learning_style):
        """
        计算资源的推荐评分
        :param resource: 学习资源
        :param skill_info: 技能信息
        :param learning_style: 学习风格
        :return: 资源评分（0-1之间）
        """
        score = 0.0
        
        # 1. 学习风格匹配度 (权重：0.3)
        style_score = self._calculate_style_match(resource, learning_style)
        score += style_score * 0.3
        
        # 2. 技能相关性 (权重：0.25)
        relevance_score = resource.get("relevance", 0.5)
        score += relevance_score * 0.25
        
        # 3. 资源类型多样性 (权重：0.15)
        type_score = self._calculate_type_diversity_score(resource)
        score += type_score * 0.15
        
        # 4. 更新时间 (权重：0.1)
        update_score = self._calculate_update_score(resource)
        score += update_score * 0.1
        
        # 5. 用户评价 (权重：0.1)
        rating_score = resource.get("rating", 3) / 5.0  # 转换为0-1范围
        score += rating_score * 0.1
        
        # 6. 学习时长合理性 (权重：0.1)
        duration_score = self._calculate_duration_score(resource, skill_info)
        score += duration_score * 0.1
        
        return round(score, 3)
    
    def _calculate_style_match(self, resource, learning_style):
        """
        计算资源与学习风格的匹配度
        :param resource: 学习资源
        :param learning_style: 学习风格
        :return: 匹配度得分（0-1之间）
        """
        resource_type = resource.get("type", "")
        
        # 学习风格与资源类型的匹配规则
        style_mapping = {
            "理论型": ["文档", "教程", "课程"],
            "实践型": ["项目", "案例", "实战", "练习"],
            "混合型": ["视频", "综合", "全栈"]
        }
        
        # 如果学习风格不在映射中，返回默认值
        if learning_style not in style_mapping:
            return 0.5
        
        # 检查资源类型是否匹配学习风格
        for preferred_type in style_mapping[learning_style]:
            if preferred_type in resource_type:
                return 1.0
        
        # 如果不匹配，返回较低的分数
        return 0.3
    
    def _calculate_type_diversity_score(self, resource):
        """
        计算资源类型多样性得分
        :param resource: 学习资源
        :return: 多样性得分（0-1之间）
        """
        resource_type = resource.get("type", "")
        
        # 优先推荐多样化的资源类型
        diverse_types = ["视频", "文档", "项目", "课程", "实战"]
        
        for dtype in diverse_types:
            if dtype in resource_type:
                return 1.0
        
        return 0.5
    
    def _calculate_update_score(self, resource):
        """
        计算资源更新时间得分
        :param resource: 学习资源
        :return: 更新时间得分（0-1之间）
        """
        try:
            update_date_str = resource.get("updateTime", "")
            if not update_date_str:
                return 0.5
            
            # 解析更新时间
            update_date = datetime.strptime(update_date_str, "%Y-%m-%d")
            
            # 计算距今的天数
            days_since_update = (datetime.now() - update_date).days
            
            # 最近6个月内更新的资源得分最高
            if days_since_update <= 180:
                return 1.0
            # 6-12个月内更新的资源
            elif days_since_update <= 365:
                return 0.8
            # 1-2年内更新的资源
            elif days_since_update <= 730:
                return 0.6
            # 超过2年的资源
            else:
                return 0.3
                
        except Exception as e:
            print(f"计算更新时间得分失败: {str(e)}")
            return 0.5
    
    def _calculate_duration_score(self, resource, skill_info):
        """
        计算学习时长合理性得分
        :param resource: 学习资源
        :param skill_info: 技能信息
        :return: 时长得分（0-1之间）
        """
        try:
            duration = resource.get("duration", 0)
            if not duration:
                return 0.5
            
            # 根据技能难度调整期望时长
            difficulty = skill_info.get("difficulty", "中级")
            
            difficulty_duration_mapping = {
                "初级": 10,   # 小时
                "中级": 20,
                "高级": 40,
                "专家级": 80
            }
            
            expected_duration = difficulty_duration_mapping.get(difficulty, 20)
            
            # 计算时长与期望时长的偏差
            # 偏差在20%以内为最佳
            duration_ratio = duration / expected_duration
            if 0.8 <= duration_ratio <= 1.2:
                return 1.0
            elif 0.5 <= duration_ratio < 0.8 or 1.2 < duration_ratio <= 1.5:
                return 0.8
            elif 0.3 <= duration_ratio < 0.5 or 1.5 < duration_ratio <= 2.0:
                return 0.6
            else:
                return 0.3
                
        except Exception as e:
            print(f"计算时长得分失败: {str(e)}")
            return 0.5
    
    def _balance_resource_types(self, resources, limit):
        """
        平衡不同类型的资源
        :param resources: 排序后的资源列表
        :param limit: 需要的资源数量
        :return: 平衡后的资源列表
        """
        if not resources or limit <= 0:
            return []
        
        # 资源类型分组
        type_groups = {}
        for resource in resources:
            res_type = resource.get("type", "其他")
            if res_type not in type_groups:
                type_groups[res_type] = []
            type_groups[res_type].append(resource)
        
        # 确定每种类型的资源数量
        num_types = len(type_groups)
        if num_types == 0:
            return []
        
        # 计算每种类型的基础数量和剩余数量
        base_count = limit // num_types
        remainder = limit % num_types
        
        # 选择数量较多的类型组
        sorted_types = sorted(type_groups.items(), key=lambda x: len(x[1]), reverse=True)
        
        balanced = []
        
        # 分配资源
        for i, (res_type, res_list) in enumerate(sorted_types):
            # 计算该类型应分配的数量
            count = base_count
            if i < remainder:
                count += 1
            
            # 添加该类型的资源
            balanced.extend(res_list[:count])
            
            # 如果已经达到限制，提前结束
            if len(balanced) >= limit:
                break
        
        # 如果资源不足，补充其他资源
        if len(balanced) < limit:
            remaining = limit - len(balanced)
            for resource in resources:
                if resource not in balanced:
                    balanced.append(resource)
                    remaining -= 1
                    if remaining <= 0:
                        break
        
        return balanced[:limit]
    
    def recommend_for_path(self, user_id, learning_path, max_resources_per_skill=3):
        """
        为学习路径中的每个技能推荐资源
        :param user_id: 用户ID
        :param learning_path: 学习路径
        :param max_resources_per_skill: 每个技能推荐的最大资源数
        :return: 包含推荐资源的学习路径
        """
        try:
            path_with_resources = []
            
            for skill in learning_path:
                skill_id = skill["skillId"]
                
                # 推荐资源
                recommended_resources = self.recommend_resources(user_id, skill_id, limit=max_resources_per_skill)
                
                # 添加资源到技能信息
                skill_with_resources = skill.copy()
                skill_with_resources["resources"] = recommended_resources
                
                path_with_resources.append(skill_with_resources)
            
            return path_with_resources
            
        except Exception as e:
            print(f"为学习路径推荐资源失败: {str(e)}")
            return learning_path
    
    def filter_by_preference(self, resources, preference):
        """
        根据用户偏好过滤资源
        :param resources: 资源列表
        :param preference: 用户偏好（如："视频", "实战项目"）
        :return: 过滤后的资源列表
        """
        if not resources or not preference:
            return resources
        
        filtered = []
        for resource in resources:
            res_type = resource.get("type", "")
            if preference in res_type:
                filtered.append(resource)
        
        return filtered
    
    def get_resource_similarity(self, resource1, resource2):
        """
        计算两个资源的相似度
        :param resource1: 第一个资源
        :param resource2: 第二个资源
        :return: 相似度得分（0-1之间）
        """
        similarity = 0.0
        
        # 类型相似度
        if resource1.get("type") == resource2.get("type"):
            similarity += 0.3
        
        # 难度相似度
        if resource1.get("difficulty") == resource2.get("difficulty"):
            similarity += 0.3
        
        # 标签相似度 - 使用全局词典的余弦相似度
        tags1 = resource1.get("tags", [])
        tags2 = resource2.get("tags", [])
        if tags1 and tags2:
            tag_similarity = calculate_cosine_similarity(tags1, tags2)
            similarity += tag_similarity * 0.4
        
        return round(similarity, 3)


# 创建全局资源推荐器实例
resource_recommender = ResourceRecommender()