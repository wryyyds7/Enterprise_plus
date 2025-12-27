<template>
  <div class="statistics">
    <div class="page-header">
      <h2>数据统计</h2>
      <p>平台运营数据统计分析</p>
    </div>

    <!-- 统计筛选 -->
    <el-card shadow="never" class="filter-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="统计时间">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadStatisticsData">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetFilter">
            <el-icon><RefreshRight /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 关键指标卡片 -->
    <div class="statistics-cards">
      <el-card shadow="hover" class="stat-card">
        <template #header>
          <div class="card-header">
            <span>总用户数</span>
            <el-tag type="success">今日新增{{ stats.userStats.todayNew }}</el-tag>
          </div>
        </template>
        <div class="stat-content">
          <div class="stat-number">{{ stats.userStats.total }}</div>
          <div class="stat-desc">平台注册用户总数</div>
        </div>
      </el-card>

      <el-card shadow="hover" class="stat-card">
        <template #header>
          <div class="card-header">
            <span>活跃用户数</span>
            <el-tag type="primary">今日活跃{{ stats.activityStats.todayActive }}</el-tag>
          </div>
        </template>
        <div class="stat-content">
          <div class="stat-number">{{ stats.activityStats.activeUsers }}</div>
          <div class="stat-desc">最近7天活跃用户数</div>
        </div>
      </el-card>

      <el-card shadow="hover" class="stat-card">
        <template #header>
          <div class="card-header">
            <span>企业数量</span>
            <el-tag type="info">今日新增{{ stats.enterpriseStats.todayNew }}</el-tag>
          </div>
        </template>
        <div class="stat-content">
          <div class="stat-number">{{ stats.enterpriseStats.total }}</div>
          <div class="stat-desc">已入驻平台企业总数</div>
        </div>
      </el-card>

      <el-card shadow="hover" class="stat-card">
        <template #header>
          <div class="card-header">
            <span>校招活动</span>
            <el-tag type="warning">进行中{{ stats.eventStats.activeEvents }}</el-tag>
          </div>
        </template>
        <div class="stat-content">
          <div class="stat-number">{{ stats.eventStats.total }}</div>
          <div class="stat-desc">已发布校招活动总数</div>
        </div>
      </el-card>
    </div>

    <!-- 统计图表区域 -->
    <div class="charts-container">
      <!-- 用户活跃度统计 -->
      <el-card shadow="never" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>用户活跃度统计</span>
            <el-select v-model="userActivityPeriod" size="small" style="width: 120px">
              <el-option label="最近7天" value="7d" />
              <el-option label="最近30天" value="30d" />
              <el-option label="最近90天" value="90d" />
            </el-select>
          </div>
        </template>
        <div class="chart-content">
          <!-- 这里可以集成图表库，如ECharts或Chart.js -->
          <div class="mock-chart">
            <div class="mock-chart-title">用户活跃趋势图</div>
            <div class="mock-chart-bars">
              <div class="mock-bar" v-for="item in mockUserActivityData" :key="item.date">
                <div class="mock-bar-label">{{ item.date }}</div>
                <div class="mock-bar-value" :style="{ height: `${item.value / 100 * 200}px` }">{{ item.value }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 企业推荐效果统计 -->
      <el-card shadow="never" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>企业推荐效果统计</span>
          </div>
        </template>
        <div class="chart-content">
          <div class="chart-row">
            <div class="chart-col">
              <div class="mock-chart">
                <div class="mock-chart-title">推荐点击转化率</div>
                <div class="mock-pie-chart">
                  <div class="mock-pie-section" :style="{ width: '60%' }"></div>
                  <div class="mock-pie-text">68%</div>
                </div>
              </div>
            </div>
            <div class="chart-col">
              <div class="mock-chart">
                <div class="mock-chart-title">推荐企业类型占比</div>
                <div class="mock-list">
                  <div class="mock-list-item" v-for="item in mockRecommendationTypeData" :key="item.name">
                    <span class="mock-list-label">{{ item.name }}</span>
                    <div class="mock-progress">
                      <div class="mock-progress-bar" :style="{ width: item.percentage }"></div>
                      <span class="mock-progress-text">{{ item.percentage }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 校招活动参与度统计 -->
      <el-card shadow="never" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>校招活动参与度统计</span>
          </div>
        </template>
        <div class="chart-content">
          <div class="mock-chart">
            <div class="mock-chart-title">活动报名人数趋势</div>
            <div class="mock-chart-line">
              <div class="mock-line-point" v-for="(point, index) in mockEventParticipationData" :key="index"></div>
            </div>
            <div class="mock-chart-axis">
              <div class="mock-axis-label" v-for="(label, index) in ['10/1', '10/15', '11/1', '11/15', '12/1']" :key="index">{{ label }}</div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 平台运营数据统计 -->
      <el-card shadow="never" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>平台运营数据统计</span>
          </div>
        </template>
        <div class="chart-content">
          <el-table :data="stats.operationStats" style="width: 100%" stripe>
            <el-table-column prop="indicator" label="统计指标" width="200" />
            <el-table-column prop="currentValue" label="当前值" width="150" />
            <el-table-column prop="previousValue" label="上期值" width="150" />
            <el-table-column prop="growthRate" label="增长率" width="150">
              <template #default="scope">
                <el-tag :type="scope.row.growthRate > 0 ? 'success' : 'danger'">
                  {{ scope.row.growthRate > 0 ? '+' : '' }}{{ scope.row.growthRate }}%
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="指标说明" />
          </el-table>
        </div>
      </el-card>
    </div>

    <!-- 统计详情 -->
    <el-card shadow="never" class="detail-card">
      <template #header>
        <div class="card-header">
          <span>统计详情</span>
        </div>
      </template>
      <el-tabs v-model="activeDetailTab">
        <el-tab-pane label="用户分布" name="userDistribution">
          <div class="detail-content">
            <div class="distribution-grid">
              <div class="distribution-item">
                <h3>用户类型分布</h3>
                <div class="mock-pie-chart distribution-chart">
                  <div class="mock-pie-section student" :style="{ width: '75%' }"></div>
                  <div class="mock-pie-section enterprise" :style="{ width: '25%' }"></div>
                  <div class="mock-pie-legend">
                    <div class="legend-item">
                      <span class="legend-color student"></span>
                      <span>学生用户: 75%</span>
                    </div>
                    <div class="legend-item">
                      <span class="legend-color enterprise"></span>
                      <span>企业用户: 25%</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="distribution-item">
                <h3>用户专业分布</h3>
                <div class="mock-list">
                  <div class="mock-list-item" v-for="item in mockMajorDistribution" :key="item.major">
                    <span class="mock-list-label">{{ item.major }}</span>
                    <div class="mock-progress">
                      <div class="mock-progress-bar" :style="{ width: item.percentage }"></div>
                      <span class="mock-progress-text">{{ item.percentage }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="企业分布" name="enterpriseDistribution">
          <div class="detail-content">
            <div class="distribution-grid">
              <div class="distribution-item">
                <h3>企业行业分布</h3>
                <div class="mock-pie-chart distribution-chart">
                  <div class="mock-pie-section tech" :style="{ width: '40%' }"></div>
                  <div class="mock-pie-section finance" :style="{ width: '30%' }"></div>
                  <div class="mock-pie-section manufacturing" :style="{ width: '20%' }"></div>
                  <div class="mock-pie-section other" :style="{ width: '10%' }"></div>
                  <div class="mock-pie-legend">
                    <div class="legend-item">
                      <span class="legend-color tech"></span>
                      <span>科技行业: 40%</span>
                    </div>
                    <div class="legend-item">
                      <span class="legend-color finance"></span>
                      <span>金融行业: 30%</span>
                    </div>
                    <div class="legend-item">
                      <span class="legend-color manufacturing"></span>
                      <span>制造业: 20%</span>
                    </div>
                    <div class="legend-item">
                      <span class="legend-color other"></span>
                      <span>其他行业: 10%</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="distribution-item">
                <h3>企业规模分布</h3>
                <el-table :data="mockEnterpriseSizeDistribution" style="width: 100%">
                  <el-table-column prop="size" label="企业规模" />
                  <el-table-column prop="count" label="企业数量" />
                  <el-table-column prop="percentage" label="占比">
                    <template #default="scope">
                      <span>{{ scope.row.percentage }}%</span>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, RefreshRight } from '@element-plus/icons-vue';
import adminApi from '@/api/modules/admin';

// 筛选表单
const filterForm = reactive({
  dateRange: [],
  type: ''
});

// 统计数据
const stats = reactive({
  userStats: {
    total: 12580,
    todayNew: 156,
    active: 3890
  },
  activityStats: {
    todayActive: 3890,
    activeUsers: 8765,
    loginRate: 69.6
  },
  enterpriseStats: {
    total: 258,
    todayNew: 5,
    certified: 189
  },
  eventStats: {
    total: 126,
    activeEvents: 38,
    registeredUsers: 15678
  },
  operationStats: [
    {
      indicator: '平台访问量',
      currentValue: 125800,
      previousValue: 105600,
      growthRate: 19.1,
      description: '平台总访问量'
    },
    {
      indicator: '页面平均停留时长',
      currentValue: '4分35秒',
      previousValue: '3分45秒',
      growthRate: 22.2,
      description: '用户平均页面停留时长'
    },
    {
      indicator: '企业推荐点击量',
      currentValue: 35680,
      previousValue: 28900,
      growthRate: 23.5,
      description: '企业推荐点击总量'
    },
    {
      indicator: '校招活动报名量',
      currentValue: 15678,
      previousValue: 12890,
      growthRate: 21.6,
      description: '校招活动报名总人数'
    }
  ]
});

// 页面状态
const userActivityPeriod = ref('30d');
const activeDetailTab = ref('userDistribution');
const loading = ref(false);

// Mock数据
const mockUserActivityData = [
  { date: '12/1', value: 2560 },
  { date: '12/2', value: 3210 },
  { date: '12/3', value: 2890 },
  { date: '12/4', value: 3560 },
  { date: '12/5', value: 3890 },
  { date: '12/6', value: 4230 },
  { date: '12/7', value: 3980 }
];

const mockRecommendationTypeData = [
  { name: '科技行业', percentage: '45%' },
  { name: '金融行业', percentage: '25%' },
  { name: '制造业', percentage: '15%' },
  { name: '教育行业', percentage: '10%' },
  { name: '其他行业', percentage: '5%' }
];

const mockEventParticipationData = [
  { date: '11/1', value: 1250 },
  { date: '11/5', value: 1890 },
  { date: '11/10', value: 2560 },
  { date: '11/15', value: 2890 },
  { date: '11/20', value: 3210 },
  { date: '11/25', value: 3560 },
  { date: '12/1', value: 3890 }
];

const mockMajorDistribution = [
  { major: '计算机科学', percentage: '35%' },
  { major: '电子工程', percentage: '20%' },
  { major: '工商管理', percentage: '15%' },
  { major: '机械工程', percentage: '12%' },
  { major: '金融', percentage: '10%' },
  { major: '其他专业', percentage: '8%' }
];

const mockEnterpriseSizeDistribution = [
  { size: '1-50人', count: 89, percentage: 34.5 },
  { size: '51-200人', count: 65, percentage: 25.2 },
  { size: '201-500人', count: 45, percentage: 17.4 },
  { size: '501-1000人', count: 32, percentage: 12.4 },
  { size: '1000人以上', count: 27, percentage: 10.5 }
];

// 方法
const handleDateChange = () => {
  // 日期范围变化处理
};

const loadStatisticsData = async () => {
  loading.value = true;
  try {
    // 调用真实API获取统计数据
    const [
      userStatsResponse,
      enterpriseStatsResponse,
      eventStatsResponse,
      loginFlowResponse,
      favoriteStatsResponse,
      registrationStatsResponse
    ] = await Promise.all([
      adminApi.getUserStatistics(),
      adminApi.getEnterpriseStatistics(),
      adminApi.getEventStatistics(),
      adminApi.getLoginFlowStatistics(),
      adminApi.getFavoriteStatistics(),
      adminApi.getRegistrationStatistics()
    ]);
    
    // 更新统计数据
    Object.assign(stats.userStats, {
      total: userStatsResponse.data.totalUserCount,
      todayNew: userStatsResponse.data.newUserCountToday,
      active: userStatsResponse.data.activeUserCount
    });
    
    Object.assign(stats.activityStats, {
      todayActive: userStatsResponse.data.activeUserCount,
      activeUsers: userStatsResponse.data.activeUserCount,
      loginRate: loginFlowResponse.data.loginFlowList[loginFlowResponse.data.loginFlowList.length - 1]?.successLoginCount / loginFlowResponse.data.loginFlowList[loginFlowResponse.data.loginFlowList.length - 1]?.totalLoginCount * 100 || 0
    });
    
    Object.assign(stats.enterpriseStats, {
      total: enterpriseStatsResponse.data.totalEnterpriseCount,
      todayNew: enterpriseStatsResponse.data.newEnterpriseCountToday,
      certified: enterpriseStatsResponse.data.activeEnterpriseCount
    });
    
    Object.assign(stats.eventStats, {
      total: eventStatsResponse.data.totalEventCount,
      activeEvents: eventStatsResponse.data.activeEventCount,
      registeredUsers: eventStatsResponse.data.totalEventCount * 100 // 模拟数据，实际应该从活动报名接口获取
    });
    
    ElMessage.success('统计数据加载成功');
  } catch (error) {
    ElMessage.error('统计数据加载失败');
    console.error('统计数据加载失败:', error);
  } finally {
    loading.value = false;
  }
};

const resetFilter = () => {
  Object.assign(filterForm, { dateRange: [], type: '' });
  loadStatisticsData();
};

// 初始化
onMounted(() => {
  loadStatisticsData();
});
</script>

<style scoped>
.statistics {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 5px 0;
  font-size: 20px;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.filter-card {
  margin-bottom: 20px;
}

.statistics-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-content {
  padding: 20px 0;
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.stat-desc {
  color: #666;
  font-size: 14px;
}

.charts-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  height: 100%;
}

.chart-content {
  padding: 20px 0;
}

.chart-row {
  display: flex;
  gap: 20px;
}

.chart-col {
  flex: 1;
}

.mock-chart {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
  text-align: center;
}

.mock-chart-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.mock-chart-bars {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 250px;
  padding: 20px 0;
}

.mock-bar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  width: 60px;
}

.mock-bar-label {
  font-size: 12px;
  color: #666;
}

.mock-bar-value {
  background-color: #409eff;
  width: 40px;
  border-radius: 4px 4px 0 0;
  display: flex;
  justify-content: center;
  align-items: flex-end;
  padding: 5px;
  color: white;
  font-size: 12px;
}

.mock-pie-chart {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background-color: #e0e0e0;
  margin: 0 auto;
  position: relative;
  overflow: hidden;
}

.mock-pie-section {
  height: 100%;
  background-color: #409eff;
  transition: all 0.3s ease;
}

.mock-pie-section.student {
  background-color: #409eff;
  width: 75%;
}

.mock-pie-section.enterprise {
  background-color: #67c23a;
  width: 25%;
}

.mock-pie-section.tech {
  background-color: #409eff;
  width: 40%;
}

.mock-pie-section.finance {
  background-color: #67c23a;
  width: 30%;
}

.mock-pie-section.manufacturing {
  background-color: #e6a23c;
  width: 20%;
}

.mock-pie-section.other {
  background-color: #f56c6c;
  width: 10%;
}

.mock-pie-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.mock-pie-legend {
  margin-top: 20px;
  text-align: left;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.legend-color.student {
  background-color: #409eff;
}

.legend-color.enterprise {
  background-color: #67c23a;
}

.legend-color.tech {
  background-color: #409eff;
}

.legend-color.finance {
  background-color: #67c23a;
}

.legend-color.manufacturing {
  background-color: #e6a23c;
}

.legend-color.other {
  background-color: #f56c6c;
}

.mock-list {
  padding: 10px;
}

.mock-list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.mock-list-label {
  font-size: 14px;
  color: #333;
  width: 80px;
}

.mock-progress {
  flex: 1;
  height: 20px;
  background-color: #e0e0e0;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  margin: 0 10px;
}

.mock-progress-bar {
  height: 100%;
  background-color: #409eff;
  border-radius: 10px;
}

.mock-progress-text {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 12px;
  color: #333;
}

.mock-chart-line {
  height: 200px;
  position: relative;
  margin: 20px 0;
}

.mock-line-point {
  position: absolute;
  width: 12px;
  height: 12px;
  background-color: #409eff;
  border-radius: 50%;
  top: 50%;
  transform: translateY(-50%);
}

.mock-line-point:nth-child(1) { left: 10%; top: 60%; }
.mock-line-point:nth-child(2) { left: 25%; top: 45%; }
.mock-line-point:nth-child(3) { left: 40%; top: 30%; }
.mock-line-point:nth-child(4) { left: 55%; top: 25%; }
.mock-line-point:nth-child(5) { left: 70%; top: 20%; }
.mock-line-point:nth-child(6) { left: 85%; top: 15%; }

.mock-chart-axis {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
}

.mock-axis-label {
  font-size: 12px;
  color: #666;
}

.detail-card {
  margin-bottom: 20px;
}

.detail-content {
  padding: 20px 0;
}

.distribution-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.distribution-item {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.distribution-item h3 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.distribution-chart {
  margin: 0 auto;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .statistics-cards {
    grid-template-columns: 1fr 1fr;
  }
  
  .distribution-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .statistics-cards {
    grid-template-columns: 1fr;
  }
}
</style>