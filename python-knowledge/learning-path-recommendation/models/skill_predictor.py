# 技能掌握度预测模块

import numpy as np
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from config import get_config
from services.user_service import user_service
from services.skill_service import skill_service
from utils.cache import cache
import json


class SkillPredictor:
    """技能掌握度预测类，用于预测用户学习新技能的成功率"""
    
    def __init__(self):
        """
        初始化技能掌握度预测器
        """
        self.config = get_config()
        self.model = None
        self.feature_columns = None
        self._load_or_train_model()
    
    def _load_or_train_model(self):
        """
        加载或训练技能掌握度预测模型
        """
        # 尝试从缓存加载模型
        model_data = cache.get("skill_predictor_model")
        if model_data:
            try:
                # 这里简化处理，实际应该使用pickle保存和加载模型
                print("从缓存加载技能预测模型")
                return
            except Exception as e:
                print(f"加载模型失败: {str(e)}")
        
        # 训练新模型
        print("开始训练技能预测模型")
        self._train_model()
    
    def _train_model(self):
        """
        训练技能掌握度预测模型
        """
        try:
            # 模拟训练数据 - 实际应用中应该从数据库获取真实数据
            # # 特征：当前技能水平、技能难度、学习时长、学习频率
            # # 标签：是否成功掌握（0=失败，1=成功）
            # X = np.array([
            #     [3, 1, 10, 3],  # 当前技能水平3，目标技能难度1，学习时长10小时，每周学习3次
            #     [2, 2, 20, 2],
            #     [4, 1, 8, 4],
            #     [1, 3, 30, 1],
            #     [3, 2, 15, 3],
            #     [2, 1, 12, 2],
            #     [4, 3, 25, 4],
            #     [3, 1, 8, 3],
            #     [2, 3, 28, 2],
            #     [4, 2, 18, 4]
            # ])
            # 
            # y = np.array([1, 1, 1, 0, 1, 1, 1, 1, 0, 1])  # 标签
            # 
            # # 划分训练集和测试集
            # X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
            # 
            # # 根据配置选择模型
            # if self.config.SKILL_PREDICTION_MODEL == "random_forest":
            #     self.model = RandomForestClassifier(n_estimators=100, random_state=42)
            # else:
            #     self.model = DecisionTreeClassifier(random_state=42)
            # 
            # # 训练模型
            # self.model.fit(X_train, y_train)
            # 
            # # 评估模型
            # y_pred = self.model.predict(X_test)
            # accuracy = accuracy_score(y_test, y_pred)
            # print(f"模型训练完成，准确率: {accuracy:.2f}")
            
            # 定义特征列
            self.feature_columns = ["current_level", "skill_difficulty", "learning_hours", "learning_frequency"]
            
            # 保存模型到缓存
            # 实际应用中应该使用pickle保存模型
            cache.set("skill_predictor_model", {"trained": True})
            
        except Exception as e:
            print(f"训练技能预测模型失败: {str(e)}")
            # 如果训练失败，使用默认的简单预测逻辑
            self.model = None
    
    def predict_mastery(self, user_id, skill_id):
        """
        预测用户掌握特定技能的成功率
        :param user_id: 用户ID
        :param skill_id: 技能ID
        :return: 掌握成功率（0-1之间的浮点数）
        """
        try:
            # 获取用户当前技能水平
            user_skills = user_service.get_user_skills(user_id)
            if not user_skills:
                return 0.5  # 默认50%成功率
            
            # 获取目标技能信息
            target_skill = skill_service.get_skill_by_id(skill_id)
            if not target_skill:
                return 0.5
            
            # 模拟特征数据 - 实际应用中应该从用户历史学习数据中提取
            current_level = max(user_skills.get("skillLevels", {}).values()) if user_skills.get("skillLevels") else 2
            skill_difficulty = self._map_difficulty_to_numeric(target_skill.get("difficulty", "中级"))
            learning_hours = self._estimate_learning_hours(skill_difficulty)
            learning_frequency = user_skills.get("learningFrequency", 2)  # 每周学习次数
            
            # 如果有训练好的模型，使用模型预测
            if self.model:
                features = np.array([[current_level, skill_difficulty, learning_hours, learning_frequency]])
                probability = self.model.predict_proba(features)[0][1]
                return round(probability, 2)
            else:
                # 使用简单的基于规则的预测
                return self._rule_based_prediction(current_level, skill_difficulty)
                
        except Exception as e:
            print(f"预测技能掌握度失败: {str(e)}")
            return 0.5
    
    def predict_learning_path_success(self, user_id, learning_path):
        """
        预测用户完成整个学习路径的成功率
        :param user_id: 用户ID
        :param learning_path: 学习路径
        :return: 完成成功率（0-1之间的浮点数）
        """
        try:
            if not learning_path or not learning_path.get("path"):
                return 0.0
            
            total_probability = 1.0
            skill_count = len(learning_path["path"])
            
            # 计算路径中所有技能的平均掌握概率
            for skill in learning_path["path"]:
                probability = self.predict_mastery(user_id, skill["skillId"])
                total_probability *= probability
            
            # 考虑路径长度的影响（路径越长，完成概率越低）
            path_length_factor = 1.0 / (1.0 + 0.1 * (skill_count - 1))
            
            # 综合考虑所有因素的成功率
            overall_probability = total_probability ** (1.0 / skill_count) * path_length_factor
            
            return round(overall_probability, 2)
            
        except Exception as e:
            print(f"预测学习路径成功率失败: {str(e)}")
            return 0.5
    
    def adjust_path_difficulty(self, user_id, learning_path):
        """
        根据用户情况调整学习路径的难度
        :param user_id: 用户ID
        :param learning_path: 学习路径
        :return: 调整后的学习路径
        """
        try:
            if not learning_path or not learning_path.get("path"):
                return learning_path
            
            adjusted_path = learning_path.copy()
            path = adjusted_path["path"]
            
            # 预测路径成功率
            success_rate = self.predict_learning_path_success(user_id, learning_path)
            
            print(f"原始路径成功率: {success_rate:.2f}")
            
            # 如果成功率过低，调整路径难度
            if success_rate < self.config.MIN_LEARNING_SUCCESS_RATE:
                print("调整路径难度以提高成功率")
                adjusted_path = self._decrease_path_difficulty(path)
            elif success_rate > self.config.MAX_LEARNING_SUCCESS_RATE:
                print("调整路径难度以增加挑战性")
                adjusted_path = self._increase_path_difficulty(path)
            
            return adjusted_path
            
        except Exception as e:
            print(f"调整路径难度失败: {str(e)}")
            return learning_path
    
    def _decrease_path_difficulty(self, path):
        """
        降低学习路径难度
        :param path: 学习路径
        :return: 调整后的路径
        """
        adjusted_path = []
        
        for skill in path:
            # 降低技能难度要求 - 实际应用中应该根据技能图谱找到更简单的替代技能
            # 这里简化处理，只添加难度降低的标记
            skill_copy = skill.copy()
            skill_copy["adjustedDifficulty"] = "降低"
            adjusted_path.append(skill_copy)
        
        return {"path": adjusted_path}
    
    def _increase_path_difficulty(self, path):
        """
        增加学习路径难度
        :param path: 学习路径
        :return: 调整后的路径
        """
        adjusted_path = []
        
        for skill in path:
            # 增加技能难度要求 - 实际应用中应该根据技能图谱找到更复杂的扩展技能
            # 这里简化处理，只添加难度增加的标记
            skill_copy = skill.copy()
            skill_copy["adjustedDifficulty"] = "增加"
            adjusted_path.append(skill_copy)
        
        return {"path": adjusted_path}
    
    def _map_difficulty_to_numeric(self, difficulty):
        """
        将难度字符串映射为数值
        :param difficulty: 难度字符串（如："初级", "中级", "高级"）
        :return: 数值表示的难度
        """
        difficulty_map = {
            "初级": 1,
            "中级": 2,
            "高级": 3,
            "专家级": 4
        }
        return difficulty_map.get(difficulty, 2)  # 默认中级
    
    def _estimate_learning_hours(self, difficulty):
        """
        估计学习特定难度技能所需的时长
        :param difficulty: 技能难度（数值）
        :return: 估计的学习时长（小时）
        """
        # 简单的线性估计：难度1需要10小时，每增加1难度增加10小时
        return difficulty * 10
    
    def _rule_based_prediction(self, current_level, skill_difficulty):
        """
        基于规则的简单预测
        :param current_level: 当前技能水平
        :param skill_difficulty: 目标技能难度
        :return: 掌握成功率
        """
        # 如果当前水平 >= 技能难度，成功率高
        if current_level >= skill_difficulty:
            return 0.8
        # 如果当前水平 + 1 >= 技能难度，成功率中等
        elif current_level + 1 >= skill_difficulty:
            return 0.6
        # 否则成功率较低
        else:
            return 0.3


# 创建全局技能预测器实例
skill_predictor = SkillPredictor()