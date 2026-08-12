# 应用入口，Flask服务配置

from flask import Flask, request, jsonify
from flask_cors import CORS
import logging
import time

from config import get_config
from models.graph_builder import graph_builder
from models.recommendation import recommendation_engine
from models.data_loader import data_loader
from utils.response import success_response, error_response

# 获取配置
config = get_config()

# 初始化Flask应用
app = Flask(__name__)

# 配置CORS，允许跨域请求
CORS(app, resources={r"/api/*": {"origins": "*"}})

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler('app.log', encoding='utf-8')
    ]
)
logger = logging.getLogger(__name__)


@app.route('/')
def index():
    """
    首页
    """
    return jsonify({
        "message": "企业推荐知识图谱服务",
        "version": "1.0.0",
        "apis": {
            "推荐企业": "/api/recommend/enterprise (POST)",
            "构建图谱": "/api/graph/build (GET)",
            "更新图谱": "/api/graph/update (GET)",
            "健康检查": "/api/health (GET)"
        }
    })


@app.route('/api/health', methods=['GET'])
def health_check():
    """
    健康检查接口
    """
    return success_response({
        "status": "ok",
        "timestamp": time.time(),
        "service": "enterprise-recommendation"
    })


@app.route('/api/graph/build', methods=['GET'])
def build_graph():
    """
    构建知识图谱接口
    """
    try:
        logger.info("开始构建知识图谱...")
        result = graph_builder.build_graph(use_cache=False)
        
        if result:
            return success_response({
                "message": "知识图谱构建成功",
                "enterprise_count": len(data_loader.enterprises),
                "position_count": len(data_loader.positions),
                "tag_count": len(data_loader.tags)
            })
        else:
            return error_response(500, "知识图谱构建失败")
            
    except Exception as e:
        logger.error(f"构建知识图谱出错: {str(e)}")
        return error_response(500, f"构建知识图谱出错: {str(e)}")


@app.route('/api/graph/update', methods=['GET'])
def update_graph():
    """
    更新知识图谱接口
    """
    try:
        logger.info("开始更新知识图谱...")
        result = graph_builder.update_graph()
        
        if result:
            return success_response({
                "message": "知识图谱更新成功",
                "enterprise_count": len(data_loader.enterprises),
                "position_count": len(data_loader.positions),
                "tag_count": len(data_loader.tags)
            })
        else:
            return error_response(500, "知识图谱更新失败")
            
    except Exception as e:
        logger.error(f"更新知识图谱出错: {str(e)}")
        return error_response(500, f"更新知识图谱出错: {str(e)}")


@app.route('/api/recommend/enterprise', methods=['POST'])
def recommend_enterprise():
    """
    企业推荐接口
    输入: {"userId": "123", "tags": ["软件工程师", "Java"], "preferredIndustry": "互联网", "preferredCity": "北京", "strategy": "hybrid"}
    输出: 推荐企业列表
    """
    try:
        data = request.get_json()

        if not data:
            return error_response(400, "请求参数不能为空")

        user_id = data.get('userId')
        user_tags = data.get('tags', [])
        preferred_industry = data.get('preferredIndustry')
        preferred_city = data.get('preferredCity')
        strategy = data.get('strategy', 'hybrid')
        top_n = data.get('topN', 10)

        logger.info(f"为用户 {user_id} 推荐企业，标签: {user_tags}，行业偏好: {preferred_industry}，城市偏好: {preferred_city}，策略: {strategy}")

        # 使用带上下文的多维度推荐
        recommendations = recommendation_engine.recommend_with_context(
            user_tags=user_tags,
            preferred_industry=preferred_industry,
            preferred_city=preferred_city,
            top_n=top_n,
            strategy=strategy
        )

        formatted_results = []
        for rec in recommendations:
            formatted_results.append({
                "enterpriseId": rec.get("enterpriseId"),
                "enterpriseName": rec.get("enterpriseName"),
                "relevanceScore": rec.get("relevanceScore", 0),
                "matchedTags": rec.get("matchedTags", []),
                "positions": rec.get("positions", [])
            })

        return success_response({
            "recommendations": formatted_results,
            "total": len(formatted_results),
            "request": {
                "userId": user_id,
                "tags": user_tags,
                "preferredIndustry": preferred_industry,
                "preferredCity": preferred_city,
                "strategy": strategy,
                "topN": top_n
            }
        })

    except Exception as e:
        logger.error(f"推荐企业出错: {str(e)}")
        return error_response(500, f"推荐企业出错: {str(e)}")


@app.route('/api/enterprise/<int:enterprise_id>/positions', methods=['GET'])
def get_enterprise_positions(enterprise_id):
    """
    获取指定企业的职位信息
    """
    try:
        # 从推荐引擎获取企业职位
        positions = recommendation_engine._get_enterprise_positions(enterprise_id)
        
        return success_response({
            "enterpriseId": enterprise_id,
            "positions": positions,
            "total": len(positions)
        })
        
    except Exception as e:
        logger.error(f"获取企业职位出错: {str(e)}")
        return error_response(500, f"获取企业职位出错: {str(e)}")


@app.route('/api/enterprise/<int:enterprise_id>/tags', methods=['GET'])
def get_enterprise_tags(enterprise_id):
    """
    获取指定企业的标签信息
    """
    try:
        # 从推荐引擎获取企业标签
        tags = recommendation_engine._get_enterprise_tags(enterprise_id)
        
        return success_response({
            "enterpriseId": enterprise_id,
            "tags": tags,
            "total": len(tags)
        })
        
    except Exception as e:
        logger.error(f"获取企业标签出错: {str(e)}")
        return error_response(500, f"获取企业标签出错: {str(e)}")


@app.route('/api/recommend/tags', methods=['POST'])
def recommend_tags():
    """
    标签推荐接口
    输入: {"tags": ["软件工程师", "Java", "Spring Boot"]}
    输出: 推荐的相关标签列表
    """
    try:
        # 解析请求参数
        data = request.get_json()
        
        # 验证参数
        if not data:
            return error_response(400, "请求参数不能为空")
        
        input_tags = data.get('tags', [])
        
        if not isinstance(input_tags, list) or len(input_tags) == 0:
            return error_response(400, "输入标签必须是非空列表")
        
        # 可选参数
        top_n = data.get('topN', 10)
        
        logger.info(f"为标签 {input_tags} 推荐相关标签")
        
        # 调用推荐引擎的标签推荐方法
        recommendations = recommendation_engine.recommend_related_tags(input_tags, top_n)
        
        return success_response({
            "tags": recommendations,
            "total": len(recommendations),
            "request": {
                "tags": input_tags,
                "topN": top_n
            }
        })
        
    except Exception as e:
        logger.error(f"标签推荐出错: {str(e)}")
        return error_response(500, f"标签推荐出错: {str(e)}")


if __name__ == '__main__':
    """
    应用入口
    """
    # 加载数据
    data_loader.load_all_data()
    data_loader.clean_data()
    
    # 构建知识图谱
    graph_builder.build_graph(use_cache=True)
    
    # 启动Flask服务
    app.run(
        host=config.HOST,
        port=config.PORT,
        debug=config.DEBUG
    )