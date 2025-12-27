import sys
import os

# 添加当前目录到Python路径
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

try:
    from utils.similarity import GLOBAL_TAGS
    print("✓ 成功加载全局标签")
    print(f"加载的标签数量: {len(GLOBAL_TAGS)}")
    print(f"前10个标签: {GLOBAL_TAGS[:10]}")
    if len(GLOBAL_TAGS) > 0:
        print("✓ 标签加载功能正常")
    else:
        print("✗ 未加载到任何标签")
except Exception as e:
    print(f"✗ 标签加载失败: {e}")
