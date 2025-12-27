# 补充缺失的pom.xml文件

## 问题描述
四个模块（activity、advertisement、event、forum）缺少pom.xml文件，需要参照其他模块格式补充。

## 解决方案
为每个模块创建符合项目规范的pom.xml文件，基于search模块的配置模板。

## 实施步骤

1. **创建activity模块pom.xml**
   - 文件路径：`d:\bianchenglianxi\java\project\enterprise_plus\activity\pom.xml`
   - 修改artifactId为"activity"
   - 设置合适的name和description

2. **创建advertisement模块pom.xml**
   - 文件路径：`d:\bianchenglianxi\java\project\enterprise_plus\advertisement\pom.xml`
   - 修改artifactId为"advertisement"
   - 设置合适的name和description

3. **创建event模块pom.xml**
   - 文件路径：`d:\bianchenglianxi\java\project\enterprise_plus\event\pom.xml`
   - 修改artifactId为"event"
   - 设置合适的name和description

4. **创建forum模块pom.xml**
   - 文件路径：`d:\bianchenglianxi\java\project\enterprise_plus\forum\pom.xml`
   - 修改artifactId为"forum"
   - 设置合适的name和description

## 配置模板
每个pom.xml文件将包含：
- 与父项目的继承关系
- 正确的groupId、artifactId和version
- 依赖于common模块
- 符合项目的Java版本要求

## 预期结果
四个模块都将有完整的pom.xml配置，能够正常参与项目构建。