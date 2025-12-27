# 知识图谱构建模块

from neo4j import GraphDatabase, basic_auth
from config import get_config
from models.data_loader import data_loader
from utils.cache import cache_graph, get_cached_graph

# 获取配置
config = get_config()


class GraphBuilder:
    """知识图谱构建类"""
    
    def __init__(self):
        """
        初始化知识图谱构建器
        """
        self.driver = None
        self._connect_to_neo4j()
    
    def _connect_to_neo4j(self):
        """
        连接到Neo4j数据库
        """
        try:
            self.driver = GraphDatabase.driver(
                config.NEO4J_URI,
                auth=basic_auth(config.NEO4J_USER, config.NEO4J_PASSWORD)
            )
            # 测试连接
            self.driver.verify_connectivity()
            print("成功连接到Neo4j数据库")
        except Exception as e:
            print(f"连接Neo4j数据库失败: {str(e)}")
            self.driver = None
    
    def close(self):
        """
        关闭数据库连接
        """
        if self.driver:
            self.driver.close()
            print("Neo4j数据库连接已关闭")
    
    def build_graph(self, use_cache=True):
        """
        构建知识图谱
        :param use_cache: 是否使用缓存
        :return: 构建状态
        """
        try:
            # 检查缓存
            if use_cache:
                cached_graph = get_cached_graph("enterprise_recommendation")
                if cached_graph:
                    print("使用缓存的知识图谱")
                    return True
            
            print("开始构建知识图谱...")
            
            # 加载数据
            if not data_loader.enterprises or not data_loader.positions:
                data_loader.load_all_data()
            
            # 创建节点和边
            with self.driver.session() as session:
                # 清空现有图数据
                session.run("MATCH (n) DETACH DELETE n")
                print("已清空现有图数据")
                
                # 创建企业节点
                self._create_enterprise_nodes(session)
                
                # 创建职位节点
                self._create_position_nodes(session)
                
                # 创建标签节点
                self._create_tag_nodes(session)
                
                # 创建企业-职位边
                self._create_enterprise_position_edges(session)
                
                # 创建企业-标签边
                self._create_enterprise_tag_edges(session)
                
                # 创建职位-标签边
                self._create_position_tag_edges(session)
            
            print("知识图谱构建完成")
            
            # 缓存图数据
            if use_cache:
                cache_graph("enterprise_recommendation", {"built_at": "now"})
            
            return True
        except Exception as e:
            print(f"构建知识图谱失败: {str(e)}")
            return False
    
    def _create_enterprise_nodes(self, session):
        """
        创建企业节点
        :param session: Neo4j会话
        """
        print("创建企业节点...")
        
        query = """
        CREATE (e:Enterprise {
            enterpriseId: $enterpriseId,
            enterpriseName: $enterpriseName,
            website: $website,
            city: $city,
            size: $size,
            industry: $industry
        })
        """
        
        count = 0
        for enterprise in data_loader.enterprises:
            parameters = {
                "enterpriseId": enterprise.get("enterpriseId"),
                "enterpriseName": enterprise.get("enterpriseName"),
                "website": enterprise.get("website", ""),
                "city": enterprise.get("city", ""),
                "size": enterprise.get("size", 0),
                "industry": enterprise.get("industry", "")
            }
            session.run(query, **parameters)
            count += 1
        
        print(f"创建了 {count} 个企业节点")
    
    def _create_position_nodes(self, session):
        """
        创建职位节点
        :param session: Neo4j会话
        """
        print("创建职位节点...")
        
        query = """
        CREATE (p:Position {
            positionId: $positionId,
            positionName: $positionName,
            salary: $salary,
            city: $city,
            description: $description
        })
        """
        
        count = 0
        for position in data_loader.positions:
            parameters = {
                "positionId": position.get("positionId"),
                "positionName": position.get("positionName"),
                "salary": position.get("salary", ""),
                "city": position.get("city", ""),
                "description": position.get("description", "")
            }
            session.run(query, **parameters)
            count += 1
        
        print(f"创建了 {count} 个职位节点")
    
    def _create_tag_nodes(self, session):
        """
        创建标签节点
        :param session: Neo4j会话
        """
        print("创建标签节点...")
        
        query = "CREATE (t:Tag {name: $name})"
        
        count = 0
        for tag in data_loader.tags:
            session.run(query, name=tag)
            count += 1
        
        print(f"创建了 {count} 个标签节点")
    
    def _create_enterprise_position_edges(self, session):
        """
        创建企业-职位边
        :param session: Neo4j会话
        """
        print("创建企业-职位边...")
        
        query = """
        MATCH (e:Enterprise {enterpriseId: $enterpriseId})
        MATCH (p:Position {positionId: $positionId})
        CREATE (e)-[:招聘]->(p)
        """
        
        count = 0
        for position in data_loader.positions:
            enterprise_id = position.get("enterpriseId")
            position_id = position.get("positionId")
            
            if enterprise_id and position_id:
                session.run(query, enterpriseId=enterprise_id, positionId=position_id)
                count += 1
        
        print(f"创建了 {count} 个企业-职位边")
    
    def _create_enterprise_tag_edges(self, session):
        """
        创建企业-标签边
        :param session: Neo4j会话
        """
        print("创建企业-标签边...")
        
        query = """
        MATCH (e:Enterprise {enterpriseId: $enterpriseId})
        MATCH (t:Tag {name: $tagName})
        CREATE (e)-[:属于 {importance: $importance}]->(t)
        """
        
        count = 0
        for mapping in data_loader.entity_tag_mappings:
            if mapping["entityType"] == "enterprise":
                session.run(
                    query,
                    enterpriseId=mapping["entityId"],
                    tagName=mapping["tag"],
                    importance=1.0  # 默认权重
                )
                count += 1
        
        print(f"创建了 {count} 个企业-标签边")
    
    def _create_position_tag_edges(self, session):
        """
        创建职位-标签边
        :param session: Neo4j会话
        """
        print("创建职位-标签边...")
        
        query = """
        MATCH (p:Position {positionId: $positionId})
        MATCH (t:Tag {name: $tagName})
        CREATE (p)-[:需要技能 {relevance: $relevance}]->(t)
        """
        
        count = 0
        for mapping in data_loader.entity_tag_mappings:
            if mapping["entityType"] == "position":
                session.run(
                    query,
                    positionId=mapping["entityId"],
                    tagName=mapping["tag"],
                    relevance=1.0  # 默认权重
                )
                count += 1
        
        print(f"创建了 {count} 个职位-标签边")
    
    def add_user_node(self, user_id, name, tags):
        """
        添加用户节点和用户-标签边
        :param user_id: 用户ID
        :param name: 用户名
        :param tags: 用户标签列表
        :return: 添加状态
        """
        try:
            with self.driver.session() as session:
                # 创建用户节点
                session.run(
                    "CREATE (u:User {userId: $userId, name: $name})",
                    userId=user_id,
                    name=name
                )
                
                # 创建用户-标签边
                for tag in tags:
                    session.run(
                        """
                        MATCH (u:User {userId: $userId})
                        MATCH (t:Tag {name: $tagName})
                        CREATE (u)-[:拥有标签]->(t)
                        """,
                        userId=user_id,
                        tagName=tag
                    )
            
            print(f"已添加用户 {user_id} 节点和标签边")
            return True
        except Exception as e:
            print(f"添加用户节点失败: {str(e)}")
            return False
    
    def get_related_enterprises(self, user_tags, top_n=10):
        """
        根据用户标签获取相关企业
        :param user_tags: 用户标签列表
        :param top_n: 返回企业数量
        :return: 相关企业列表
        """
        try:
            with self.driver.session() as session:
                # 构建查询条件
                tag_conditions = " OR ".join([f"t.name = '{tag}'" for tag in user_tags])
                
                query = f"""
                MATCH (e:Enterprise)-[:属于]->(t:Tag)
                WHERE {tag_conditions}
                RETURN e.enterpriseId, e.enterpriseName, COUNT(t) as tag_match_count
                ORDER BY tag_match_count DESC
                LIMIT {top_n}
                """
                
                results = session.run(query)
                
                enterprises = []
                for record in results:
                    enterprises.append({
                        "enterpriseId": record["e.enterpriseId"],
                        "enterpriseName": record["e.enterpriseName"],
                        "matchedTagsCount": record["tag_match_count"]
                    })
                
                return enterprises
        except Exception as e:
            print(f"查询相关企业失败: {str(e)}")
            return []
    
    def update_graph(self):
        """
        更新知识图谱
        :return: 更新状态
        """
        print("开始更新知识图谱...")
        # 重新构建图谱
        return self.build_graph(use_cache=True)


# 知识图谱构建器实例
graph_builder = GraphBuilder()