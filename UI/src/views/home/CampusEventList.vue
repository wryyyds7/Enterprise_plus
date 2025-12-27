<template>
  <div class="campus-event-list">
    <NavigationHeader />
    <div class="event-list-container">
      <h2>校园招聘活动列表</h2>
    
    <div class="filter-bar">
      <el-select v-model="filters.location" placeholder="选择地点" style="width: 150px; margin-right: 10px;">
        <el-option label="北京" value="北京" />
        <el-option label="上海" value="上海" />
        <el-option label="广州" value="广州" />
        <el-option label="深圳" value="深圳" />
      </el-select>
      
      <el-date-picker
        v-model="filters.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        style="width: 300px; margin-right: 10px;"
      />
      
      <el-button type="primary" @click="searchEvents">搜索</el-button>
    </div>
    
    <el-card class="event-card" v-for="event in events" :key="event.id" shadow="hover">
      <div class="event-info">
        <h3>{{ event.title }}</h3>
        <div class="event-meta">
          <span class="event-time">{{ event.startTime }} - {{ event.endTime }}</span>
          <span class="event-location">{{ event.location }}</span>
          <span class="event-company-count">参与企业: {{ event.companyCount }}家</span>
        </div>
        <p class="event-description">{{ event.description }}</p>
      </div>
      <div class="event-actions">
        <el-button type="primary" @click="viewEventDetail(event.id)">查看详情</el-button>
        <el-button :type="event.registered ? 'info' : 'success'" @click="toggleRegistration(event)">
          {{ event.registered ? '已报名' : '立即报名' }}
        </el-button>
      </div>
    </el-card>
    
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavigationHeader from '@/components/NavigationHeader.vue'
import { ElMessage, ElLoading } from 'element-plus'
import homeApi from '@/api/modules/home.js'

const router = useRouter()

const filters = ref({
  location: '',
  dateRange: []
})

// Mock数据作为后备
const mockEvents = [
  {
    id: 1,
    title: '2024秋季校园招聘会',
    startTime: '2024-10-20 09:00',
    endTime: '2024-10-20 17:00',
    location: '北京大学校园招聘会场馆',
    companyCount: 100,
    description: '本次招聘会汇聚了100家知名企业，涵盖互联网、金融、教育等多个行业，为2025届毕业生提供丰富的就业机会。',
    registered: false
  },
  {
    id: 2,
    title: '腾讯2025校招宣讲会',
    startTime: '2024-10-25 14:00',
    endTime: '2024-10-25 16:00',
    location: '清华大学礼堂',
    companyCount: 1,
    description: '腾讯2025校园招聘宣讲会，了解腾讯企业文化、校招政策和岗位信息，现场接收简历。',
    registered: true
  }
]

const events = ref([])

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 获取校园招聘活动列表
const getCampusEvents = async () => {
  const loading = ElLoading.service({
    lock: true,
    text: '加载中...',
    background: 'rgba(255, 255, 255, 0.7)'
  })
  
  try {
    const params = {
      location: filters.value.location,
      startTime: filters.value.dateRange[0] ? filters.value.dateRange[0] : undefined,
      endTime: filters.value.dateRange[1] ? filters.value.dateRange[1] : undefined,
      pageNum: pagination.value.currentPage,
      pageSize: pagination.value.pageSize
    }
    
    const response = await homeApi.getCampusEvents(params)
    events.value = response.data || []
    pagination.value.total = response.total || 0
  } catch (error) {
    console.error('获取校园招聘活动失败:', error)
    ElMessage.warning('获取数据失败，使用模拟数据')
    // API调用失败时使用mock数据作为后备
    events.value = mockEvents
    pagination.value.total = mockEvents.length
  } finally {
    loading.close()
  }
}

const searchEvents = () => {
  // 重置页码并搜索
  pagination.value.currentPage = 1
  getCampusEvents()
}

const viewEventDetail = (eventId) => {
  router.push(`/home/event/${eventId}`)
}

const toggleRegistration = async (event) => {
  try {
    await homeApi.registerEvent({ eventId: event.id })
    event.registered = !event.registered
    ElMessage.success(event.registered ? '报名成功！' : '取消报名成功！')
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const handleSizeChange = (val) => {
  pagination.value.pageSize = val
  getCampusEvents()
}

const handleCurrentChange = (val) => {
  pagination.value.currentPage = val
  getCampusEvents()
}

onMounted(() => {
  // 组件挂载时获取活动列表
  getCampusEvents()
})
</script>

<style scoped>
.campus-event-list {
  padding: 20px;
}

.filter-bar {
  margin-bottom: 20px;
}

.event-card {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.event-info {
  flex: 1;
}

.event-meta {
  margin: 10px 0;
  color: #666;
}

.event-time, .event-location, .event-company-count {
  margin-right: 20px;
}

.event-description {
  color: #999;
  margin-bottom: 10px;
}

.event-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>