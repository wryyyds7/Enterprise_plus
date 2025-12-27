# 数据加载模块

from neo4j import GraphDatabase
from config import get_config
from .skill_service import skill_service
from .resource_service import resource_service
from .user_service import user_service
from utils.cache import cache
from utils.similarity import GLOBAL_LEARNING_TAGS


class DataLoader:
    """数据加载类，用于从Java后端获取数据并加载到Neo4j数据库"""
    
    def __init__(self):
        """
        初始化数据加载器
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
    

    

    
    def load_all_data(self):
        """
        加载所有数据到Neo4j
        """
        try:
            self.load_skills_data()
            self.load_resources_data()
            self.load_skill_resource_relations()
            self.load_skill_dependencies()
            print("数据加载完成")
        except Exception as e:
            print(f"数据加载失败: {str(e)}")
    

    

    

    

    

    

    

    
    def load_skills_data(self):
        """
        加载技能数据到Neo4j
        """
        print("开始加载技能数据...")
        
        try:
            skills = skill_service.get_all_skills()
            if not skills:
                print("没有获取到技能数据")
                return
            
            with self.driver.session() as session:
                for skill in skills:
                    skill_id = skill.get('skillId')
                    name = skill.get('name')
                    difficulty = skill.get('difficulty', 1)
                    category = skill.get('category', '其他')
                    
                    # 创建技能节点
                    session.run(
                        "MERGE (s:Skill {skillId: $skillId}) "
                        "SET s.name = $name, s.difficulty = $difficulty, s.category = $category",
                        skillId=skill_id,
                        name=name,
                        difficulty=difficulty,
                        category=category
                    )
                    
                    # 加载技能标签
                    tags = skill_service.get_skill_tags(skill_id)
                    if tags:
                        for tag in tags:
                            tag_id = tag.get('id')
                            tag_name = tag.get('name')
                            
                            # 创建标签节点
                            session.run(
                                "MERGE (t:Tag {tagId: $tagId}) "
                                "SET t.name = $tagName",
                                tagId=tag_id,
                                tagName=tag_name
                            )
                            
                            # 创建技能-标签关系
                            session.run(
                                "MATCH (s:Skill {skillId: $skillId}) "
                                "MATCH (t:Tag {tagId: $tagId}) "
                                "MERGE (s)-[:HAS_TAG]->(t)",
                                skillId=skill_id,
                                tagId=tag_id
                            )
            
            print(f"成功加载 {len(skills)} 个技能数据")
            
            # 缓存加载完成的信号
            cache.set("skills_loaded", True)
            
        except Exception as e:
            print(f"加载技能数据失败: {str(e)}")
    
    def load_resources_data(self):
        """
        加载学习资源数据到Neo4j
        """
        print("开始加载学习资源数据...")
        
        try:
            resources = resource_service.get_all_resources()
            if not resources:
                print("没有获取到学习资源数据")
                return
            
            with self.driver.session() as session:
                for resource in resources:
                    resource_id = resource.get('resourceId')
                    title = resource.get('title')
                    resource_type = resource.get('type', '其他')
                    difficulty = resource.get('difficulty', '初级')
                    duration = resource.get('duration', 0)
                    url = resource.get('url', '')
                    relevance = resource.get('relevance', 0.5)
                    
                    # 创建资源节点
                    session.run(
                        "MERGE (r:Resource {resourceId: $resourceId}) "
                        "SET r.title = $title, r.type = $type, r.difficulty = $difficulty, "
                        "r.duration = $duration, r.url = $url, r.relevance = $relevance",
                        resourceId=resource_id,
                        title=title,
                        type=resource_type,
                        difficulty=difficulty,
                        duration=duration,
                        url=url,
                        relevance=relevance
                    )
            
            print(f"成功加载 {len(resources)} 个学习资源数据")
            
            # 缓存加载完成的信号
            cache.set("resources_loaded", True)
            
        except Exception as e:
            print(f"加载学习资源数据失败: {str(e)}")
    
    def load_skill_resource_relations(self):
        """
        加载技能与学习资源的关系
        """
        print("开始加载技能与学习资源的关系...")
        
        try:
            skills = skill_service.get_all_skills()
            if not skills:
                print("没有获取到技能数据")
                return
            
            with self.driver.session() as session:
                for skill in skills:
                    skill_id = skill.get('skillId')
                    resources = resource_service.get_resources_by_skill(skill_id)
                    
                    if resources:
                        for resource in resources:
                            resource_id = resource.get('resourceId')
                            relevance = resource.get('relevance', 0.5)
                            
                            # 创建技能-资源关系
                            session.run(
                                "MATCH (s:Skill {skillId: $skillId}) "
                                "MATCH (r:Resource {resourceId: $resourceId}) "
                                "MERGE (s)-[rel:CONTAINS]->(r) "
                                "SET rel.relevance = $relevance",
                                skillId=skill_id,
                                resourceId=resource_id,
                                relevance=relevance
                            )
            
            print("成功加载技能与学习资源的关系")
            
        except Exception as e:
            print(f"加载技能与学习资源的关系失败: {str(e)}")
    
    def load_skill_dependencies(self):
        """
        加载技能之间的依赖关系
        """
        print("开始加载技能依赖关系...")
        
        try:
            skills = skill_service.get_all_skills()
            if not skills:
                print("没有获取到技能数据")
                return
            
            with self.driver.session() as session:
                for skill in skills:
                    skill_id = skill.get('skillId')
                    dependencies = skill_service.get_skill_dependencies(skill_id)
                    
                    if dependencies:
                        for dep in dependencies:
                            dep_skill_id = dep.get('skillId')
                            importance = dep.get('importance', 1.0)
                            
                            # 创建技能依赖关系
                            session.run(
                                "MATCH (s1:Skill {skillId: $depSkillId}) "
                                "MATCH (s2:Skill {skillId: $skillId}) "
                                "MERGE (s1)-[rel:DEPENDS_ON]->(s2) "
                                "SET rel.importance = $importance",
                                depSkillId=dep_skill_id,
                                skillId=skill_id,
                                importance=importance
                            )
            
            print("成功加载技能依赖关系")
            
        except Exception as e:
            print(f"加载技能依赖关系失败: {str(e)}")
    
    def load_user_data(self, user_id):
        """
        加载指定用户的数据到Neo4j
        :param user_id: 用户ID
        """
        print(f"开始加载用户 {user_id} 的数据...")
        
        try:
            user_skills = user_service.get_user_skills(user_id)
            if not user_skills:
                print(f"没有获取到用户 {user_id} 的技能数据")
                return
            
            with self.driver.session() as session:
                current_skills = user_skills.get('currentSkills', [])
                skill_levels = user_skills.get('skillLevels', {})
                learning_style = user_skills.get('learningStyle', '混合型')
                
                # 创建用户节点
                session.run(
                    "MERGE (u:User {userId: $userId}) "
                    "SET u.learningStyle = $learningStyle",
                    userId=user_id,
                    learningStyle=learning_style
                )
                
                # 创建用户-技能关系
                for skill_name in current_skills:
                    level = skill_levels.get(skill_name, 1)
                    
                    # 查找对应的技能节点
                    result = session.run(
                        "MATCH (s:Skill) WHERE s.name = $skillName RETURN s.skillId",
                        skillName=skill_name
                    )
                    
                    for record in result:
                        skill_id = record['s.skillId']
                        
                        # 创建用户-技能关系
                        session.run(
                            "MATCH (u:User {userId: $userId}) "
                            "MATCH (s:Skill {skillId: $skillId}) "
                            "MERGE (u)-[rel:MASTERS]->(s) "
                            "SET rel.proficiencyLevel = $level",
                            userId=user_id,
                            skillId=skill_id,
                            level=level
                        )
            
            print(f"成功加载用户 {user_id} 的数据")
            
            # 缓存用户数据已加载的信号
            cache.set(f"user_data_loaded:{user_id}", True)
            
        except Exception as e:
            print(f"加载用户 {user_id} 的数据失败: {str(e)}")
    
    def update_skill_data(self, skill_id):
        """
        更新指定技能的数据
        :param skill_id: 技能ID
        """
        print(f"开始更新技能 {skill_id} 的数据...")
        
        try:
            skill = skill_service.get_skill_by_id(skill_id)
            if not skill:
                print(f"没有获取到技能 {skill_id} 的数据")
                return
            
            with self.driver.session() as session:
                name = skill.get('name')
                difficulty = skill.get('difficulty', 1)
                category = skill.get('category', '其他')
                
                # 更新技能节点
                session.run(
                    "MATCH (s:Skill {skillId: $skillId}) "
                    "SET s.name = $name, s.difficulty = $difficulty, s.category = $category",
                    skillId=skill_id,
                    name=name,
                    difficulty=difficulty,
                    category=category
                )
                
                # 更新技能标签
                session.run(
                    "MATCH (s:Skill {skillId: $skillId})-[r:HAS_TAG]->(t) "
                    "DELETE r",
                    skillId=skill_id
                )
                
                tags = skill_service.get_skill_tags(skill_id)
                if tags:
                    for tag in tags:
                        tag_id = tag.get('id')
                        tag_name = tag.get('name')
                        
                        session.run(
                            "MERGE (t:Tag {tagId: $tagId}) "
                            "SET t.name = $tagName",
                            tagId=tag_id,
                            tagName=tag_name
                        )
                        
                        session.run(
                            "MATCH (s:Skill {skillId: $skillId}) "
                            "MATCH (t:Tag {tagId: $tagId}) "
                            "MERGE (s)-[:HAS_TAG]->(t)",
                            skillId=skill_id,
                            tagId=tag_id
                        )
            
            print(f"成功更新技能 {skill_id} 的数据")
            
        except Exception as e:
            print(f"更新技能 {skill_id} 的数据失败: {str(e)}")
    
    def sync_all_data(self):
        """
        同步所有数据
        """
        print("开始同步所有数据...")
        try:
            # 清除旧数据
            self.clear_all_data()
            
            # 重新加载所有数据
            self.load_all_data()
            
            print("数据同步完成")
        except Exception as e:
            print(f"数据同步失败: {str(e)}")
    
    def clear_all_data(self):
        """
        清除Neo4j中的所有数据
        """
        print("开始清除所有数据...")
        
        try:
            with self.driver.session() as session:
                session.run("MATCH (n) DETACH DELETE n")
            
            print("所有数据已清除")
            
            # 清除缓存
            cache.flush_all()
            
        except Exception as e:
            print(f"清除数据失败: {str(e)}")


# 创建全局数据加载器实例
data_loader = DataLoader()