#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试学习路径推荐的相似度计算功能
"""

import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from utils.similarity import GLOBAL_LEARNING_TAGS, calculate_cosine_similarity, calculate_jaccard_similarity


def test_global_learning_tags():
    """测试全局学习标签词典"""
    print("=== 测试全局学习标签词典 ===")
    print(f"全局学习标签总数: {len(GLOBAL_LEARNING_TAGS)}")
    print(f"前10个标签: {GLOBAL_LEARNING_TAGS[:10]}")
    print(f"后10个标签: {GLOBAL_LEARNING_TAGS[-10:]}")
    return True


def test_cosine_similarity():
    """测试余弦相似度计算"""
    print("\n=== 测试余弦相似度计算 ===")
    
    # 测试1: 完全相同的标签
    tags1 = ["Java", "Spring Boot", "后端开发", "视频教程"]
    tags2 = ["Java", "Spring Boot", "后端开发", "视频教程"]
    similarity = calculate_cosine_similarity(tags1, tags2)
    print(f"完全相同的标签相似度: {similarity:.4f} (预期: 1.0)")
    
    # 测试2: 部分相同的标签
    tags3 = ["Java", "Spring Boot", "后端开发"]
    tags4 = ["Java", "Python", "后端开发", "Web开发"]
    similarity = calculate_cosine_similarity(tags3, tags4)
    print(f"部分相同的标签相似度: {similarity:.4f} (预期: 约0.5)")
    
    # 测试3: 完全不同的标签
    tags5 = ["Java", "Spring Boot", "后端开发"]
    tags6 = ["UI设计", "Photoshop", "前端设计", "文档"]
    similarity = calculate_cosine_similarity(tags5, tags6)
    print(f"完全不同的标签相似度: {similarity:.4f} (预期: 0.0)")
    
    return True


def test_jaccard_similarity():
    """测试Jaccard相似度计算"""
    print("\n=== 测试Jaccard相似度计算 ===")
    
    # 测试1: 完全相同的标签
    tags1 = ["Java", "Spring Boot", "后端开发"]
    tags2 = ["Java", "Spring Boot", "后端开发"]
    similarity = calculate_jaccard_similarity(tags1, tags2)
    print(f"完全相同的标签相似度: {similarity:.4f} (预期: 1.0)")
    
    # 测试2: 部分相同的标签
    tags3 = ["Java", "Spring Boot", "后端开发"]
    tags4 = ["Java", "Python", "后端开发", "Web开发"]
    similarity = calculate_jaccard_similarity(tags3, tags4)
    print(f"部分相同的标签相似度: {similarity:.4f} (预期: 约0.4)")
    
    # 测试3: 完全不同的标签
    tags5 = ["Java", "Spring Boot"]
    tags6 = ["UI设计", "Photoshop"]
    similarity = calculate_jaccard_similarity(tags5, tags6)
    print(f"完全不同的标签相似度: {similarity:.4f} (预期: 0.0)")
    
    return True


if __name__ == "__main__":
    try:
        test_global_learning_tags()
        test_cosine_similarity()
        test_jaccard_similarity()
        print("\n=== 所有测试通过! ===")
    except Exception as e:
        print(f"\n=== 测试失败: {e} ===")
        sys.exit(1)
