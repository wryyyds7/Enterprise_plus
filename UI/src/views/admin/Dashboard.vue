<template>
  <div class="admin-dashboard">
    <!-- 页面内容 -->
    <div class="page-content">
      <div class="page-header">
        <h2>仪表盘</h2>
        <div class="header-info">
          <span class="current-date">{{ currentDate }}</span>
          <span class="system-status" :class="{ 'status-online': isOnline, 'status-offline': !isOnline }">
            <el-icon><CircleCheckFilled v-if="isOnline" /><CircleCloseFilled v-else /></el-icon>
            {{ isOnline ? '系统运行正常' : '系统维护中' }}
          </span>
        </div>
      </div>
      
      <div class="stats-grid">
        <!-- 登录流量统计 -->
        <el-card shadow="hover" class="stat-card" @click="goToStatistics('loginFlow')">
          <div class="stat-header">
            <div class="stat-title">今日登录流量</div>
            <el-tooltip content="查看详细数据" placement="top">
              <el-icon class="stat-more"><ArrowRight /></el-icon>
            </el-tooltip>
          </div>
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ statistics.loginFlow }}</div>
              <div class="stat-trend" :class="{ 'trend-up': loginFlowTrend > 0, 'trend-down': loginFlowTrend < 0 }">
                <el-icon v-if="loginFlowTrend > 0"><ArrowUp /></el-icon>
                <el-icon v-else-if="loginFlowTrend < 0"><ArrowDown /></el-icon>
                <span>{{ Math.abs(loginFlowTrend) }}%</span>
              </div>
            </div>
            <div class="stat-icon stat-icon-primary">
              <el-icon><Monitor /></el-icon>
            </div>
          </div>
          <el-progress :percentage="75" :stroke-width="6" :text-inside="true" status="success" class="stat-progress" />
          <div class="stat-desc">较昨日增长12%</div>
        </el-card>
        
        <!-- 用户统计 -->
        <el-card shadow="hover" class="stat-card" @click="goToStatistics('users')">
          <div class="stat-header">
            <div class="stat-title">用户总数</div>
            <el-tooltip content="查看详细数据" placement="top">
              <el-icon class="stat-more"><ArrowRight /></el-icon>
            </el-tooltip>
          </div>
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ statistics.users }}</div>
              <div class="stat-trend" :class="{ 'trend-up': usersTrend > 0, 'trend-down': usersTrend < 0 }">
                <el-icon v-if="usersTrend > 0"><ArrowUp /></el-icon>
                <el-icon v-else-if="usersTrend < 0"><ArrowDown /></el-icon>
                <span>{{ Math.abs(usersTrend) }}%</span>
              </div>
            </div>
            <div class="stat-icon stat-icon-success">
              <el-icon><User /></el-icon>
            </div>
          </div>
          <el-progress :percentage="80" :stroke-width="6" :text-inside="true" status="primary" class="stat-progress" />
          <div class="stat-desc">活跃用户: {{ activeUsers }}</div>
        </el-card>
        
        <!-- 企业统计 -->
        <el-card shadow="hover" class="stat-card" @click="goToStatistics('enterprises')">
          <div class="stat-header">
            <div class="stat-title">企业总数</div>
            <el-tooltip content="查看详细数据" placement="top">
              <el-icon class="stat-more"><ArrowRight /></el-icon>
            </el-tooltip>
          </div>
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ statistics.enterprises }}</div>
              <div class="stat-trend" :class="{ 'trend-up': enterprisesTrend > 0, 'trend-down': enterprisesTrend < 0 }">
                <el-icon v-if="enterprisesTrend > 0"><ArrowUp /></el-icon>
                <el-icon v-else-if="enterprisesTrend < 0"><ArrowDown /></el-icon>
                <span>{{ Math.abs(enterprisesTrend) }}%</span>
              </div>
            </div>
            <div class="stat-icon stat-icon-warning">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
          </div>
          <el-progress :percentage="65" :stroke-width="6" :text-inside="true" status="warning" class="stat-progress" />
          <div class="stat-desc">新增企业: {{ newEnterprises }}</div>
        </el-card>
        
        <!-- 职位统计 -->
        <el-card shadow="hover" class="stat-card" @click="goToStatistics('positions')">
          <div class="stat-header">
            <div class="stat-title">职位总数</div>
            <el-tooltip content="查看详细数据" placement="top">
              <el-icon class="stat-more"><ArrowRight /></el-icon>
            </el-tooltip>
          </div>
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ statistics.positions }}</div>
              <div class="stat-trend" :class="{ 'trend-up': positionsTrend > 0, 'trend-down': positionsTrend < 0 }">
                <el-icon v-if="positionsTrend > 0"><ArrowUp /></el-icon>
                <el-icon v-else-if="positionsTrend < 0"><ArrowDown /></el-icon>
                <span>{{ Math.abs(positionsTrend) }}%</span>
              </div>
            </div>
            <div class="stat-icon stat-icon-info">
              <el-icon><Briefcase /></el-icon>
            </div>
          </div>
          <el-progress :percentage="70" :stroke-width="6" :text-inside="true" status="info" class="stat-progress" />
          <div class="stat-desc">今日新增: {{ newPositions }}</div>
        </el-card>
        
        <!-- 活动统计 -->
        <el-card shadow="hover" class="stat-card" @click="goToStatistics('events')">
          <div class="stat-header">
            <div class="stat-title">活动总数</div>
            <el-tooltip content="查看详细数据" placement="top">
              <el-icon class="stat-more"><ArrowRight /></el-icon>
            </el-tooltip>
          </div>
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ statistics.events }}</div>
              <div class="stat-trend" :class="{ 'trend-up': eventsTrend > 0, 'trend-down': eventsTrend < 0 }">
                <el-icon v-if="eventsTrend > 0"><ArrowUp /></el-icon>
                <el-icon v-else-if="eventsTrend < 0"><ArrowDown /></el-icon>
                <span>{{ Math.abs(eventsTrend) }}%</span>
              </div>
            </div>
            <div class="stat-icon stat-icon-success">
              <el-icon><Calendar /></el-icon>
            </div>
          </div>
          <el-progress :percentage="55" :stroke-width="6" :text-inside="true" status="success" class="stat-progress" />
          <div class="stat-desc">即将开始: {{ upcomingEvents }}</div>
        </el-card>
        
        <!-- 收藏统计 -->
        <el-card shadow="hover" class="stat-card" @click="goToStatistics('favorites')">
          <div class="stat-header">
            <div class="stat-title">收藏总数</div>
            <el-tooltip content="查看详细数据" placement="top">
              <el-icon class="stat-more"><ArrowRight /></el-icon>
            </el-tooltip>
          </div>
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ statistics.favorites }}</div>
              <div class="stat-trend" :class="{ 'trend-up': favoritesTrend > 0, 'trend-down': favoritesTrend < 0 }">
                <el-icon v-if="favoritesTrend > 0"><ArrowUp /></el-icon>
                <el-icon v-else-if="favoritesTrend < 0"><ArrowDown /></el-icon>
                <span>{{ Math.abs(favoritesTrend) }}%</span>
              </div>
            </div>
            <div class="stat-icon stat-icon-primary">
              <el-icon><StarFilled /></el-icon>
            </div>
          </div>
          <el-progress :percentage="90" :stroke-width="6" :text-inside="true" status="primary" class="stat-progress" />
          <div class="stat-desc">企业收藏: {{ enterpriseFavorites }}</div>
        </el-card>
        
        <!-- 注册统计 -->
        <el-card shadow="hover" class="stat-card" @click="goToStatistics('registrations')">
          <div class="stat-header">
            <div class="stat-title">今日注册</div>
            <el-tooltip content="查看详细数据" placement="top">
              <el-icon class="stat-more"><ArrowRight /></el-icon>
            </el-tooltip>
          </div>
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ statistics.registrations }}</div>
              <div class="stat-trend" :class="{ 'trend-up': registrationsTrend > 0, 'trend-down': registrationsTrend < 0 }">
                <el-icon v-if="registrationsTrend > 0"><ArrowUp /></el-icon>
                <el-icon v-else-if="registrationsTrend < 0"><ArrowDown /></el-icon>
                <span>{{ Math.abs(registrationsTrend) }}%</span>
              </div>
            </div>
            <div class="stat-icon stat-icon-warning">
              <el-icon><UserFilled /></el-icon>
            </div>
          </div>
          <el-progress :percentage="45" :stroke-width="6" :text-inside="true" status="warning" class="stat-progress" />
          <div class="stat-desc">较昨日增长8%</div>
        </el-card>
      </div>
      
      <div class="dashboard-content">
        <div class="left-panel">
          <el-card shadow="hover" class="panel-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">待办事项</span>
                <el-button type="text" size="small" class="card-more">更多</el-button>
              </div>
            </template>
            <el-timeline>
              <el-timeline-item v-for="item in todos" :key="item.id" :timestamp="item.time" placement="top">
                <div class="todo-item">
                  <h4 class="todo-title">{{ item.title }}</h4>
                  <p class="todo-desc">{{ item.description }}</p>
                  <el-button type="primary" size="small" @click="handleTodo(item)" class="todo-action">
                    {{ item.action }}
                  </el-button>
                </div>
              </el-timeline-item>
            </el-timeline>
          </el-card>
          
          <el-card shadow="hover" class="panel-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">最近活动</span>
                <el-button type="text" size="small" class="card-more">更多</el-button>
              </div>
            </template>
            <div class="recent-events">
              <div class="event-item" v-for="event in recentEvents" :key="event.id">
                <div class="event-icon">
                  <el-icon :class="event.type === 'success' ? 'event-icon-success' : event.type === 'warning' ? 'event-icon-warning' : event.type === 'info' ? 'event-icon-info' : 'event-icon-primary'">
                    <Check v-if="event.type === 'success'" /><Warning v-if="event.type === 'warning'" /><InfoFilled v-if="event.type === 'info'" /><Bell v-else /></el-icon>
                </div>
                <div class="event-info">
                  <div class="event-title">{{ event.title }}</div>
                  <div class="event-time">{{ event.time }}</div>
                </div>
                <el-tag :type="event.type" size="small" class="event-status">{{ event.status }}</el-tag>
              </div>
            </div>
          </el-card>
        </div>
        
        <div class="right-panel">
          <el-card shadow="hover" class="panel-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">用户活跃度趋势</span>
                <el-button type="text" size="small" @click="goToStatistics('users')" class="card-more">查看详情</el-button>
              </div>
            </template>
            <div class="chart-container">
              <div class="chart-placeholder">
                <el-icon class="chart-icon"><Line /></el-icon>
                <span class="chart-text">图表加载中...</span>
                <el-button type="text" size="small" @click="loadChart" class="chart-reload">重新加载</el-button>
              </div>
            </div>
          </el-card>
          
          <el-card shadow="hover" class="panel-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">快捷操作</span>
              </div>
            </template>
            <div class="quick-actions">
              <el-button 
                v-for="action in quickActions" 
                :key="action.name" 
                type="primary" 
                :icon="action.icon" 
                @click="handleQuickAction(action)"
                class="quick-action-btn"
              >
                {{ action.name }}
              </el-button>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Monitor, User, OfficeBuilding, Briefcase, Calendar, StarFilled, UserFilled,
  ArrowRight, ArrowUp, ArrowDown, CircleCheckFilled, CircleCloseFilled,
  Check, Warning, InfoFilled, Bell, Plus, Document
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import adminApi from '@/api/modules/admin'

const router = useRouter()

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
})

// 系统状态
const isOnline = ref(true)

// 统计数据
const statistics = ref({
  loginFlow: 0,
  users: 0,
  enterprises: 0,
  positions: 0,
  events: 0,
  favorites: 0,
  registrations: 0
})

// 统计趋势数据
const loginFlowTrend = ref(12)
const usersTrend = ref(8)
const enterprisesTrend = ref(5)
const positionsTrend = ref(15)
const eventsTrend = ref(20)
const favoritesTrend = ref(18)
const registrationsTrend = ref(8)

// 衍生数据
const activeUsers = ref(0)
const newEnterprises = ref(0)
const newPositions = ref(0)
const upcomingEvents = ref(0)
const enterpriseFavorites = ref(0)

// 待办事项
const todos = ref([
  { id: 1, title: '审核新企业注册', description: '有20家新企业等待审核', time: '2小时前', action: '立即审核' },
  { id: 2, title: '处理用户投诉', description: '有5条用户投诉需要处理', time: '5小时前', action: '查看投诉' },
  { id: 3, title: '发布新活动', description: '秋季招聘会需要发布', time: '1天前', action: '发布活动' }
])

// 最近活动
const recentEvents = ref([
  { id: 1, title: '企业A注册成功', time: '刚刚', type: 'success', status: '完成' },
  { id: 2, title: '用户B报名活动', time: '10分钟前', type: 'primary', status: '进行中' },
  { id: 3, title: '企业C信息更新', time: '30分钟前', type: 'info', status: '更新' },
  { id: 4, title: '活动D报名截止', time: '1小时前', type: 'warning', status: '截止' }
])

// 快捷操作
const quickActions = ref([
  { name: '添加企业', icon: Plus, path: '/admin/enterprises' },
  { name: '发布活动', icon: Calendar, path: '/admin/content-management' },
  { name: '审核用户', icon: User, path: '/admin/users' },
  { name: '查看报表', icon: Document, path: '/admin/statistics' },
  { name: '新增管理员', icon: UserFilled, path: '/admin/register' },
  { name: '事件管理', icon: Bell, path: '/admin/event-management' }
])

// 跳转到统计页面
const goToStatistics = (type) => {
  router.push({ path: '/admin/statistics', query: { type } })
}

// 处理待办事项
const handleTodo = (todo) => {
  if (todo.title.includes('审核新企业')) {
    router.push('/admin/enterprise-management')
  } else if (todo.title.includes('处理用户投诉')) {
    ElMessage.info('用户投诉处理功能开发中...')
  } else if (todo.title.includes('发布新活动')) {
    router.push('/admin/content-management')
  }
}

// 处理快捷操作
const handleQuickAction = (action) => {
  router.push(action.path)
}

// 加载图表
const loadChart = () => {
  ElMessage.info('图表加载中...')
  // 这里可以添加实际的图表加载逻辑
  setTimeout(() => {
    ElMessage.success('图表加载成功')
  }, 1000)
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    // 获取登录流量统计
    const loginFlowResult = await adminApi.getLoginFlowStatistics()
    if (loginFlowResult.code === 200) {
      statistics.value.loginFlow = loginFlowResult.data
    }
    
    // 获取用户统计
    const userResult = await adminApi.getUserStatistics()
    if (userResult.code === 200) {
      statistics.value.users = userResult.data.total
      activeUsers.value = userResult.data.active
    }
    
    // 获取企业统计
    const enterpriseResult = await adminApi.getEnterpriseStatistics()
    if (enterpriseResult.code === 200) {
      statistics.value.enterprises = enterpriseResult.data.total
      newEnterprises.value = enterpriseResult.data.new
    }
    
    // 获取职位统计
    const positionResult = await adminApi.getPositionStatistics()
    if (positionResult.code === 200) {
      statistics.value.positions = positionResult.data.total
      newPositions.value = positionResult.data.new
    }
    
    // 获取活动统计
    const eventResult = await adminApi.getEventStatistics()
    if (eventResult.code === 200) {
      statistics.value.events = eventResult.data.total
      upcomingEvents.value = eventResult.data.upcoming
    }
    
    // 获取收藏统计
    const favoriteResult = await adminApi.getFavoriteStatistics()
    if (favoriteResult.code === 200) {
      statistics.value.favorites = favoriteResult.data.total
      enterpriseFavorites.value = favoriteResult.data.enterprise
    }
    
    // 获取注册统计
    const registrationResult = await adminApi.getRegistrationStatistics()
    if (registrationResult.code === 200) {
      statistics.value.registrations = registrationResult.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

// 组件挂载时获取统计数据
onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 0;
  margin: 0;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-content {
  padding: 20px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.current-date {
  color: #666;
  font-size: 14px;
}

.system-status {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
}

.system-status.status-online {
  background-color: #f0f9ff;
  color: #409eff;
}

.system-status.status-offline {
  background-color: #fef0f0;
  color: #f56c6c;
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.12);
}

/* 统计卡片头部 */
.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.stat-title {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.stat-more {
  font-size: 12px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s ease;
}

.stat-more:hover {
  color: #409eff;
}

/* 统计卡片内容 */
.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  line-height: 1.2;
  margin-bottom: 8px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.stat-trend.trend-up {
  color: #67c23a;
}

.stat-trend.trend-down {
  color: #f56c6c;
}

.stat-trend .el-icon {
  font-size: 10px;
}

/* 统计卡片图标 */
.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon-primary {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.stat-icon-success {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.stat-icon-warning {
  background-color: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.stat-icon-info {
  background-color: rgba(144, 147, 153, 0.1);
  color: #909399;
}

/* 统计卡片进度条 */
.stat-progress {
  margin-bottom: 8px;
}

.stat-desc {
  font-size: 12px;
  color: #999;
  text-align: right;
}

/* 仪表盘内容布局 */
.dashboard-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 25px;
}

/* 面板卡片 */
.panel-card {
  margin-bottom: 25px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
}

.panel-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #fafafa;
  border-bottom: 1px solid #e0e0e0;
}

.card-title {
  font-weight: 600;
  color: #333;
  font-size: 16px;
}

.card-more {
  font-size: 13px;
  color: #999;
  padding: 0;
  height: auto;
}

.card-more:hover {
  color: #409eff;
}

/* 待办事项 */
:deep(.el-timeline-item__timestamp) {
  font-size: 12px;
  color: #999;
}

.todo-item {
  padding: 15px;
  background-color: #fafafa;
  border-radius: 6px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.todo-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.todo-desc {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.todo-action {
  font-size: 12px;
  padding: 4px 12px;
}

/* 最近活动 */
.recent-events {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 10px;
}

.event-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
  background-color: #fafafa;
}

.event-item:hover {
  background-color: #f0f9ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
}

.event-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 16px;
}

.event-icon-success {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.event-icon-warning {
  background-color: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.event-icon-info {
  background-color: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.event-icon-primary {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.event-info {
  flex: 1;
}

.event-title {
  font-weight: 500;
  color: #333;
  font-size: 14px;
  margin-bottom: 4px;
}

.event-time {
  font-size: 12px;
  color: #999;
}

.event-status {
  flex-shrink: 0;
}

/* 图表容器 */
.chart-container {
  height: 280px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fafafa;
  border-radius: 4px;
  margin: 10px;
}

.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: #999;
}

.chart-icon {
  font-size: 64px;
  opacity: 0.3;
}

.chart-text {
  font-size: 14px;
  margin-bottom: 5px;
}

.chart-reload {
  font-size: 12px;
  padding: 4px 12px;
  color: #409eff;
}

/* 快捷操作 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  padding: 15px;
}

.quick-action-btn {
  height: 50px;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.quick-action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 15px;
  }
  
  .dashboard-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .quick-actions {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-info {
    width: 100%;
    justify-content: space-between;
  }
  
  .stats-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 15px;
  }
  
  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-content {
    padding: 15px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 12px;
  }
  
  .stat-number {
    font-size: 28px;
  }
  
  .stat-icon {
    width: 45px;
    height: 45px;
    font-size: 20px;
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
  
  .chart-container {
    height: 250px;
  }
  
  .chart-icon {
    font-size: 56px;
  }
}

@media (max-width: 576px) {
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .stat-card {
    min-height: 180px;
  }
}
</style>