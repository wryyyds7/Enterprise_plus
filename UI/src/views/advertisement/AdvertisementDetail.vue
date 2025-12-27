<template>
  <div class="advertisement-detail-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>广告详情</span>
        </div>
      </template>
      
      <div v-if="advertisement" class="advertisement-info">
        <h2 class="advertisement-title">{{ advertisement.adName }}</h2>
        <div class="advertisement-meta">
          <el-tag>{{ advertisement.adType }}</el-tag>
          <el-tag :type="advertisement.status === '1' ? 'success' : 'info'">
            {{ advertisement.status === '1' ? '启用' : '禁用' }}
          </el-tag>
        </div>
        <div class="advertisement-content">
          <p><strong>展示位置：</strong>{{ advertisement.displayPosition }}</p>
          <p><strong>开始时间：</strong>{{ advertisement.startTime }}</p>
          <p><strong>结束时间：</strong>{{ advertisement.endTime }}</p>
          <p><strong>点击次数：</strong>{{ advertisement.clickCount }}</p>
          <p><strong>浏览次数：</strong>{{ advertisement.viewCount }}</p>
          <p><strong>广告链接：</strong>
            <el-link :href="advertisement.adUrl" target="_blank">{{ advertisement.adUrl }}</el-link>
          </p>
          <p><strong>广告描述：</strong>{{ advertisement.description }}</p>
        </div>
        <div class="advertisement-actions">
          <el-button type="primary" @click="handleBack">返回列表</el-button>
          <el-button type="success" @click="handleEdit">编辑广告</el-button>
          <el-button type="danger" @click="handleDelete">删除广告</el-button>
        </div>
      </div>
      <div v-else class="no-data">
        <el-empty description="暂无广告详情" />
        <el-button type="primary" @click="handleBack">返回列表</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import advertisementApi from '@/api/modules/advertisement'

const router = useRouter()
const route = useRoute()

// 广告详情
const advertisement = ref(null)

// 获取广告详情
const fetchAdvertisementDetail = async () => {
  const adId = route.params.adId
  try {
    const response = await advertisementApi.getAdvertisementInfo(adId)
    advertisement.value = response.data
    // 更新广告浏览次数
    updateViewCount(adId)
  } catch (error) {
    ElMessage.error('获取广告详情失败：' + error.message)
    console.error('Error fetching advertisement detail:', error)
    // 使用模拟数据作为后备
    advertisement.value = {
      adId: adId,
      adName: '2025年春季校园招聘会广告',
      adType: 'BANNER',
      displayPosition: 'HOME_TOP',
      startTime: '2025-03-01 00:00:00',
      endTime: '2025-03-31 23:59:59',
      clickCount: 1234,
      viewCount: 56789,
      adUrl: 'https://example.com',
      description: '2025年春季校园招聘会广告，欢迎点击了解详情！',
      status: '1'
    }
  }
}

// 更新广告浏览次数
const updateViewCount = async (adId) => {
  try {
    await advertisementApi.incrementViewCount(adId)
    // 这里可以选择是否更新本地数据，为了避免频繁请求，暂时不更新
  } catch (error) {
    console.error('Error updating view count:', error)
  }
}

// 返回列表
const handleBack = () => {
  router.push('/advertisement/list')
}

// 编辑广告
const handleEdit = () => {
  router.push(`/advertisement/edit/${advertisement.value.adId}`)
}

// 删除广告
const handleDelete = async () => {
  try {
    const response = await advertisementApi.deleteAdvertisement(advertisement.value.adId)
    if (response.success) {
      ElMessage.success('删除广告成功')
      router.push('/advertisement/list')
    } else {
      ElMessage.error('删除广告失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('删除广告失败：' + error.message)
    console.error('Error deleting advertisement:', error)
  }
}

// 页面挂载时获取广告详情
onMounted(() => {
  fetchAdvertisementDetail()
})
</script>

<style scoped>
.advertisement-detail-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.advertisement-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #303133;
}

.advertisement-meta {
  margin-bottom: 20px;
}

.advertisement-meta .el-tag {
  margin-right: 10px;
}

.advertisement-content {
  margin-bottom: 30px;
}

.advertisement-content p {
  margin-bottom: 10px;
  line-height: 1.6;
}

.advertisement-actions {
  display: flex;
  justify-content: flex-start;
  gap: 10px;
}

.no-data {
  text-align: center;
  padding: 50px 0;
}

.no-data .el-button {
  margin-top: 20px;
}
</style>