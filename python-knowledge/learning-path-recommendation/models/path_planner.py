# 学习路径规划模块

from config import get_config
from utils.graph_algorithms import dijkstra, a_star, find_all_paths, calculate_path_cost, filter_paths_by_cost
from .graph_builder import graph_builder
from services.skill_service import skill_service
from services.user_service import user_service
from utils.cache import cache


class PathPlanner:
    """学习路径规划类，用于根据用户当前技能和学习目标规划最佳学习路径"""
    
    def __init__(self):
        """
        初始化路径规划器
        """
        self.config = get_config()
        self.algorithm = self.config.PATH_PLANNING_ALGORITHM
    
    def plan_learning_path(self, user_id, target_skills, timeframe=None, learning_style=None):
        """
        规划学习路径
        :param user_id: 用户ID
        :param target_skills: 目标技能列表
        :param timeframe: 时间限制（如："3个月"）
        :param learning_style: 学习风格（如："理论+实践"）
        :return: 学习路径列表
        """
        print(f"开始为用户 {user_id} 规划学习路径...")
        
        # 尝试从缓存获取
        cache_key = f"learning_path:{user_id}:{'-'.join(target_skills)}"
        cached_path = cache.get(cache_key)
        if cached_path:
            print("从缓存加载学习路径")
            return cached_path
        
        try:
            # 获取用户当前技能和掌握程度
            user_skills = user_service.get_user_skills(user_id)
            if not user_skills:
                return []
            
            current_skills = user_skills.get('currentSkills', [])
            skill_levels = user_skills.get('skillLevels', {})
            
            # 获取目标技能的ID
            target_skill_ids = []
            for skill_name in target_skills:
                # 根据技能名称查找技能ID
                skills = skill_service.search_skills(skill_name)
                if skills:
                    target_skill_ids.append(skills[0]['skillId'])
            
            if not target_skill_ids:
                print("没有找到目标技能的ID")
                return []
            
            # 获取当前技能的ID
            current_skill_ids = []
            for skill_name in current_skills:
                skills = skill_service.search_skills(skill_name)
                if skills:
                    current_skill_ids.append(skills[0]['skillId'])
            
            # 构建技能知识图谱
            skill_graph = graph_builder.build_skill_graph()
            if not skill_graph:
                return []
            
            # 规划路径
            learning_paths = []
            
            for target_skill_id in target_skill_ids:
                # 如果用户已经掌握该技能，跳过
                if target_skill_id in current_skill_ids:
                    continue
                
                # 找到所有从当前技能到目标技能的路径
                all_paths = []
                for current_skill_id in current_skill_ids:
                    if current_skill_id not in skill_graph.nodes:
                        continue
                    
                    # 使用配置的算法查找路径
                    if self.algorithm == 'dijkstra':
                        path, cost = dijkstra(skill_graph, current_skill_id, target_skill_id)
                        if path and len(path) > 1:
                            all_paths.append((path, cost))
                    elif self.algorithm == 'a_star':
                        path, cost = a_star(skill_graph, current_skill_id, target_skill_id)
                        if path and len(path) > 1:
                            all_paths.append((path, cost))
                    else:
                        # 使用默认的dijkstra算法
                        path, cost = dijkstra(skill_graph, current_skill_id, target_skill_id)
                        if path and len(path) > 1:
                            all_paths.append((path, cost))
                
                # 如果没有找到路径，尝试从所有技能节点开始查找
                if not all_paths:
                    for node_id in skill_graph.nodes:
                        if node_id != target_skill_id:
                            path, cost = dijkstra(skill_graph, node_id, target_skill_id)
                            if path and len(path) > 1:
                                all_paths.append((path, cost))
                
                # 按成本排序路径
                all_paths.sort(key=lambda x: x[1])
                
                # 取前N个最佳路径
                best_paths = all_paths[:self.config.MAX_PATHS_TO_CONSIDER]
                
                # 转换为包含技能信息的路径
                for path, cost in best_paths:
                    path_info = []
                    for skill_id in path:
                        node = skill_graph.nodes[skill_id]
                        path_info.append({
                            'skillId': skill_id,
                            'name': node.data['name'],
                            'difficulty': node.data['difficulty'],
                            'category': node.data['category']
                        })
                    
                    learning_paths.append({
                        'path': path_info,
                        'cost': cost,
                        'targetSkill': target_skill_id
                    })
            
            # 合并和优化路径
            optimized_paths = self._optimize_paths(learning_paths)
            
            # 根据时间限制过滤路径
            if timeframe:
                optimized_paths = self._filter_by_timeframe(optimized_paths, timeframe)
            
            print(f"成功规划 {len(optimized_paths)} 条学习路径")
            
            # 缓存学习路径
            cache.set(cache_key, optimized_paths)
            
            return optimized_paths
        except Exception as e:
            print(f"规划学习路径失败: {str(e)}")
            return []
    
    def _optimize_paths(self, paths):
        """
        优化学习路径
        :param paths: 原始路径列表
        :return: 优化后的路径列表
        """
        if not paths:
            return []
        
        # 按成本排序
        paths.sort(key=lambda x: x['cost'])
        
        # 合并具有相同前缀的路径
        merged_paths = []
        seen_prefixes = set()
        
        for path in paths:
            path_skills = [p['skillId'] for p in path['path']]
            prefix = tuple(path_skills[:-1])  # 除了最后一个技能的前缀
            
            if prefix not in seen_prefixes:
                merged_paths.append(path)
                seen_prefixes.add(prefix)
        
        # 限制路径数量
        return merged_paths[:self.config.DEFAULT_LEARNING_PATH_LENGTH]
    
    def _filter_by_timeframe(self, paths, timeframe):
        """
        根据时间限制过滤路径
        :param paths: 路径列表
        :param timeframe: 时间限制（如："3个月"）
        :return: 过滤后的路径列表
        """
        if not paths or not timeframe:
            return paths
        
        # 简单的时间映射（实际应用中应该更复杂）
        time_mapping = {
            '1个月': 30,
            '2个月': 60,
            '3个月': 90,
            '6个月': 180,
            '1年': 365
        }
        
        max_days = time_mapping.get(timeframe, 90)  # 默认3个月
        
        # 计算每条路径的预计学习时间（这里简化为技能数量 * 平均学习时间）
        avg_days_per_skill = 10  # 每个技能平均学习10天
        
        filtered = []
        for path in paths:
            estimated_days = len(path['path']) * avg_days_per_skill
            if estimated_days <= max_days:
                path['estimatedTime'] = f"约 {estimated_days} 天"
                filtered.append(path)
        
        return filtered
    
    def get_learning_path_details(self, path, user_id):
        """
        获取学习路径的详细信息，包括推荐的学习资源
        :param path: 学习路径
        :param user_id: 用户ID
        :return: 包含详细信息的学习路径
        """
        from services.resource_service import resource_service
        
        try:
            path_details = []
            user_skills = user_service.get_user_skills(user_id)
            learning_style = user_skills.get('learningStyle', '混合型') if user_skills else '混合型'
            
            for skill in path['path']:
                skill_id = skill['skillId']
                
                # 获取推荐的学习资源
                resources = resource_service.get_relevant_resources(
                    skill_id,
                    learning_style=learning_style,
                    limit=self.config.MAX_RESOURCES_PER_SKILL
                )
                
                skill_details = skill.copy()
                skill_details['resources'] = resources
                skill_details['prerequisites'] = self._get_prerequisites(skill_id)
                
                path_details.append(skill_details)
            
            return {
                'path': path_details,
                'cost': path['cost'],
                'targetSkill': path['targetSkill'],
                'estimatedTime': path.get('estimatedTime', '未知')
            }
        except Exception as e:
            print(f"获取学习路径详情失败: {str(e)}")
            return None
    
    def _get_prerequisites(self, skill_id):
        """
        获取技能的前置依赖
        :param skill_id: 技能ID
        :return: 前置依赖列表
        """
        try:
            dependencies = skill_service.get_skill_dependencies(skill_id)
            if dependencies:
                return [dep['name'] for dep in dependencies]
            return []
        except Exception as e:
            print(f"获取技能前置依赖失败: {str(e)}")
            return []
    
    def calculate_learning_progress(self, user_id, target_skills):
        """
        计算用户的学习进度
        :param user_id: 用户ID
        :param target_skills: 目标技能列表
        :return: 学习进度信息
        """
        try:
            # 获取用户当前技能
            user_skills = user_service.get_user_skills(user_id)
            if not user_skills:
                return {'progress': 0, 'completedSkills': [], 'remainingSkills': target_skills}
            
            current_skills = user_skills.get('currentSkills', [])
            
            # 计算已完成的目标技能数量
            completed = 0
            completed_skills = []
            remaining_skills = []
            
            for skill_name in target_skills:
                if skill_name in current_skills:
                    completed += 1
                    completed_skills.append(skill_name)
                else:
                    remaining_skills.append(skill_name)
            
            # 计算进度百分比
            progress = (completed / len(target_skills)) * 100 if target_skills else 0
            
            return {
                'progress': round(progress, 2),
                'completedSkills': completed_skills,
                'remainingSkills': remaining_skills,
                'totalSkills': len(target_skills)
            }
        except Exception as e:
            print(f"计算学习进度失败: {str(e)}")
            return {'progress': 0, 'completedSkills': [], 'remainingSkills': target_skills}
    
    def generate_personalized_plan(self, user_id, target_skills, timeframe=None, learning_style=None):
        """
        生成个性化学习计划
        :param user_id: 用户ID
        :param target_skills: 目标技能列表
        :param timeframe: 时间限制
        :param learning_style: 学习风格
        :return: 个性化学习计划
        """
        try:
            # 规划学习路径
            paths = self.plan_learning_path(user_id, target_skills, timeframe, learning_style)
            
            if not paths:
                return None
            
            # 选择最佳路径
            best_path = paths[0]
            
            # 获取详细信息
            path_details = self.get_learning_path_details(best_path, user_id)
            
            if not path_details:
                return None
            
            # 计算学习进度
            progress = self.calculate_learning_progress(user_id, target_skills)
            
            # 生成学习计划
            learning_plan = {
                'userId': user_id,
                'targetSkills': target_skills,
                'timeframe': timeframe,
                'learningStyle': learning_style,
                'progress': progress,
                'path': path_details['path'],
                'estimatedTime': path_details['estimatedTime'],
                'totalSkills': len(path_details['path']),
                'totalResources': sum(len(skill['resources']) for skill in path_details['path'])
            }
            
            return learning_plan
        except Exception as e:
            print(f"生成个性化学习计划失败: {str(e)}")
            return None


# 创建全局路径规划器实例
path_planner = PathPlanner()