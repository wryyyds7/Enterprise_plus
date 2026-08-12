# 推荐算法模块

from sklearn.metrics.pairwise import cosine_similarity
from sklearn.feature_extraction.text import CountVectorizer
from utils.similarity import calculate_cosine_similarity, calculate_jaccard_similarity
from models.graph_builder import graph_builder
from config import get_config

# 获取配置
config = get_config()


class RecommendationEngine:
    """推荐引擎类"""

    def __init__(self):
        """
        初始化推荐引擎
        """
        self.graph_builder = graph_builder
        # 企业热度缓存（用于冷启动和热门推荐）
        self._popularity_cache = None
        self._popularity_cache_time = 0
    
    def recommend_by_tag_similarity(self, user_tags, top_n=10):
        """
        基于标签相似度的推荐算法
        :param user_tags: 用户标签列表
        :param top_n: 返回推荐结果数量
        :return: 推荐企业列表
        """
        print(f"基于标签相似度为用户推荐企业...")
        
        recommendations = []
        
        try:
            # 从图谱中获取所有企业及其标签
            enterprises = self._get_all_enterprise_tags()
            
            # 计算每个企业与用户标签的相似度
            for enterprise in enterprises:
                enterprise_tags = enterprise.get('tags', [])
                
                if not enterprise_tags:
                    continue
                
                # 计算余弦相似度
                tag_similarity = calculate_cosine_similarity(user_tags, enterprise_tags)
                
                # 计算Jaccard相似度
                jaccard_similarity = calculate_jaccard_similarity(user_tags, enterprise_tags)
                
                # 综合相似度得分 (可以根据需要调整权重)
                combined_score = 0.7 * tag_similarity + 0.3 * jaccard_similarity
                
                # 添加到推荐列表
                recommendations.append({
                    'enterpriseId': enterprise['enterpriseId'],
                    'enterpriseName': enterprise['enterpriseName'],
                    'tags': enterprise_tags,
                    'tag_similarity': tag_similarity,
                    'jaccard_similarity': jaccard_similarity,
                    'relevanceScore': combined_score,
                    'matchedTags': list(set(user_tags) & set(enterprise_tags))
                })
            
            # 按综合得分排序
            recommendations.sort(key=lambda x: x['relevanceScore'], reverse=True)
            
            # 返回前top_n个结果
            return recommendations[:top_n]
            
        except Exception as e:
            print(f"基于标签相似度推荐失败: {str(e)}")
            return []
    
    def recommend_by_graph_traversal(self, user_tags, top_n=10):
        """
        基于图遍历的推荐算法
        :param user_tags: 用户标签列表
        :param top_n: 返回推荐结果数量
        :return: 推荐企业列表
        """
        print(f"基于图遍历为用户推荐企业...")
        
        try:
            # 使用图构建器的方法获取相关企业
            related_enterprises = self.graph_builder.get_related_enterprises(user_tags, top_n * 2)
            
            # 获取每个企业的详细信息和标签
            recommendations = []
            for enterprise in related_enterprises:
                enterprise_id = enterprise['enterpriseId']
                enterprise_name = enterprise['enterpriseName']
                tag_match_count = enterprise['tag_match_count']
                
                # 获取企业的所有标签
                enterprise_tags = self._get_enterprise_tags(enterprise_id)
                
                # 计算与用户标签的相似度
                similarity = calculate_cosine_similarity(user_tags, enterprise_tags)
                
                # 计算综合得分
                relevance_score = similarity * (1 + tag_match_count * 0.1)
                
                # 获取企业的职位信息
                positions = self._get_enterprise_positions(enterprise_id)
                
                recommendations.append({
                    'enterpriseId': enterprise_id,
                    'enterpriseName': enterprise_name,
                    'relevanceScore': relevance_score,
                    'matchedTagsCount': tag_match_count,
                    'matchedTags': list(set(user_tags) & set(enterprise_tags)),
                    'tags': enterprise_tags,
                    'positions': positions
                })
            
            # 按相关性得分排序
            recommendations.sort(key=lambda x: x['relevanceScore'], reverse=True)
            
            return recommendations[:top_n]
            
        except Exception as e:
            print(f"基于图遍历推荐失败: {str(e)}")
            return []
    
    def hybrid_recommend(self, user_tags, top_n=10):
        """
        混合推荐算法
        :param user_tags: 用户标签列表
        :param top_n: 返回推荐结果数量
        :return: 推荐企业列表
        """
        print(f"使用混合推荐算法为用户推荐企业...")
        
        try:
            # 获取两种推荐结果
            tag_based_recs = self.recommend_by_tag_similarity(user_tags, top_n * 2)
            graph_based_recs = self.recommend_by_graph_traversal(user_tags, top_n * 2)
            
            # 合并推荐结果
            combined_recs = {}
            
            # 处理标签相似度推荐结果
            for rec in tag_based_recs:
                enterprise_id = rec['enterpriseId']
                combined_recs[enterprise_id] = rec.copy()
                combined_recs[enterprise_id]['tag_based_score'] = rec['relevanceScore']
                combined_recs[enterprise_id]['graph_based_score'] = 0
                combined_recs[enterprise_id]['final_score'] = rec['relevanceScore'] * 0.6
            
            # 处理图遍历推荐结果
            for rec in graph_based_recs:
                enterprise_id = rec['enterpriseId']
                if enterprise_id in combined_recs:
                    combined_recs[enterprise_id]['graph_based_score'] = rec['relevanceScore']
                    combined_recs[enterprise_id]['final_score'] += rec['relevanceScore'] * 0.4
                    # 合并职位信息
                    if 'positions' in rec:
                        combined_recs[enterprise_id]['positions'] = rec['positions']
                else:
                    combined_recs[enterprise_id] = rec.copy()
                    combined_recs[enterprise_id]['tag_based_score'] = 0
                    combined_recs[enterprise_id]['graph_based_score'] = rec['relevanceScore']
                    combined_recs[enterprise_id]['final_score'] = rec['relevanceScore'] * 0.4
            
            # 转换为列表并排序
            recommendations = list(combined_recs.values())
            recommendations.sort(key=lambda x: x['final_score'], reverse=True)
            
            # 提取最终推荐结果
            final_recommendations = []
            for rec in recommendations[:top_n]:
                final_recommendations.append({
                    'enterpriseId': rec['enterpriseId'],
                    'enterpriseName': rec['enterpriseName'],
                    'relevanceScore': rec['final_score'],
                    'matchedTags': rec.get('matchedTags', []),
                    'positions': rec.get('positions', [])
                })
            
            return final_recommendations
            
        except Exception as e:
            print(f"混合推荐算法失败: {str(e)}")
            # 如果混合推荐失败，回退到标签相似度推荐
            return self.recommend_by_tag_similarity(user_tags, top_n)

    def recommend_popular(self, top_n=10):
        """
        热门推荐（冷启动策略）：按企业职位数量和标签数量排序
        :param top_n: 返回数量
        :return: 推荐企业列表
        """
        print("使用热门推荐策略（冷启动）...")

        try:
            enterprises = self._get_all_enterprise_tags()

            for ent in enterprises:
                eid = ent.get('enterpriseId')
                # 职位数作为热度指标
                positions = self._get_enterprise_positions(eid)
                ent['popularity_score'] = len(ent.get('tags', [])) * 0.3 + len(positions) * 0.7

            enterprises.sort(key=lambda x: x.get('popularity_score', 0), reverse=True)

            recommendations = []
            for ent in enterprises[:top_n]:
                recommendations.append({
                    'enterpriseId': ent['enterpriseId'],
                    'enterpriseName': ent['enterpriseName'],
                    'relevanceScore': min(ent['popularity_score'] / 10.0, 1.0),
                    'matchedTags': ent.get('tags', [])[:5],
                    'positions': []
                })
            return recommendations

        except Exception as e:
            print(f"热门推荐失败: {str(e)}")
            return []

    def recommend_with_context(self, user_tags=None, preferred_industry=None, preferred_city=None, top_n=10, strategy='hybrid'):
        """
        带上下文的多维度推荐
        :param user_tags: 用户标签
        :param preferred_industry: 期望行业
        :param preferred_city: 期望城市
        :param top_n: 返回数量
        :param strategy: 推荐策略
        :return: 推荐企业列表
        """
        # 冷启动：无标签时使用热门推荐
        if not user_tags or len(user_tags) == 0:
            print("用户无标签，使用冷启动策略")
            return self.recommend_popular(top_n)

        # 选择推荐策略
        if strategy == 'tag_based':
            recs = self.recommend_by_tag_similarity(user_tags, top_n * 2)
        elif strategy == 'graph':
            recs = self.recommend_by_graph_traversal(user_tags, top_n * 2)
        elif strategy == 'popular':
            return self.recommend_popular(top_n)
        else:
            recs = self.hybrid_recommend(user_tags, top_n * 2)

        # 二次过滤：根据行业和城市偏好
        if preferred_industry:
            recs = [r for r in recs if r.get('industry', '') == preferred_industry or
                    preferred_industry in r.get('matchedTags', [])]
        if preferred_city:
            recs = [r for r in recs if r.get('city', '') == preferred_city]

        return recs[:top_n] if recs else self.recommend_popular(top_n)
        """
        获取所有企业及其标签
        :return: 企业列表，每个企业包含标签信息
        """
        try:
            with self.graph_builder.driver.session() as session:
                query = """
                MATCH (e:Enterprise)-[:属于]->(t:Tag)
                RETURN e.enterpriseId, e.enterpriseName, COLLECT(t.name) as tags
                """
                
                results = session.run(query)
                
                enterprises = []
                for record in results:
                    enterprises.append({
                        'enterpriseId': record['e.enterpriseId'],
                        'enterpriseName': record['e.enterpriseName'],
                        'tags': record['tags']
                    })
                
                return enterprises
                
        except Exception as e:
            print(f"获取所有企业标签失败: {str(e)}")
            return []
    
    def _get_enterprise_tags(self, enterprise_id):
        """
        获取指定企业的标签
        :param enterprise_id: 企业ID
        :return: 标签列表
        """
        try:
            with self.graph_builder.driver.session() as session:
                query = """
                MATCH (e:Enterprise {enterpriseId: $enterpriseId})-[:属于]->(t:Tag)
                RETURN COLLECT(t.name) as tags
                """
                
                result = session.run(query, enterpriseId=enterprise_id).single()
                return result['tags'] if result else []
                
        except Exception as e:
            print(f"获取企业 {enterprise_id} 标签失败: {str(e)}")
            return []
    
    def _get_enterprise_positions(self, enterprise_id):
        """
        获取指定企业的职位信息
        :param enterprise_id: 企业ID
        :return: 职位列表
        """
        try:
            with self.graph_builder.driver.session() as session:
                query = """
                MATCH (e:Enterprise {enterpriseId: $enterpriseId})-[:招聘]->(p:Position)
                RETURN p.positionId, p.positionName, p.salary, p.city, p.description
                """
                
                results = session.run(query, enterpriseId=enterprise_id)
                
                positions = []
                for record in results:
                    positions.append({
                        'positionId': record['p.positionId'],
                        'positionName': record['p.positionName'],
                        'salary': record['p.salary'],
                        'city': record['p.city'],
                        'description': record['p.description']
                    })
                
                return positions
                
        except Exception as e:
            print(f"获取企业 {enterprise_id} 职位失败: {str(e)}")
            return []
    
    def recommend_related_tags(self, input_tags, top_n=10):
        """
        推荐相关标签
        :param input_tags: 输入标签列表
        :param top_n: 返回推荐结果数量
        :return: 推荐标签列表
        """
        print(f"推荐与标签 {input_tags} 相关的标签...")
        
        try:
            # 从图谱中获取所有标签
            all_tags = self._get_all_tags()
            
            # 计算每个标签与输入标签的相似度
            tag_similarities = []
            
            for tag in all_tags:
                # 跳过输入标签本身
                if tag in input_tags:
                    continue
                
                # 计算与每个输入标签的相似度
                similarities = []
                for input_tag in input_tags:
                    # 计算标签与输入标签的相似度
                    # 这里可以根据需要实现更复杂的标签关联计算
                    # 简单实现：如果两个标签同时出现在同一个企业中，则认为它们相关
                    co_occurrence = self._get_tag_co_occurrence(input_tag, tag)
                    
                    # 计算相似度得分
                    similarity_score = co_occurrence
                    similarities.append(similarity_score)
                
                # 综合相似度得分（取最大值或平均值）
                max_similarity = max(similarities) if similarities else 0
                avg_similarity = sum(similarities) / len(similarities) if similarities else 0
                
                # 使用综合得分
                final_score = 0.7 * max_similarity + 0.3 * avg_similarity
                
                tag_similarities.append({
                    'tag': tag,
                    'similarityScore': final_score,
                    'maxSimilarity': max_similarity,
                    'avgSimilarity': avg_similarity
                })
            
            # 按综合得分排序
            tag_similarities.sort(key=lambda x: x['similarityScore'], reverse=True)
            
            # 返回前top_n个结果
            recommended_tags = []
            for rec in tag_similarities[:top_n]:
                recommended_tags.append({
                    'tag': rec['tag'],
                    'similarityScore': rec['similarityScore']
                })
            
            return recommended_tags
            
        except Exception as e:
            print(f"标签推荐失败: {str(e)}")
            return []
    
    def _get_all_tags(self):
        """
        获取所有标签
        :return: 标签列表
        """
        try:
            with self.graph_builder.driver.session() as session:
                query = """
                MATCH (t:Tag)
                RETURN t.name
                """
                
                results = session.run(query)
                
                tags = []
                for record in results:
                    tags.append(record['t.name'])
                
                return tags
                
        except Exception as e:
            print(f"获取所有标签失败: {str(e)}")
            return []
    
    def _get_tag_co_occurrence(self, tag1, tag2):
        """
        获取两个标签的共现次数（同时出现在同一个企业中的次数）
        :param tag1: 标签1
        :param tag2: 标签2
        :return: 共现次数
        """
        try:
            with self.graph_builder.driver.session() as session:
                query = """
                MATCH (t1:Tag {name: $tag1})<-[:属于]-(e:Enterprise)-[:属于]->(t2:Tag {name: $tag2})
                RETURN COUNT(e) as co_occurrence
                """
                
                result = session.run(query, tag1=tag1, tag2=tag2).single()
                return result['co_occurrence'] if result else 0
                
        except Exception as e:
            print(f"获取标签共现次数失败: {str(e)}")
            return 0


# 创建推荐引擎实例
recommendation_engine = RecommendationEngine()