# 企业推荐知识图谱模块

## 1. 项目简介

本模块是基于知识图谱的企业推荐系统，主要用于为求职者推荐与其技能标签匹配的企业和职位。系统采用Neo4j构建知识图谱，结合基于标签相似度和图遍历的混合推荐算法，为用户提供精准的企业推荐服务。

## 2. 技术栈

### Python端技术栈
- **Web框架**: Flask 2.0+ - 用于构建RESTful API服务
- **知识图谱**: Neo4j 5.0+ - 用于构建和操作图数据结构
- **数据处理**: pandas 1.5+ - 用于数据清洗和处理
- **相似度计算**: scikit-learn 1.1+ - 用于实现余弦相似度等算法
- **HTTP请求**: requests 2.28+ - 用于调用Java后端API获取数据
- **缓存**: Redis 4.0+ - 用于缓存频繁访问的图数据

### Java端整合技术
- **Feign客户端**: Spring Cloud OpenFeign - 用于调用Python服务的RESTful API
- **数据传输**: JSON - 统一的数据交换格式

## 3. 项目结构

```
enterprise-recommendation/
├── app.py                    # 应用入口，Flask服务配置
├── config.py                 # 配置文件
├── models/                   # 模型层
│   ├── graph_builder.py      # 知识图谱构建模块
│   ├── recommendation.py     # 推荐算法模块
│   └── data_loader.py        # 数据加载模块
├── services/                 # 服务层
│   ├── tag_service.py        # 标签服务（调用Java API）
│   ├── enterprise_service.py # 企业数据服务
│   └── position_service.py   # 职位数据服务
├── utils/                    # 工具层
│   ├── similarity.py         # 相似度计算工具
│   ├── cache.py              # 缓存工具
│   └── response.py           # 响应格式化工具
├── tests/                    # 测试目录
├── requirements.txt          # 依赖包列表
└── README.md                 # 项目说明文档
```

## 4. 知识图谱结构

### 节点类型
- **User** (用户) - 属性: userId, name
- **Enterprise** (企业) - 属性: enterpriseId, enterpriseName, website, city, size, industry
- **Position** (职位) - 属性: positionId, positionName, salary, city, description
- **Tag** (标签) - 属性: tagId, name

### 边类型
- **User-[:拥有标签]->Tag** - 用户拥有的技能标签
- **Enterprise-[:属于]->Tag** - 企业所属的行业/类型标签
- **Position-[:需要技能]->Tag** - 职位所需的技能标签
- **Enterprise-[:招聘]->Position** - 企业发布的职位

## 5. 安装和配置

### 5.1 安装依赖

```bash
# 创建虚拟环境（可选但推荐）
python -m venv venv
# 激活虚拟环境
# Windows: venv\Scripts\activate
# Linux/Mac: source venv/bin/activate

# 安装依赖包
pip install -r requirements.txt
```

### 5.2 配置文件

创建 `.env` 文件并配置以下环境变量：

```
# Flask配置
FLASK_HOST=0.0.0.0
FLASK_PORT=5000
DEBUG=True

# Neo4j数据库配置
NEO4J_URI=bolt://localhost:7687
NEO4J_USER=neo4j
NEO4J_PASSWORD=your_password

# Redis缓存配置
REDIS_URL=redis://localhost:6379/0

# Java后端API配置
JAVA_API_BASE_URL=http://localhost:8080
```

## 6. 启动服务

### 6.1 直接启动

```bash
python app.py
```

### 6.2 使用Flask命令启动

```bash
export FLASK_APP=app.py  # Linux/Mac
set FLASK_APP=app.py     # Windows
flask run
```

## 7. API接口

### 7.1 健康检查
- **URL**: `/api/health`
- **Method**: `GET`
- **Description**: 检查服务是否正常运行

### 7.2 构建知识图谱
- **URL**: `/api/graph/build`
- **Method**: `GET`
- **Description**: 从Java后端获取数据并构建知识图谱

### 7.3 更新知识图谱
- **URL**: `/api/graph/update`
- **Method**: `GET`
- **Description**: 更新知识图谱数据

### 7.4 企业推荐
- **URL**: `/api/recommend/enterprise`
- **Method**: `POST`
- **Description**: 根据用户标签推荐企业
- **Request Body**:
  ```json
  {
    "userId": "123",
    "tags": ["软件工程师", "Java", "Spring Boot"],
    "topN": 10,
    "type": "hybrid"  # 可选值: hybrid, tag, graph
  }
  ```
- **Response**:
  ```json
  {
    "code": 200,
    "msg": "success",
    "data": {
      "recommendations": [
        {
          "enterpriseId": 1,
          "enterpriseName": "示例科技公司",
          "relevanceScore": 0.92,
          "matchedTags": ["软件工程师", "Java"],
          "positions": [
            {
              "positionId": 1,
              "positionName": "Java开发工程师",
              "salary": "15k-25k",
              "city": "北京"
            }
          ]
        }
      ],
      "total": 1,
      "request": {
        "userId": "123",
        "tags": ["软件工程师", "Java", "Spring Boot"],
        "topN": 10,
        "type": "hybrid"
      }
    }
  }
  ```

## 8. 推荐算法

### 8.1 基于标签相似度的推荐
- 使用余弦相似度计算用户标签与企业标签的相似度
- 使用Jaccard相似度计算标签交集比例
- 综合两种相似度得分得到推荐结果

### 8.2 基于图遍历的推荐
- 从用户节点出发，通过标签节点遍历到相关的企业和职位节点
- 基于路径长度和节点重要性计算推荐分数

### 8.3 混合推荐策略
- 结合基于标签相似度和基于图遍历的结果
- 为不同推荐类型设置权重，得到最终推荐结果

## 9. Java端整合

### 9.1 创建Feign客户端

```java
@FeignClient(value = "python-recommendation", url = "http://localhost:5000")
public interface PythonRecommendationClient {
    
    @PostMapping("/api/recommend/enterprise")
    AjaxResult recommendEnterprises(@RequestBody UserTagRequest request);
    
    @GetMapping("/api/graph/build")
    AjaxResult buildGraph();
    
    @GetMapping("/api/health")
    AjaxResult healthCheck();
}
```

### 9.2 调用示例

```java
@Autowired
private PythonRecommendationClient recommendationClient;

public List<EnterpriseRecommendation> recommendEnterprises(User user) {
    UserTagRequest request = new UserTagRequest();
    request.setUserId(user.getId());
    request.setTags(user.getTags());
    request.setTopN(10);
    request.setType("hybrid");
    
    AjaxResult result = recommendationClient.recommendEnterprises(request);
    // 处理返回结果
    return parseRecommendations(result);
}
```

## 10. 性能优化

1. **图数据缓存** - 使用Redis缓存构建好的图数据
2. **推荐结果缓存** - 缓存用户的推荐结果
3. **批量处理** - 对数据加载和处理进行批量优化
4. **并发处理** - 使用多线程处理多个推荐请求

## 11. 部署方案

### 11.1 容器化部署

创建 `Dockerfile`：

```dockerfile
FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 5000

CMD ["python", "app.py"]
```

### 11.2 使用docker-compose

```yaml
version: '3'
services:
  enterprise-recommendation:
    build: .
    ports:
      - "5000:5000"
    environment:
      - FLASK_HOST=0.0.0.0
      - FLASK_PORT=5000
      - NEO4J_URI=bolt://neo4j:7687
      - REDIS_URL=redis://redis:6379/0
    depends_on:
      - neo4j
      - redis

  neo4j:
    image: neo4j:5.5.0
    ports:
      - "7474:7474"
      - "7687:7687"
    environment:
      - NEO4J_AUTH=neo4j/your_password
    volumes:
      - neo4j-data:/data

  redis:
    image: redis:7.0.5
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data

volumes:
  neo4j-data:
  redis-data:
```

## 12. 扩展建议

1. **引入深度学习** - 使用Graph Neural Networks (GNN) 提升推荐效果
2. **实时推荐** - 实现实时推荐功能，响应用户行为变化
3. **多维度推荐** - 结合企业地理位置、规模等信息进行推荐
4. **实时更新** - 实现知识图谱的实时更新机制

## 13. 联系和支持

如有问题或建议，请联系项目维护者。

---

**版本**: 1.0.0
**创建日期**: 2024-01-17