# 知识图谱构建模块

from neo4j import GraphDatabase
from config import get_config
from utils.graph_algorithms import Graph, GraphNode
from utils.cache import cache


class GraphBuilder:
    """知识图谱构建类，用于从Neo4j加载数据并构建内存中的图结构"""
    
    def __init__(self):
        """
        初始化知识图谱构建器
        """
        self.config = get_config()
        self.driver = GraphDatabase.driver(
            self.config.NEO4J_URI,
            auth=(self.config.NEO4J_USER, self.config.NEO4J_PASSWORD)
        )
    
    def close(self):
        """
        关闭Neo4j连接
        """
        if self.driver:
            self.driver.close()
    
    def build_skill_graph(self):
        """
        构建技能知识图谱
        :return: 技能知识图谱对象
        """
        print("开始构建技能知识图谱...")
        
        # 尝试从缓存获取
        cache_key = "skill_graph"
        cached_graph = cache.get(cache_key)
        if cached_graph:
            print("从缓存加载技能知识图谱")
            return cached_graph
        
        try:
            graph = Graph()
            
            with self.driver.session() as session:
                # 加载所有技能节点
                result = session.run("MATCH (s:Skill) RETURN s.skillId, s.name, s.difficulty, s.category")
                
                for record in result:
                    skill_id = record['s.skillId']
                    data = {
                        'name': record['s.name'],
                        'difficulty': record['s.difficulty'],
                        'category': record['s.category']
                    }
                    graph.add_node(skill_id, data)
                
                # 加载技能依赖关系
                result = session.run(
                    "MATCH (s1:Skill)-[r:DEPENDS_ON]->(s2:Skill) "
                    "RETURN s1.skillId, s2.skillId, r.importance"
                )
                
                for record in result:
                    from_skill_id = record['s1.skillId']
                    to_skill_id = record['s2.skillId']
                    importance = record['r.importance']
                    
                    # 根据依赖重要性和技能难度计算权重
                    from_skill = graph.nodes[from_skill_id].data
                    to_skill = graph.nodes[to_skill_id].data
                    
                    # 权重计算：重要性 * (from_skill.difficulty + to_skill.difficulty) / 2
                    weight = importance * (from_skill['difficulty'] + to_skill['difficulty']) / 2
                    
                    graph.add_edge(from_skill_id, to_skill_id, weight)
            
            print(f"成功构建技能知识图谱，包含 {len(graph.nodes)} 个技能节点")
            
            # 缓存图谱
            cache.set(cache_key, graph)
            
            return graph
        except Exception as e:
            print(f"构建技能知识图谱失败: {str(e)}")
            return None
    
    def build_resource_graph(self):
        """
        构建学习资源知识图谱
        :return: 学习资源知识图谱对象
        """
        print("开始构建学习资源知识图谱...")
        
        # 尝试从缓存获取
        cache_key = "resource_graph"
        cached_graph = cache.get(cache_key)
        if cached_graph:
            print("从缓存加载学习资源知识图谱")
            return cached_graph
        
        try:
            graph = Graph()
            
            with self.driver.session() as session:
                # 加载所有学习资源节点
                result = session.run(
                    "MATCH (r:Resource) "
                    "RETURN r.resourceId, r.title, r.type, r.difficulty, r.duration, r.relevance"
                )
                
                for record in result:
                    resource_id = record['r.resourceId']
                    data = {
                        'title': record['r.title'],
                        'type': record['r.type'],
                        'difficulty': record['r.difficulty'],
                        'duration': record['r.duration'],
                        'relevance': record['r.relevance']
                    }
                    graph.add_node(resource_id, data)
                
                # 加载技能-资源关系
                result = session.run(
                    "MATCH (s:Skill)-[r:CONTAINS]->(res:Resource) "
                    "RETURN s.skillId, res.resourceId, r.relevance"
                )
                
                for record in result:
                    skill_id = record['s.skillId']
                    resource_id = record['res.resourceId']
                    relevance = record['r.relevance']
                    
                    # 将技能节点也添加到图谱中
                    if skill_id not in graph.nodes:
                        skill_result = session.run(
                            "MATCH (s:Skill) WHERE s.skillId = $skillId RETURN s.name, s.difficulty, s.category",
                            skillId=skill_id
                        )
                        for skill_record in skill_result:
                            data = {
                                'name': skill_record['s.name'],
                                'difficulty': skill_record['s.difficulty'],
                                'category': skill_record['s.category']
                            }
                            graph.add_node(skill_id, data)
                    
                    # 创建技能-资源关系
                    graph.add_edge(skill_id, resource_id, 1.0 / relevance)  # 相关性越高，权重越小
            
            print(f"成功构建学习资源知识图谱，包含 {len(graph.nodes)} 个节点")
            
            # 缓存图谱
            cache.set(cache_key, graph)
            
            return graph
        except Exception as e:
            print(f"构建学习资源知识图谱失败: {str(e)}")
            return None
    
    def build_user_profile_graph(self, user_id):
        """
        构建用户学习图谱
        :param user_id: 用户ID
        :return: 用户学习图谱对象
        """
        print(f"开始构建用户 {user_id} 的学习图谱...")
        
        # 尝试从缓存获取
        cache_key = f"user_profile_graph:{user_id}"
        cached_graph = cache.get(cache_key)
        if cached_graph:
            print(f"从缓存加载用户 {user_id} 的学习图谱")
            return cached_graph
        
        try:
            graph = Graph()
            
            with self.driver.session() as session:
                # 加载用户节点
                result = session.run(
                    "MATCH (u:User {userId: $userId}) "
                    "RETURN u.learningStyle",
                    userId=user_id
                )
                
                for record in result:
                    learning_style = record['u.learningStyle']
                    graph.add_node(user_id, {'type': 'user', 'learningStyle': learning_style})
                
                # 加载用户掌握的技能
                result = session.run(
                    "MATCH (u:User {userId: $userId})-[r:MASTERS]->(s:Skill) "
                    "RETURN s.skillId, s.name, s.difficulty, s.category, r.proficiencyLevel",
                    userId=user_id
                )
                
                for record in result:
                    skill_id = record['s.skillId']
                    proficiency_level = record['r.proficiencyLevel']
                    data = {
                        'type': 'skill',
                        'name': record['s.name'],
                        'difficulty': record['s.difficulty'],
                        'category': record['s.category'],
                        'proficiencyLevel': proficiency_level
                    }
                    graph.add_node(skill_id, data)
                    
                    # 创建用户-技能关系
                    graph.add_edge(user_id, skill_id, 1.0 / proficiency_level)  # 掌握程度越高，权重越小
                
                # 加载技能的依赖关系
                result = session.run(
                    "MATCH (s1:Skill)-[r:DEPENDS_ON]->(s2:Skill) "
                    "RETURN s1.skillId, s2.skillId, r.importance"
                )
                
                for record in result:
                    from_skill_id = record['s1.skillId']
                    to_skill_id = record['s2.skillId']
                    importance = record['r.importance']
                    
                    # 确保技能节点存在
                    if from_skill_id not in graph.nodes:
                        skill_result = session.run(
                            "MATCH (s:Skill) WHERE s.skillId = $skillId RETURN s.name, s.difficulty, s.category",
                            skillId=from_skill_id
                        )
                        for skill_record in skill_result:
                            data = {
                                'type': 'skill',
                                'name': skill_record['s.name'],
                                'difficulty': skill_record['s.difficulty'],
                                'category': skill_record['s.category']
                            }
                            graph.add_node(from_skill_id, data)
                    
                    if to_skill_id not in graph.nodes:
                        skill_result = session.run(
                            "MATCH (s:Skill) WHERE s.skillId = $skillId RETURN s.name, s.difficulty, s.category",
                            skillId=to_skill_id
                        )
                        for skill_record in skill_result:
                            data = {
                                'type': 'skill',
                                'name': skill_record['s.name'],
                                'difficulty': skill_record['s.difficulty'],
                                'category': skill_record['s.category']
                            }
                            graph.add_node(to_skill_id, data)
                    
                    # 计算权重
                    from_skill = graph.nodes[from_skill_id].data
                    to_skill = graph.nodes[to_skill_id].data
                    
                    weight = importance * (from_skill['difficulty'] + to_skill['difficulty']) / 2
                    
                    graph.add_edge(from_skill_id, to_skill_id, weight)
            
            print(f"成功构建用户 {user_id} 的学习图谱，包含 {len(graph.nodes)} 个节点")
            
            # 缓存图谱
            cache.set(cache_key, graph)
            
            return graph
        except Exception as e:
            print(f"构建用户 {user_id} 的学习图谱失败: {str(e)}")
            return None
    
    def get_skill_neighbors(self, skill_id, depth=1):
        """
        获取技能的邻居节点
        :param skill_id: 技能ID
        :param depth: 搜索深度
        :return: 邻居技能列表
        """
        try:
            graph = self.build_skill_graph()
            if not graph or skill_id not in graph.nodes:
                return []
            
            neighbors = set()
            
            def dfs(current_id, current_depth):
                if current_depth > depth:
                    return
                
                current_node = graph.nodes[current_id]
                for neighbor_id in current_node.neighbors:
                    if neighbor_id != skill_id and neighbor_id not in neighbors:
                        neighbors.add(neighbor_id)
                        dfs(neighbor_id, current_depth + 1)
            
            dfs(skill_id, 0)
            
            # 转换为包含技能信息的列表
            result = []
            for neighbor_id in neighbors:
                node = graph.nodes[neighbor_id]
                result.append({
                    'skillId': neighbor_id,
                    'name': node.data['name'],
                    'difficulty': node.data['difficulty'],
                    'category': node.data['category']
                })
            
            return result
        except Exception as e:
            print(f"获取技能邻居失败: {str(e)}")
            return []
    
    def get_shortest_path(self, start_skill_id, end_skill_id):
        """
        获取两个技能之间的最短路径
        :param start_skill_id: 起始技能ID
        :param end_skill_id: 目标技能ID
        :return: 最短路径和路径成本
        """
        try:
            graph = self.build_skill_graph()
            if not graph:
                return None, None
            
            from utils.graph_algorithms import dijkstra
            return dijkstra(graph, start_skill_id, end_skill_id)
        except Exception as e:
            print(f"获取技能最短路径失败: {str(e)}")
            return None, None


# 创建全局知识图谱构建器实例
graph_builder = GraphBuilder()