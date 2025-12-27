# 测试动态标签加载功能

import os
import sys

# 测试企业推荐模块的标签加载
enterprise_path = 'd:\\bianchenglianxi\\java\\project\\enterprise_plus\\python-knowledge\\enterprise-recommendation'
sys.path.append(enterprise_path)

# 导入模块
import importlib.util

# 导入企业推荐模块的similarity
enterprise_similarity_path = os.path.join(enterprise_path, 'utils', 'similarity.py')
spec = importlib.util.spec_from_file_location("similarity", enterprise_similarity_path)
enterprise_similarity = importlib.util.module_from_spec(spec)
sys.modules["similarity"] = enterprise_similarity
spec.loader.exec_module(enterprise_similarity)

print("=== 企业推荐模块标签加载测试 ===")
print(f"加载的标签数量: {len(enterprise_similarity.GLOBAL_TAGS)}")
if enterprise_similarity.GLOBAL_TAGS:
    print(f"前10个标签: {enterprise_similarity.GLOBAL_TAGS[:10]}")
else:
    print("未加载到任何标签")

# 测试学习路径推荐模块的标签加载
learning_path = 'd:\\bianchenglianxi\\java\\project\\enterprise_plus\\python-knowledge\\learning-path-recommendation'
sys.path.append(learning_path)

# 导入学习路径推荐模块的similarity
learning_similarity_path = os.path.join(learning_path, 'utils', 'similarity.py')
spec = importlib.util.spec_from_file_location("learning_similarity", learning_similarity_path)
learning_similarity = importlib.util.module_from_spec(spec)
sys.modules["learning_similarity"] = learning_similarity
spec.loader.exec_module(learning_similarity)

print("\n=== 学习路径推荐模块标签加载测试 ===")
print(f"加载的标签数量: {len(learning_similarity.GLOBAL_LEARNING_TAGS)}")
if learning_similarity.GLOBAL_LEARNING_TAGS:
    print(f"前10个标签: {learning_similarity.GLOBAL_LEARNING_TAGS[:10]}")
else:
    print("未加载到任何标签")

print("\n标签加载测试完成!")