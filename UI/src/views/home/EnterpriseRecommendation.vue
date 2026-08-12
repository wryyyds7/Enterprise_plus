<template>
  <div class="enterprise-recommendation">
    <NavigationHeader />
    <div class="recommendation-container">
      <div class="page-header">
        <h2>个性化企业推荐</h2>
        <p class="header-desc">根据您的标签和偏好，为您智能推荐最匹配的企业</p>
      </div>

      <div class="filter-bar">
        <div class="filter-left">
          <el-select v-model="filters.industry" placeholder="选择行业" clearable size="default" @change="handleFilterChange">
            <el-option label="互联网" value="互联网" />
            <el-option label="金融" value="金融" />
            <el-option label="教育" value="教育" />
            <el-option label="制造业" value="制造业" />
            <el-option label="医疗" value="医疗" />
          </el-select>
          <el-select v-model="filters.sortBy" placeholder="排序方式" size="default" @change="handleFilterChange">
            <el-option label="推荐度最高" value="relevance" />
            <el-option label="企业规模" value="size" />
          </el-select>
        </div>
        <el-button type="primary" @click="refreshRecommendations" :loading="loading">
          刷新推荐
        </el-button>
      </div>

      <div v-loading="loading" class="recommendation-content">
        <div v-if="enterprises.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无推荐结果，请先设置您的标签偏好" />
        </div>

        <div class="enterprise-list">
          <div
            v-for="(enterprise, index) in enterprises"
            :key="enterprise.enterpriseId || enterprise.id || index"
            class="enterprise-card"
            @click="viewEnterpriseDetail(enterprise)"
          >
            <!-- 推荐排名标识 -->
            <div class="rank-badge" v-if="index < 3">{{ index + 1 }}</div>

            <div class="card-header">
              <div class="enterprise-logo">
                {{ (enterprise.enterpriseName || '?').charAt(0) }}
              </div>
              <div class="enterprise-info">
                <h3>{{ enterprise.enterpriseName }}</h3>
                <div class="enterprise-meta">
                  <el-tag size="small" type="info">{{ enterprise.enterpriseClassification || '其他' }}</el-tag>
                  <span v-if="enterprise.city" class="meta-text">{{ enterprise.city }}</span>
                </div>
              </div>
              <!-- 推荐分数 -->
              <div v-if="enterprise.relevanceScore" class="recommendation-score">
                <div class="score-bar">
                  <div class="score-fill" :style="{ width: (enterprise.relevanceScore * 100) + '%' }"></div>
                </div>
                <span class="score-text">{{ (enterprise.relevanceScore * 100).toFixed(0) }}% 匹配</span>
              </div>
            </div>

            <!-- 推荐理由：匹配的标签 -->
            <div v-if="enterprise.matchedTags && enterprise.matchedTags.length > 0" class="match-reason">
              <span class="reason-label">推荐理由：</span>
              <el-tag
                v-for="tag in enterprise.matchedTags.slice(0, 5)"
                :key="tag"
                size="small"
                type="success"
                effect="plain"
              >
                {{ tag }}
              </el-tag>
            </div>

            <div class="enterprise-description">
              {{ enterprise.enterpriseIntroduction || enterprise.address || '暂无描述' }}
            </div>

            <!-- 推荐职位 -->
            <div v-if="enterprise.positions && enterprise.positions.length > 0" class="position-preview">
              <span class="preview-label">推荐职位：</span>
              <el-tag
                v-for="pos in enterprise.positions.slice(0, 3)"
                :key="pos.positionId || pos.positionName"
                size="small"
                type="warning"
                effect="plain"
              >
                {{ pos.positionName }}
              </el-tag>
              <span v-if="enterprise.positions.length > 3" class="more-positions">
                等{{ enterprise.positions.length }}个职位
              </span>
            </div>

            <div class="card-footer" @click.stop>
              <el-button type="primary" size="small" round @click="viewEnterpriseDetail(enterprise)">查看详情</el-button>
              <el-button
                :type="favoriteStatus[enterprise.enterpriseId || enterprise.id] ? 'warning' : 'default'"
                size="small"
                round
                @click="toggleFavorite(enterprise)"
              >
                {{ favoriteStatus[enterprise.enterpriseId || enterprise.id] ? '已收藏' : '收藏' }}
              </el-button>
            </div>
          </div>
        </div>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalEnterprises"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavigationHeader from '@/components/NavigationHeader.vue'
import { ElMessage } from 'element-plus'
import homeApi from '@/api/modules/home.js'
import { useAuthStore } from '@/store/modules/auth'

const router = useRouter()
const authStore = useAuthStore()

const filters = ref({
  industry: '',
  sortBy: 'relevance'
})

const enterprises = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalEnterprises = ref(0)
const favoriteStatus = ref({})

const getEnterpriseRecommendations = async () => {
  loading.value = true

  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      sortBy: filters.value.sortBy
    }
    if (filters.value.industry) params.industry = filters.value.industry

    const response = await homeApi.getEnterpriseRecommendations(params)

    // 适配多种响应格式
    const isSuccess = response.code === 200 || response.success
    if (isSuccess) {
      const data = response.data
      if (Array.isArray(data)) {
        enterprises.value = data
        totalEnterprises.value = data.length
      } else if (data && data.records) {
        enterprises.value = data.records
        totalEnterprises.value = data.total || data.records.length
      } else if (data && data.data) {
        enterprises.value = Array.isArray(data.data) ? data.data : []
        totalEnterprises.value = enterprises.value.length
      } else {
        enterprises.value = []
        totalEnterprises.value = 0
      }
    } else {
      enterprises.value = []
      totalEnterprises.value = 0
    }
  } catch (error) {
    console.error('获取企业推荐列表失败:', error)
    ElMessage.warning('获取推荐数据失败，请稍后重试')
    enterprises.value = []
    totalEnterprises.value = 0
  } finally {
    loading.value = false
  }
}

const toggleFavorite = async (enterprise) => {
  const enterpriseId = enterprise.enterpriseId || enterprise.id
  const isFavorited = favoriteStatus.value[enterpriseId]

  try {
    if (isFavorited) {
      await homeApi.unfavoriteEnterprise(enterpriseId)
      favoriteStatus.value[enterpriseId] = false
      ElMessage.success('取消收藏成功')
    } else {
      await homeApi.favoriteEnterprise({ enterpriseId })
      favoriteStatus.value[enterpriseId] = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error(isFavorited ? '取消收藏失败' : '收藏失败')
  }
}

const viewEnterpriseDetail = (enterprise) => {
  const id = enterprise.enterpriseId || enterprise.id
  router.push(`/home/enterprise/${id}`)
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  getEnterpriseRecommendations()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getEnterpriseRecommendations()
}

const handleFilterChange = () => {
  currentPage.value = 1
  getEnterpriseRecommendations()
}

const refreshRecommendations = () => {
  currentPage.value = 1
  getEnterpriseRecommendations()
}

onMounted(() => {
  getEnterpriseRecommendations()
})
</script>

<style scoped>
.enterprise-recommendation {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #f5f7fa;
}

.recommendation-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px;
  flex: 1;
  width: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.header-desc {
  font-size: 14px;
  color: #909399;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: #fff;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.filter-left {
  display: flex;
  gap: 12px;
}

.recommendation-content {
  min-height: 400px;
}

.empty-state {
  padding: 80px 0;
  text-align: center;
}

.enterprise-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.enterprise-card {
  position: relative;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #ebeef5;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.enterprise-card:hover {
  border-color: #409eff;
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.12);
  transform: translateY(-3px);
}

.rank-badge {
  position: absolute;
  top: -8px;
  right: 16px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f56c6c, #f89898);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.4);
}

.card-header {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  margin-bottom: 16px;
}

.enterprise-logo {
  width: 52px;
  height: 52px;
  border-radius: 10px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
  flex-shrink: 0;
}

.enterprise-info {
  flex: 1;
}

.enterprise-info h3 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 8px;
}

.enterprise-meta {
  display: flex;
  gap: 8px;
  align-items: center;
}

.meta-text {
  font-size: 13px;
  color: #909399;
}

.recommendation-score {
  text-align: right;
  flex-shrink: 0;
}

.score-bar {
  width: 80px;
  height: 6px;
  background: #ebeef5;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 4px;
}

.score-fill {
  height: 100%;
  background: linear-gradient(90deg, #67c23a, #409eff);
  border-radius: 3px;
  transition: width 0.5s ease;
}

.score-text {
  font-size: 12px;
  color: #409eff;
  font-weight: 500;
}

.match-reason {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 14px;
  padding: 10px 12px;
  background: #f0f9eb;
  border-radius: 6px;
}

.reason-label {
  font-size: 12px;
  color: #67c23a;
  font-weight: 500;
  white-space: nowrap;
}

.enterprise-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.position-preview {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 16px;
  padding: 10px 12px;
  background: #fdf6ec;
  border-radius: 6px;
}

.preview-label {
  font-size: 12px;
  color: #e6a23c;
  font-weight: 500;
  white-space: nowrap;
}

.more-positions {
  font-size: 12px;
  color: #e6a23c;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .recommendation-container {
    padding: 16px;
  }
  .enterprise-list {
    grid-template-columns: 1fr;
  }
  .filter-bar {
    flex-direction: column;
    gap: 12px;
  }
  .filter-left {
    flex-direction: column;
    width: 100%;
  }
}
</style>