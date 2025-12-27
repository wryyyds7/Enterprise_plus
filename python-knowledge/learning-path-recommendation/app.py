# 学习路径推荐服务入口

from flask import Flask, request, jsonify
from flask_cors import CORS
from config import get_config
from models.path_planner import path_planner
from models.resource_recommender import resource_recommender
from models.skill_predictor import skill_predictor
from services.data_loader import data_loader
from utils.response import success_response, error_response

# 创建Flask应用实例
app = Flask(__name__)

# 获取配置
config = get_config()

# 配置CORS
CORS(app, origins="*")  # 生产环境中应该限制允许的来源


@app.route('/health', methods=['GET'])
def health_check():
    """
    健康检查接口
    """
    return success_response(data={"status": "healthy", "service": "learning-path-recommendation"})


@app.route('/api/recommend/learning-path', methods=['POST'])
def recommend_learning_path():
    """
    推荐学习路径接口
    
    请求参数:
    {
        "userId": "123",
        "currentSkills": ["Java基础", "SQL入门"],
        "targetSkills": ["Spring Boot", "微服务架构"],
        "timeframe": "3个月",
        "learningStyle": "理论+实践"
    }
    
    返回结果:
    {
        "code": 200,
        "msg": "success",
        "data": {
            "userId": "123",
            "targetSkills": ["Spring Boot", "微服务架构"],
            "timeframe": "3个月",
            "learningStyle": "理论+实践",
            "progress": {
                "progress": 0,
                "completedSkills": [],
                "remainingSkills": ["Spring Boot", "微服务架构"],
                "totalSkills": 2
            },
            "path": [
                {
                    "skillId": "skill1",
                    "name": "Java面向对象",
                    "difficulty": "中级",
                    "category": "编程语言",
                    "resources": [
                        {
                            "resourceId": "res1",
                            "title": "Java面向对象编程详解",
                            "type": "视频教程",
                            "difficulty": "中级",
                            "duration": 10,
                            "url": "http://example.com/resource1",
                            "relevance": 0.9
                        }
                    ],
                    "prerequisites": ["Java基础"]
                }
            ],
            "estimatedTime": "约 60 天",
            "totalSkills": 4,
            "totalResources": 12
        }
    }
    """
    try:
        # 获取请求参数
        request_data = request.get_json()
        
        # 验证必填参数
        required_params = ["userId", "targetSkills"]
        for param in required_params:
            if param not in request_data:
                return error_response(code=400, msg=f"缺少必填参数: {param}")
        
        user_id = request_data["userId"]
        target_skills = request_data["targetSkills"]
        timeframe = request_data.get("timeframe")
        learning_style = request_data.get("learningStyle")
        
        # 生成个性化学习计划
        learning_plan = path_planner.generate_personalized_plan(
            user_id=user_id,
            target_skills=target_skills,
            timeframe=timeframe,
            learning_style=learning_style
        )
        
        if not learning_plan:
            return error_response(code=404, msg="无法生成学习路径")
        
        # 根据技能掌握度预测调整学习路径
        adjusted_path = skill_predictor.adjust_path_difficulty(user_id, learning_plan)
        
        # 为路径中的每个技能推荐资源
        if "path" in learning_plan:
            for i, skill in enumerate(learning_plan["path"]):
                skill_id = skill["skillId"]
                resources = resource_recommender.recommend_resources(
                    user_id=user_id,
                    skill_id=skill_id,
                    limit=config.MAX_RESOURCES_PER_SKILL
                )
                learning_plan["path"][i]["resources"] = resources
        
        return success_response(data=learning_plan)
        
    except Exception as e:
        app.logger.error(f"推荐学习路径失败: {str(e)}")
        return error_response(code=500, msg=f"推荐学习路径失败: {str(e)}")


@app.route('/api/predict/skill-mastery', methods=['POST'])
def predict_skill_mastery():
    """
    预测技能掌握度接口
    
    请求参数:
    {
        "userId": "123",
        "skillId": "skill1"
    }
    
    返回结果:
    {
        "code": 200,
        "msg": "success",
        "data": {
            "userId": "123",
            "skillId": "skill1",
            "masteryProbability": 0.85
        }
    }
    """
    try:
        # 获取请求参数
        request_data = request.get_json()
        
        # 验证必填参数
        required_params = ["userId", "skillId"]
        for param in required_params:
            if param not in request_data:
                return error_response(code=400, msg=f"缺少必填参数: {param}")
        
        user_id = request_data["userId"]
        skill_id = request_data["skillId"]
        
        # 预测技能掌握度
        probability = skill_predictor.predict_mastery(user_id, skill_id)
        
        return success_response(data={
            "userId": user_id,
            "skillId": skill_id,
            "masteryProbability": probability
        })
        
    except Exception as e:
        app.logger.error(f"预测技能掌握度失败: {str(e)}")
        return error_response(code=500, msg=f"预测技能掌握度失败: {str(e)}")


@app.route('/api/recommend/resources', methods=['POST'])
def recommend_resources_api():
    """
    推荐学习资源接口
    
    请求参数:
    {
        "userId": "123",
        "skillId": "skill1",
        "limit": 5
    }
    
    返回结果:
    {
        "code": 200,
        "msg": "success",
        "data": {
            "userId": "123",
            "skillId": "skill1",
            "resources": [
                {
                    "resourceId": "res1",
                    "title": "Java面向对象编程详解",
                    "type": "视频教程",
                    "difficulty": "中级",
                    "duration": 10,
                    "url": "http://example.com/resource1",
                    "relevance": 0.9
                }
            ]
        }
    }
    """
    try:
        # 获取请求参数
        request_data = request.get_json()
        
        # 验证必填参数
        required_params = ["userId", "skillId"]
        for param in required_params:
            if param not in request_data:
                return error_response(code=400, msg=f"缺少必填参数: {param}")
        
        user_id = request_data["userId"]
        skill_id = request_data["skillId"]
        limit = request_data.get("limit", config.MAX_RESOURCES_PER_SKILL)
        
        # 推荐学习资源
        resources = resource_recommender.recommend_resources(
            user_id=user_id,
            skill_id=skill_id,
            limit=limit
        )
        
        return success_response(data={
            "userId": user_id,
            "skillId": skill_id,
            "resources": resources
        })
        
    except Exception as e:
        app.logger.error(f"推荐学习资源失败: {str(e)}")
        return error_response(code=500, msg=f"推荐学习资源失败: {str(e)}")


if __name__ == '__main__':
    """
    启动应用
    """
    import logging
    
    # 配置日志
    logging.basicConfig(level=logging.INFO, 
                        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
    app.logger.setLevel(logging.INFO)
    
    # 加载实际数据
    app.logger.info("正在加载数据...")
    data_loader.load_all_data()
    
    # 启动服务
    app.logger.info(f"启动学习路径推荐服务，监听端口: {config.FLASK_PORT}")
    app.run(host='0.0.0.0', port=config.FLASK_PORT, debug=config.DEBUG)
