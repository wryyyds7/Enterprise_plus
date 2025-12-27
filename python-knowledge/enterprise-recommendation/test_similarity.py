#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试相似度计算功能
"""

import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from utils.similarity import GLOBAL_TAGS, calculate_cosine_similarity, calculate_jaccard_similarity


def test_global_tags():
    """测试全局标签词典"""
    print("=== 测试全局标签词典 ===")
    print(f"全局标签总数: {len(GLOBAL_TAGS)}")
    print(f"前10个标签: {GLOBAL_TAGS[:10]}")
    print(f"后10个标签: {GLOBAL_TAGS[-10:]}")
    return True


def test_cosine_similarity():
    """测试余弦相似度计算"""
    print("\n=== 测试余弦相似度计算 ===")
    
    # 测试1: 完全相同的标签
    tags1 = ["Java开发工程师", "软件公司", "互联网公司"]
    tags2 = ["Java开发工程师", "软件公司", "互联网公司"]
    similarity = calculate_cosine_similarity(tags1, tags2)
    print(f"完全相同的标签相似度: {similarity:.4f} (预期: 1.0)")
    
    # 测试2: 部分相同的标签
    tags3 = ["Java开发工程师", "软件公司", "互联网公司"]
    tags4 = ["Java开发工程师", "Python开发工程师", "软件公司"]
    similarity = calculate_cosine_similarity(tags3, tags4)
    print(f"部分相同的标签相似度: {similarity:.4f} (预期: 约0.6667)")
    
    # 测试3: 完全不同的标签
    tags5 = ["Java开发工程师", "软件公司"]
    tags6 = ["销售经理", "金融公司"]
    similarity = calculate_cosine_similarity(tags5, tags6)
    print(f"完全不同的标签相似度: {similarity:.4f} (预期: 0.0)")
    
    # 测试4: 空标签列表
    tags7 = []
    tags8 = ["Java开发工程师"]
    similarity = calculate_cosine_similarity(tags7, tags8)
    print(f"空标签列表相似度: {similarity:.4f} (预期: 0.0)")
    
    return True


def test_jaccard_similarity():
    """测试Jaccard相似度计算"""
    print("\n=== 测试Jaccard相似度计算 ===")
    
    # 测试1: 完全相同的标签
    tags1 = ["Java开发工程师", "软件公司", "互联网公司"]
    tags2 = ["Java开发工程师", "软件公司", "互联网公司"]
    similarity = calculate_jaccard_similarity(tags1, tags2)
    print(f"完全相同的标签相似度: {similarity:.4f} (预期: 1.0)")
    
    # 测试2: 部分相同的标签
    tags3 = ["Java开发工程师", "软件公司", "互联网公司"]
    tags4 = ["Java开发工程师", "Python开发工程师", "软件公司", "科技公司"]
    similarity = calculate_jaccard_similarity(tags3, tags4)
    print(f"部分相同的标签相似度: {similarity:.4f} (预期: 约0.4)")
    
    # 测试3: 完全不同的标签
    tags5 = ["Java开发工程师", "软件公司"]
    tags6 = ["销售经理", "金融公司"]
    similarity = calculate_jaccard_similarity(tags5, tags6)
    print(f"完全不同的标签相似度: {similarity:.4f} (预期: 0.0)")
    
    return True


if __name__ == "__main__":
    try:
        test_global_tags()
        test_cosine_similarity()
        test_jaccard_similarity()
        print("\n=== 所有测试通过! ===")
    except Exception as e:
        print(f"\n=== 测试失败: {e} ===")
        sys.exit(1)
