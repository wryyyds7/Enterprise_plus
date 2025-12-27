# 相似度计算工具

from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
import os

# 从文件加载全局学习标签
current_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
global_tags_file = os.path.join(current_dir, 'global_learning_tags.txt')

try:
    with open(global_tags_file, 'r', encoding='utf-8') as f:
        GLOBAL_LEARNING_TAGS = [line.strip() for line in f if line.strip()]
except FileNotFoundError:
    print(f"Warning: Global learning tags file not found at {global_tags_file}")
    GLOBAL_LEARNING_TAGS = []
except Exception as e:
    print(f"Warning: Failed to load global learning tags: {e}")
    GLOBAL_LEARNING_TAGS = []


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
    all_tags = GLOBAL_LEARNING_TAGS
    
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


def calculate_tag_similarity(tags1, tags2, method="cosine"):
    """
    计算两个标签列表的相似度
    :param tags1: 第一个标签列表
    :param tags2: 第二个标签列表
    :param method: 相似度计算方法 (cosine, jaccard)
    :return: 相似度值
    """
    if method == "cosine":
        return calculate_cosine_similarity(tags1, tags2)
    elif method == "jaccard":
        return calculate_jaccard_similarity(tags1, tags2)
    else:
        raise ValueError(f"Unsupported similarity method: {method}")


def get_matched_tags(tags1, tags2):
    """
    获取两个标签列表的匹配标签
    :param tags1: 第一个标签列表
    :param tags2: 第二个标签列表
    :return: 匹配的标签列表
    """
    set1 = set(tags1)
    set2 = set(tags2)
    
    return list(set1.intersection(set2))


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