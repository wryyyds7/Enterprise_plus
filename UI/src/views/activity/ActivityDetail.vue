<template>
  <div class="activity-detail-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>活动详情</span>
        </div>
      </template>
      
      <div v-if="activity" class="activity-info">
        <h2 class="activity-title">{{ activity.activityName }}</h2>
        <div class="activity-meta">
          <el-tag>{{ activity.activityType }}</el-tag>
          <el-tag :type="activity.status === '1' ? 'success' : 'info'">
            {{ activity.status === '1' ? '进行中' : '已结束' }}
          </el-tag>
        </div>
        <div class="activity-content">
          <p><strong>开始时间：</strong>{{ activity.startTime }}</p>
          <p><strong>结束时间：</strong>{{ activity.endTime }}</p>
          <p><strong>活动地点：</strong>{{ activity.location }}</p>
          <p><strong>活动描述：</strong>{{ activity.description }}</p>
        </div>
        <div class="activity-actions">
          <el-button 
            v-if="!isRegistered" 
            type="primary" 
            @click="handleRegister"
            :disabled="activity.status === '0'"
          >
            报名活动
          </el-button>
          <el-button 
            v-else 
            type="warning" 
            @click="handleCancelRegistration"
          >
            取消报名
          </el-button>
          <el-button 
            type="success" 
            :icon="isFavorited ? StarFilled : Star"
            @click="handleFavorite"
          >
            {{ isFavorited ? '取消收藏' : '收藏活动' }}
          </el-button>
          <el-button 
            type="danger" 
            :icon="Star"
            @click="handleLike"
          >
            点赞 ({{ activity.likeCount || 0 }})
          </el-button>
          <el-button type="info" @click="handleBack">返回列表</el-button>
        </div>
      </div>
      <div v-else class="no-data">
        <el-empty description="暂无活动详情" />
        <el-button type="primary" @click="handleBack">返回列表</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import activityApi from '@/api/modules/activity'

const router = useRouter()
const route = useRoute()

// 活动详情
const activity = ref(null)
// 是否已报名
const isRegistered = ref(false)
// 是否已收藏
const isFavorited = ref(false)
// 模拟用户ID
const userId = ref(1)

// 获取活动详情
const fetchActivityDetail = async () => {
  const activityId = route.params.activityId
  try {
    const response = await activityApi.getActivityInfo(activityId)
    activity.value = response.data
    // 检查用户是否已报名
    checkRegistrationStatus(activityId)
    // 检查用户是否已收藏
    checkFavoriteStatus(activityId)
  } catch (error) {
    ElMessage.error('获取活动详情失败：' + error.message)
    console.error('Error fetching activity detail:', error)
    // 使用模拟数据作为后备
    activity.value = {
      activityId: activityId,
      activityName: '2025年春季校园招聘会',
      activityType: '招聘会',
      startTime: '2025-03-15 09:00:00',
      endTime: '2025-03-15 17:00:00',
      location: '图书馆前广场',
      description: '2025年春季校园招聘会，欢迎各位同学参加！',
      status: '1',
      likeCount: 123
    }
    // 初始化模拟状态
    isRegistered.value = false
    isFavorited.value = false
  }
}

// 检查用户是否已报名
const checkRegistrationStatus = async (activityId) => {
  try {
    const response = await activityApi.isRegistered(activityId, userId.value)
    isRegistered.value = response.data
  } catch (error) {
    console.error('Error checking registration status:', error)
    isRegistered.value = false
  }
}

// 检查用户是否已收藏
const checkFavoriteStatus = async (activityId) => {
  try {
    const response = await activityApi.isFavorited(activityId, userId.value)
    isFavorited.value = response.data
  } catch (error) {
    console.error('Error checking favorite status:', error)
    isFavorited.value = false
  }
}

// 报名活动
const handleRegister = async () => {
  try {
    const response = await activityApi.registerActivity({
      activityId: activity.value.activityId,
      userId: userId.value
    })
    if (response.success) {
      ElMessage.success('报名成功')
      isRegistered.value = true
    } else {
      ElMessage.error('报名失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('报名失败：' + error.message)
    console.error('Error registering for activity:', error)
  }
}

// 取消报名
const handleCancelRegistration = async () => {
  try {
    const response = await activityApi.cancelRegistration(
      activity.value.activityId,
      userId.value
    )
    if (response.success) {
      ElMessage.success('取消报名成功')
      isRegistered.value = false
    } else {
      ElMessage.error('取消报名失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('取消报名失败：' + error.message)
    console.error('Error canceling registration:', error)
  }
}

// 收藏/取消收藏活动
const handleFavorite = async () => {
  try {
    if (isFavorited.value) {
      // 取消收藏
      const response = await activityApi.cancelFavorite(
        activity.value.activityId,
        userId.value
      )
      if (response.success) {
        ElMessage.success('取消收藏成功')
        isFavorited.value = false
      } else {
        ElMessage.error('取消收藏失败：' + response.message)
      }
    } else {
      // 收藏
      const response = await activityApi.favoriteActivity({
        activityId: activity.value.activityId,
        userId: userId.value
      })
      if (response.success) {
        ElMessage.success('收藏成功')
        isFavorited.value = true
      } else {
        ElMessage.error('收藏失败：' + response.message)
      }
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
    console.error('Error handling favorite:', error)
  }
}

// 点赞活动
const handleLike = async () => {
  try {
    const response = await activityApi.incrementLikeCount(activity.value.activityId)
    if (response.success) {
      ElMessage.success('点赞成功')
      activity.value.likeCount = (activity.value.likeCount || 0) + 1
    } else {
      ElMessage.error('点赞失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('点赞失败：' + error.message)
    console.error('Error liking activity:', error)
  }
}

// 返回列表
const handleBack = () => {
  router.push('/activity/list')
}

// 页面挂载时获取活动详情
onMounted(() => {
  fetchActivityDetail()
})
</script>

<style scoped>
.activity-detail-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #303133;
}

.activity-meta {
  margin-bottom: 20px;
}

.activity-meta .el-tag {
  margin-right: 10px;
}

.activity-content {
  margin-bottom: 30px;
}

.activity-content p {
  margin-bottom: 10px;
  line-height: 1.6;
}

.activity-actions {
  display: flex;
  justify-content: flex-start;
}

.no-data {
  text-align: center;
  padding: 50px 0;
}

.no-data .el-button {
  margin-top: 20px;
}
</style>