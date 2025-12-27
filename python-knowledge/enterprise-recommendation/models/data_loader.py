# 数据加载模块

from services.enterprise_service import EnterpriseService
from services.position_service import PositionService
from services.tag_service import TagService
from config import get_config

# 获取配置
config = get_config()


class DataLoader:
    """数据加载类"""
    
    def __init__(self):
        """初始化数据加载器"""
        self.enterprises = []
        self.positions = []
        self.tags = set()
        self.entity_tag_mappings = []
    
    def load_all_data(self):
        """
        加载所有数据（企业、职位、标签）
        :return: 加载状态
        """
        try:
            # 加载所有企业
            self.load_enterprises()
            
            # 加载所有职位
            self.load_positions()
            
            # 加载标签映射
            self.load_tag_mappings()
            
            return True
        except Exception as e:
            print(f"加载数据失败: {str(e)}")
            return False
    
    def load_enterprises(self):
        """
        加载所有企业数据
        """
        print("开始加载企业数据...")
        self.enterprises = EnterpriseService.get_all_enterprises()
        print(f"企业数据加载完成，共 {len(self.enterprises)} 家企业")
    
    def load_positions(self):
        """
        加载所有职位数据
        """
        print("开始加载职位数据...")
        
        # 从所有企业中获取职位
        all_positions = []
        for enterprise in self.enterprises:
            enterprise_id = enterprise.get("enterpriseId")
            if enterprise_id:
                positions = PositionService.get_positions_by_enterprise(enterprise_id)
                # 为每个职位添加企业信息
                for position in positions:
                    position["enterpriseId"] = enterprise_id
                    position["enterpriseName"] = enterprise.get("enterpriseName")
                all_positions.extend(positions)
        
        self.positions = all_positions
        print(f"职位数据加载完成，共 {len(self.positions)} 个职位")
    
    def load_tag_mappings(self):
        """
        加载标签映射关系
        """
        print("开始加载标签映射...")
        
        self.entity_tag_mappings = []
        self.tags = set()
        
        # 加载企业标签映射
        for enterprise in self.enterprises:
            enterprise_id = enterprise.get("enterpriseId")
            if enterprise_id:
                tags = TagService.get_enterprise_tags(enterprise_id)
                for tag in tags:
                    self.tags.add(tag)
                    self.entity_tag_mappings.append({
                        "entityType": "enterprise",
                        "entityId": enterprise_id,
                        "tag": tag
                    })
        
        # 加载职位标签映射
        for position in self.positions:
            position_id = position.get("positionId")
            if position_id:
                tags = TagService.get_position_tags(position_id)
                for tag in tags:
                    self.tags.add(tag)
                    self.entity_tag_mappings.append({
                        "entityType": "position",
                        "entityId": position_id,
                        "tag": tag
                    })
        
        print(f"标签映射加载完成，共 {len(self.tags)} 个标签，{len(self.entity_tag_mappings)} 条映射关系")
    
    def get_enterprises(self):
        """
        获取加载的企业数据
        :return: 企业数据列表
        """
        return self.enterprises
    
    def get_positions(self):
        """
        获取加载的职位数据
        :return: 职位数据列表
        """
        return self.positions
    
    def get_tags(self):
        """
        获取加载的标签数据
        :return: 标签集合
        """
        return self.tags
    
    def get_entity_tag_mappings(self):
        """
        获取加载的实体标签映射关系
        :return: 实体标签映射关系列表
        """
        return self.entity_tag_mappings
    
    def get_enterprise_data(self, enterprise_id):
        """
        获取指定企业的完整数据（包含职位和标签）
        :param enterprise_id: 企业ID
        :return: 企业完整数据
        """
        enterprise = EnterpriseService.get_enterprise_with_tags(enterprise_id)
        if not enterprise:
            return None
        
        # 获取企业的职位及其标签
        positions = PositionService.get_enterprise_positions_with_tags(enterprise_id)
        enterprise["positions"] = positions
        
        return enterprise
    
    def clean_data(self):
        """
        数据清洗
        :return: 清洗后的数据
        """
        print("开始数据清洗...")
        
        # 清洗企业数据
        cleaned_enterprises = []
        for enterprise in self.enterprises:
            if self._validate_enterprise(enterprise):
                cleaned_enterprises.append(enterprise)
        self.enterprises = cleaned_enterprises
        
        # 清洗职位数据
        cleaned_positions = []
        for position in self.positions:
            if self._validate_position(position):
                cleaned_positions.append(position)
        self.positions = cleaned_positions
        
        print(f"数据清洗完成，剩余 {len(self.enterprises)} 家企业，{len(self.positions)} 个职位")
    
    def _validate_enterprise(self, enterprise):
        """
        验证企业数据有效性
        :param enterprise: 企业数据
        :return: 是否有效
        """
        return bool(
            enterprise and 
            enterprise.get("enterpriseId") and 
            enterprise.get("enterpriseName")
        )
    
    def _validate_position(self, position):
        """
        验证职位数据有效性
        :param position: 职位数据
        :return: 是否有效
        """
        return bool(
            position and 
            position.get("positionId") and 
            position.get("positionName") and
            position.get("enterpriseId")
        )
    
    def get_statistics(self):
        """
        获取数据统计信息
        :return: 统计信息字典
        """
        return {
            "enterprises_count": len(self.enterprises),
            "positions_count": len(self.positions),
            "tags_count": len(self.tags),
            "entity_tag_mappings_count": len(self.entity_tag_mappings)
        }
    
    def reload_data(self):
        """
        重新加载所有数据
        :return: 加载状态
        """
        # 清空现有数据
        self.enterprises = []
        self.positions = []
        self.tags = set()
        self.entity_tag_mappings = []
        
        # 重新加载
        return self.load_all_data()
    



# 数据加载器实例
data_loader = DataLoader()