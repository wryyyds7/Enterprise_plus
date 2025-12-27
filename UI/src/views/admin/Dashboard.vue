<template>
  <div class="admin-dashboard">
    <NavigationHeader />
    <div class="page-content">
    <h2>管理仪表盘</h2>
    
    <div class="stats-grid">
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalEnterprises }}</div>
            <div class="stat-label">企业总数</div>
          </div>
          <div class="stat-icon">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
        </div>
        <el-progress :percentage="stats.pendingEnterpriseAudit / stats.totalEnterprises * 100" :stroke-width="8" :text-inside="true" status="warning" />
        <div class="stat-desc">待审核企业: {{ stats.pendingEnterpriseAudit }}</div>
      </el-card>
      
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalUsers }}</div>
            <div class="stat-label">用户总数</div>
          </div>
          <div class="stat-icon">
            <el-icon><User /></el-icon>
          </div>
        </div>
        <el-progress :percentage="stats.activeUsers / stats.totalUsers * 100" :stroke-width="8" :text-inside="true" status="success" />
        <div class="stat-desc">活跃用户: {{ stats.activeUsers }}</div>
      </el-card>
      
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalEvents }}</div>
            <div class="stat-label">活动总数</div>
          </div>
          <div class="stat-icon">
            <el-icon><Calendar /></el-icon>
          </div>
        </div>
        <el-progress :percentage="stats.upcomingEvents / stats.totalEvents * 100" :stroke-width="8" :text-inside="true" status="primary" />
        <div class="stat-desc">即将开始: {{ stats.upcomingEvents }}</div>
      </el-card>
      
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalFavorites }}</div>
            <div class="stat-label">收藏总数</div>
          </div>
          <div class="stat-icon">
            <el-icon><StarFilled /></el-icon>
          </div>
        </div>
        <el-progress :percentage="stats.enterpriseFavorites / stats.totalFavorites * 100" :stroke-width="8" :text-inside="true" status="info" />
        <div class="stat-desc">企业收藏: {{ stats.enterpriseFavorites }}</div>
      </el-card>
    </div>
    
    <div class="dashboard-content">
      <div class="left-panel">
        <el-card shadow="hover" class="panel-card">
          <template #header>
            <div class="card-header">
              <span>待办事项</span>
              <el-button type="text" size="small">更多</el-button>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item v-for="item in todos" :key="item.id" :timestamp="item.time">
              <div class="todo-item">
                <h4>{{ item.title }}</h4>
                <p>{{ item.description }}</p>
                <el-button type="primary" size="small" @click="handleTodo(item)">
                  {{ item.action }}
                </el-button>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
        
        <el-card shadow="hover" class="panel-card">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
              <el-button type="text" size="small">更多</el-button>
            </div>
          </template>
          <div class="recent-events">
            <div class="event-item" v-for="event in recentEvents" :key="event.id">
              <div class="event-info">
                <div class="event-title">{{ event.title }}</div>
                <div class="event-time">{{ event.time }}</div>
              </div>
              <el-tag :type="event.type" size="small">{{ event.status }}</el-tag>
            </div>
          </div>
        </el-card>
      </div>
      
      <div class="right-panel">
        <el-card shadow="hover" class="panel-card">
          <template #header>
            <div class="card-header">
              <span>用户活跃度趋势</span>
              <el-button type="text" size="small">查看详情</el-button>
            </div>
          </template>
          <div class="chart-container">
            <div class="chart-placeholder">
              <el-icon><PieChart /></el-icon>
              <span>图表加载中...</span>
            </div>
          </div>
        </el-card>
        
        <el-card shadow="hover" class="panel-card">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button v-for="action in quickActions" :key="action.name" type="primary" :icon="action.icon" @click="handleQuickAction(action)">
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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { OfficeBuilding, User, Calendar, StarFilled, PieChart } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import NavigationHeader from '@/components/NavigationHeader.vue'

const router = useRouter()

const stats = ref({
  totalEnterprises: 120,
  pendingEnterpriseAudit: 20,
  totalUsers: 5000,
  activeUsers: 3500,
  totalEvents: 80,
  upcomingEvents: 30,
  totalFavorites: 2500,
  enterpriseFavorites: 1800
})

const todos = ref([
  { id: 1, title: '审核新企业注册', description: '有20家新企业等待审核', time: '2小时前', action: '立即审核' },
  { id: 2, title: '处理用户投诉', description: '有5条用户投诉需要处理', time: '5小时前', action: '查看投诉' },
  { id: 3, title: '发布新活动', description: '秋季招聘会需要发布', time: '1天前', action: '发布活动' }
])

const recentEvents = ref([
  { id: 1, title: '企业A注册成功', time: '刚刚', type: 'success', status: '完成' },
  { id: 2, title: '用户B报名活动', time: '10分钟前', type: 'primary', status: '进行中' },
  { id: 3, title: '企业C信息更新', time: '30分钟前', type: 'info', status: '更新' },
  { id: 4, title: '活动D报名截止', time: '1小时前', type: 'warning', status: '截止' }
])

const quickActions = ref([
  { name: '添加企业', icon: 'Plus' },
  { name: '发布活动', icon: 'Calendar' },
  { name: '审核用户', icon: 'User' },
  { name: '查看报表', icon: 'Document' },
  { name: '新增管理员', icon: 'UserFilled' }
])

const handleTodo = (todo) => {
  ElMessage.info(`${todo.action}功能开发中...`)
}

const handleQuickAction = (action) => {
  if (action.name === '新增管理员') {
    router.push('/admin/register')
  } else {
    ElMessage.info(`${action.name}功能开发中...`)
  }
}

onMounted(() => {
  // 组件挂载时获取统计数据
})
</script>

<style scoped>
.admin-dashboard {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  flex: 1;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  height: 100%;
}

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
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.stat-icon {
  font-size: 32px;
  color: #67c23a;
}

.stat-desc {
  font-size: 12px;
  color: #999;
  margin-top: 10px;
  text-align: right;
}

.dashboard-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.panel-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.todo-item {
  margin-bottom: 10px;
}

.todo-item h4 {
  margin: 0 0 5px 0;
}

.todo-item p {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
}

.recent-events {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.event-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.event-info {
  flex: 1;
}

.event-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.event-time {
  font-size: 12px;
  color: #999;
}

.chart-container {
  height: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
}

.chart-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}
</style>