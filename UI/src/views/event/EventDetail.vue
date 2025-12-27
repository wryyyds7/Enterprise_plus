<template>
  <div class="event-detail-container">
    <el-card shadow="hover" class="event-detail-card">
      <template #header>
        <div class="card-header">
          <h2>{{ event.eventName }}</h2>
          <div class="event-actions">
            <el-button type="primary" @click="handleEdit">编辑</el-button>
            <el-button type="danger" @click="handleDelete">删除</el-button>
          </div>
        </div>
      </template>
      
      <div class="event-info">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">事件类型：</span>
              <span class="value">{{ event.eventType }}</span>
            </div>
            <div class="info-item">
              <span class="label">展示位置：</span>
              <span class="value">{{ event.displayPosition }}</span>
            </div>
            <div class="info-item">
              <span class="label">开始时间：</span>
              <span class="value">{{ formatDate(event.startTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">结束时间：</span>
              <span class="value">{{ formatDate(event.endTime) }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">点击次数：</span>
              <span class="value">{{ event.clickCount }}</span>
            </div>
            <div class="info-item">
              <span class="label">浏览次数：</span>
              <span class="value">{{ event.viewCount }}</span>
            </div>
            <div class="info-item">
              <span class="label">状态：</span>
              <el-tag :type="event.status === '1' ? 'success' : 'info'">
                {{ event.status === '1' ? '进行中' : '已结束' }}
              </el-tag>
            </div>
          </el-col>
        </el-row>
      </div>
      
      <div class="event-content">
        <h3>事件内容</h3>
        <div class="content-text">{{ event.content }}</div>
      </div>
      
      <div class="event-metadata">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">创建时间：</span>
              <span class="value">{{ formatDate(event.createTime) }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">更新时间：</span>
              <span class="value">{{ formatDate(event.updateTime) }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import eventApi from '@/api/modules/event'

const route = useRoute()
const router = useRouter()
const eventId = route.params.eventId

// 事件详情
const event = ref({
  eventId: '',
  eventName: '',
  eventType: '',
  displayPosition: '',
  startTime: '',
  endTime: '',
  clickCount: 0,
  viewCount: 0,
  status: '1',
  content: '',
  createTime: '',
  updateTime: ''
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 获取事件详情
const fetchEventDetail = async () => {
  try {
    const response = await eventApi.getEventById(eventId)
    if (response.success) {
      event.value = response.data
    } else {
      ElMessage.error('获取事件详情失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取事件详情失败：' + error.message)
    console.error('Error fetching event detail:', error)
  }
}

// 编辑事件
const handleEdit = () => {
  router.push(`/event/edit/${eventId}`)
}

// 删除事件
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该事件吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await eventApi.deleteEvent(eventId)
    if (response.success) {
      ElMessage.success('删除事件成功')
      router.push('/event/list')
    } else {
      ElMessage.error('删除事件失败：' + response.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除事件失败：' + (error.message || '操作取消'))
      console.error('Error deleting event:', error)
    }
  }
}

// 页面挂载时获取事件详情
onMounted(() => {
  fetchEventDetail()
})
</script>

<style scoped>
.event-detail-container {
  padding: 20px;
}

.event-detail-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.event-actions {
  display: flex;
  gap: 10px;
}

.event-info {
  margin-bottom: 30px;
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.event-content {
  margin-bottom: 30px;
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.event-content h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #303133;
}

.content-text {
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
}

.event-metadata {
  padding: 20px 0;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.info-item .label {
  font-weight: bold;
  margin-right: 10px;
  color: #303133;
  width: 100px;
}

.info-item .value {
  color: #606266;
  flex: 1;
}
</style>