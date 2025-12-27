# 学习路径推荐知识图谱模块

## 功能介绍

学习路径推荐模块是一个基于知识图谱和机器学习的个性化学习路径推荐系统。该模块能够根据用户的当前技能水平、学习目标和学习风格，为用户推荐最优的学习路径和学习资源。

### 核心功能

- **个性化学习路径规划**：基于用户当前技能和目标技能，规划最佳学习路径
- **学习资源推荐**：根据用户学习风格和技能需求，推荐合适的学习资源
- **技能掌握度预测**：预测用户学习新技能的成功率，调整学习路径难度
- **学习进度跟踪**：计算用户的学习进度和完成情况

## 技术栈

- **Web框架**：Flask 2.0+
- **知识图谱**：Neo4j
- **机器学习**：scikit-learn 1.1+
- **缓存**：Redis 4.0+
- **HTTP请求**：requests 2.28+
- **数据处理**：pandas 1.5+, numpy 1.21+

## 目录结构

```
learning-path-recommendation/
├── app.py                    # 应用入口，Flask服务配置
├── config.py                 # 配置文件
├── models/
│   ├── graph_builder.py      # 知识图谱构建模块
│   ├── path_planner.py       # 学习路径规划模块
│   ├── skill_predictor.py    # 技能掌握度预测模块
│   └── resource_recommender.py # 学习资源推荐模块
├── services/
│   ├── skill_service.py      # 技能数据服务
│   ├── resource_service.py   # 学习资源服务
│   └── user_service.py       # 用户数据服务
├── utils/
│   ├── graph_algorithms.py   # 图算法工具
│   ├── cache.py              # 缓存工具
│   └── response.py           # 响应格式化工具
├── tests/                    # 测试文件目录
├── requirements.txt          # 依赖包列表
└── README.md                 # 项目说明文档
```

## 配置说明

配置文件 `config.py` 包含以下主要配置项：

- `FLASK_PORT`：Flask服务监听端口（默认：5001）
- `DEBUG`：调试模式开关（默认：True）
- `NEO4J_URI`：Neo4j数据库连接URI
- `REDIS_URL`：Redis缓存连接URL
- `JAVA_API_BASE_URL`：Java后端API基础URL
- `PATH_PLANNING_ALGORITHM`：路径规划算法（dijkstra或a_star，默认：a_star）
- `SKILL_PREDICTION_MODEL`：技能预测模型（decision_tree或random_forest，默认：decision_tree）

## 快速开始

### 环境准备

1. 安装Python 3.8+和pip
2. 安装依赖包：

```bash
pip install -r requirements.txt
```

### 启动服务

```bash
python app.py
```

服务将在 `http://localhost:5001` 启动。

## API接口

### 1. 健康检查

```
GET /health
```

**返回结果**：
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "status": "healthy",
    "service": "learning-path-recommendation"
  }
}
```

### 2. 推荐学习路径

```
POST /api/recommend/learning-path
```

**请求参数**：
```json
{
  "userId": "123",
  "currentSkills": ["Java基础", "SQL入门"],
  "targetSkills": ["Spring Boot", "微服务架构"],
  "timeframe": "3个月",
  "learningStyle": "理论+实践"
}
```

**返回结果**：
```json
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
```

### 3. 预测技能掌握度

```
POST /api/predict/skill-mastery
```

**请求参数**：
```json
{
  "userId": "123",
  "skillId": "skill1"
}
```

**返回结果**：
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "userId": "123",
    "skillId": "skill1",
    "masteryProbability": 0.85
  }
}
```

### 4. 推荐学习资源

```
POST /api/recommend/resources
```

**请求参数**：
```json
{
  "userId": "123",
  "skillId": "skill1",
  "limit": 5
}
```

**返回结果**：
```json
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
```

## Java后端整合

该模块提供RESTful API接口，Java后端可以通过Feign客户端调用这些接口：

```java
@FeignClient(value = "python-learning-path", url = "http://localhost:5001")
public interface PythonLearningPathClient {
    @PostMapping("/api/recommend/learning-path")
    AjaxResult recommendLearningPath(@RequestBody LearningPathRequest request);
}
```

## 性能优化

1. **缓存机制**：使用Redis缓存学习路径、技能数据和资源推荐结果
2. **算法优化**：使用A*算法和Dijkstra算法进行路径规划，支持并行计算
3. **数据预处理**：预处理技能依赖关系和资源特征，加速推荐过程

## 监控与日志

- 使用Prometheus和Grafana监控服务指标
- 使用ELK Stack收集和分析日志
- 跟踪用户的学习路径完成率和资源评价

## 扩展建议

1. **引入图神经网络**：使用GNN增强路径规划和资源推荐
2. **实时推荐**：实现实时推荐功能，根据用户学习行为动态调整推荐
3. **社交学习功能**：结合用户社交网络推荐学习伙伴和学习小组
4. **自适应学习**：根据用户学习进度动态调整学习路径和资源推荐
