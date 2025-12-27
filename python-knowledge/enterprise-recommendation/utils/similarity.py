# 相似度计算工具

from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np

# 从文件中读取全局标签词典
import os

# 获取词库文件路径
current_dir = os.path.dirname(os.path.abspath(__file__))
global_tags_file = os.path.join(current_dir, '../global_tags.txt')

# 读取全局标签词典
try:
    with open(global_tags_file, 'r', encoding='utf-8') as f:
        GLOBAL_TAGS = [line.strip() for line in f if line.strip()]
except FileNotFoundError:
    # 如果文件不存在，使用空列表
    GLOBAL_TAGS = []
    print(f"Warning: Global tags file not found at {global_tags_file}")
except Exception as e:
    # 其他错误处理
    GLOBAL_TAGS = []
    print(f"Error reading global tags file: {e}")


def calculate_cosine_similarity(list1, list2):
    """
    计算两个列表的余弦相似度
    :param list1: 第一个列表
    :param list2: 第二个列表
    :return: 余弦相似度值
    """
    if not list1 or not list2:
        return 0.0
    
    # 使用全局标签词典
    all_tags = GLOBAL_TAGS
    
    # 创建向量
    vector1 = [1 if tag in list1 else 0 for tag in all_tags]
    vector2 = [1 if tag in list2 else 0 for tag in all_tags]
    
    # 计算余弦相似度
    similarity = cosine_similarity([vector1], [vector2])[0][0]
    
    return similarity


def calculate_jaccard_similarity(list1, list2):
    """
    计算两个列表的Jaccard相似度
    :param list1: 第一个列表
    :param list2: 第二个列表
    :return: Jaccard相似度值
    """
    set1 = set(list1)
    set2 = set(list2)
    
    if not set1 and not set2:
        return 1.0
    if not set1 or not set2:
        return 0.0
    
    intersection = len(set1.intersection(set2))
    union = len(set1.union(set2))
    
    return intersection / union


def calculate_tag_similarity(user_tags, entity_tags, method="cosine"):
    """
    计算用户标签与实体标签的相似度
    :param user_tags: 用户标签列表
    :param entity_tags: 实体标签列表
    :param method: 相似度计算方法 (cosine, jaccard)
    :return: 相似度值
    """
    if method == "cosine":
        return calculate_cosine_similarity(user_tags, entity_tags)
    elif method == "jaccard":
        return calculate_jaccard_similarity(user_tags, entity_tags)
    else:
        raise ValueError(f"Unsupported similarity method: {method}")


def calculate_weighted_similarity(user_tags, enterprise_tags, position_tags, 
                                  enterprise_weight=0.6, position_weight=0.4):
    """
    计算加权相似度（综合企业和职位标签）
    :param user_tags: 用户标签列表
    :param enterprise_tags: 企业标签列表
    :param position_tags: 职位标签列表
    :param enterprise_weight: 企业标签权重
    :param position_weight: 职位标签权重
    :return: 加权相似度值
    """
    enterprise_similarity = calculate_cosine_similarity(user_tags, enterprise_tags)
    position_similarity = calculate_cosine_similarity(user_tags, position_tags)
    
    weighted_similarity = (enterprise_similarity * enterprise_weight) + (position_similarity * position_weight)
    
    return weighted_similarity


def get_matched_tags(user_tags, entity_tags):
    """
    获取用户标签与实体标签的匹配标签
    :param user_tags: 用户标签列表
    :param entity_tags: 实体标签列表
    :return: 匹配的标签列表
    """
    user_set = set(user_tags)
    entity_set = set(entity_tags)
    
    return list(user_set.intersection(entity_set))


def normalize_scores(scores):
    """
    归一化分数
    :param scores: 分数列表
    :return: 归一化后的分数列表
    """
    if not scores:
        return []
    
    max_score = max(scores)
    min_score = min(scores)
    
    if max_score == min_score:
        return [1.0 for _ in scores]
    
    normalized = [(score - min_score) / (max_score - min_score) for score in scores]
    
    return normalized