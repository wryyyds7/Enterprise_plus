<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import searchApi from '@/api/modules/search'
import enterpriseApi from '@/api/modules/enterprise'

const route = useRoute()
const router = useRouter()
const keyword = ref(route.query.keyword || '')

const enterpriseResults = ref([])
const positionResults = ref([])
const eventResults = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const activeTab = ref('enterprise')
const filters = ref({
  industry: route.query.industry || '',
  location: route.query.location || '',
  jobType: '',
  salaryRange: '',
  workExperience: '',
  education: '',
  companyScale: '',
  sortBy: 'relevance'
})

const industryOptions = [
  { label: '互联网', value: '互联网' },
  { label: '金融', value: '金融' },
  { label: '教育', value: '教育' },
  { label: '医疗', value: '医疗' },
  { label: '制造业', value: '制造业' },
  { label: '其他', value: '其他' }
]

const locationOptions = [
  { label: '北京', value: '北京' },
  { label: '上海', value: '上海' },
  { label: '广州', value: '广州' },
  { label: '深圳', value: '深圳' },
  { label: '杭州', value: '杭州' },
  { label: '长沙', value: '长沙' },
  { label: '其他', value: '其他' }
]

const jobTypeOptions = [
  { label: '全部', value: '' },
  { label: '全职', value: '全职' },
  { label: '兼职', value: '兼职' },
  { label: '实习', value: '实习' }
]

const salaryRangeOptions = [
  { label: '全部', value: '' },
  { label: '3k以下', value: '3k以下' },
  { label: '3k-5k', value: '3k-5k' },
  { label: '5k-8k', value: '5k-8k' },
  { label: '8k-12k', value: '8k-12k' },
  { label: '12k-20k', value: '12k-20k' },
  { label: '20k以上', value: '20k以上' }
]

const workExperienceOptions = [
  { label: '全部', value: '' },
  { label: '应届毕业生', value: '应届毕业生' },
  { label: '1年以下', value: '1年以下' },
  { label: '1-3年', value: '1-3年' },
  { label: '3-5年', value: '3-5年' },
  { label: '5年以上', value: '5年以上' }
]

const educationOptions = [
  { label: '全部', value: '' },
  { label: '大专', value: '大专' },
  { label: '本科', value: '本科' },
  { label: '硕士', value: '硕士' },
  { label: '博士', value: '博士' }
]

const companyScaleOptions = [
  { label: '全部', value: '' },
  { label: '100人以下', value: 'small' },
  { label: '100-500人', value: 'medium' },
  { label: '500-1000人', value: 'large' },
  { label: '1000人以上', value: 'huge' }
]

const sortOptions = [
  { label: '相关性', value: 'relevance' },
  { label: '企业规模', value: 'scale' },
  { label: '推荐度', value: 'recommendation' },
  { label: '薪资待遇', value: 'salary' }
]

// 收藏状态
const favoriteStatus = ref({})

const search = async () => {
  if (!keyword.value) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  loading.value = true

  try {
    if (activeTab.value === 'enterprise') {
      // 构建包含所有筛选条件的查询参数
      const params = {
        enterpriseName: keyword.value,
        page: currentPage.value,
        size: pageSize.value
      }
      if (filters.value.industry) params.industry = filters.value.industry
      if (filters.value.location) params.province = filters.value.location
      if (filters.value.companyScale) params.enterpriseScale = filters.value.companyScale

      const response = await enterpriseApi.getEnterpriseList(params)

      if (response.code === 200 && response.data) {
        let list = response.data.records || response.data || []
        // 前端二次筛选（后端可能不支持所有维度）
        if (filters.value.industry) {
          list = list.filter(e => e.enterpriseClassification === filters.value.industry)
        }
        if (filters.value.location) {
          list = list.filter(e => e.province === filters.value.location || e.city === filters.value.location)
        }
        if (filters.value.companyScale) {
          list = list.filter(e => e.enterpriseScale === filters.value.companyScale)
        }
        // 排序
        if (filters.value.sortBy === 'scale') {
          list.sort((a, b) => (b.enterpriseScale || '').localeCompare(a.enterpriseScale || ''))
        }
        enterpriseResults.value = list
        total.value = response.data.total || list.length
        ElMessage.success(`找到 ${total.value} 条结果`)
      } else {
        enterpriseResults.value = []
        total.value = 0
      }
    } else if (activeTab.value === 'position') {
      const params = {
        positionName: keyword.value,
        page: currentPage.value,
        size: pageSize.value
      }
      if (filters.value.jobType) params.jobType = filters.value.jobType
      if (filters.value.salaryRange) params.salaryRange = filters.value.salaryRange
      if (filters.value.workExperience) params.workExperience = filters.value.workExperience
      if (filters.value.education) params.education = filters.value.education

      const response = await enterpriseApi.getPositionList(params)

      if (response.code === 200 && response.data) {
        let list = response.data.records || response.data || []
        // 前端二次筛选
        if (filters.value.jobType) {
          list = list.filter(p => p.jobType === filters.value.jobType)
        }
        if (filters.value.salaryRange) {
          list = list.filter(p => p.salary && p.salary.includes(filters.value.salaryRange.replace('以下', '').replace('以上', '')))
        }
        // 薪资排序
        if (filters.value.sortBy === 'salary') {
          list.sort((a, b) => (b.salary || '').localeCompare(a.salary || ''))
        }
        positionResults.value = list
        total.value = response.data.total || list.length
        ElMessage.success(`找到 ${total.value} 条结果`)
      } else {
        positionResults.value = []
        total.value = 0
      }
    } else {
      const response = await searchApi.searchEvent({ keyword: keyword.value })
      eventResults.value = response.data || []
      total.value = eventResults.value.length || 0
    }
  } catch (error) {
    ElMessage.error('搜索失败：' + (error.message || '未知错误'))
    console.error('Search error:', error)
    enterpriseResults.value = []
    positionResults.value = []
    eventResults.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  search()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  search()
}

const handleTabChange = () => {
  currentPage.value = 1
  total.value = 0
  search()
}

const handleFilterChange = () => {
  currentPage.value = 1
  search()
}

const handleViewDetail = (enterprise) => {
  router.push({
    name: 'EnterpriseDetail',
    params: { enterpriseId: enterprise.enterpriseId || enterprise.id }
  })
}

const handleFavorite = async (enterprise) => {
  const enterpriseId = enterprise.enterpriseId || enterprise.id
  const isFavorited = favoriteStatus.value[enterpriseId]

  try {
    if (isFavorited) {
      await enterpriseApi.unfavoriteEnterprise(enterpriseId)
      favoriteStatus.value[enterpriseId] = false
      ElMessage.success('取消收藏成功')
    } else {
      await enterpriseApi.favoriteEnterprise(enterpriseId)
      favoriteStatus.value[enterpriseId] = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error(isFavorited ? '取消收藏失败' : '收藏失败')
  }
}

// 监听路由参数变化
watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword && newKeyword !== keyword.value) {
    keyword.value = newKeyword
    currentPage.value = 1
    search()
  }
})

onMounted(() => {
  if (keyword.value) {
    search()
  }
})
</script>

<template>
  <div class="search-result-container">
    <!-- 搜索头部 -->
    <div class="search-header">
      <h2>搜索结果</h2>
      <div class="search-input-wrapper">
        <el-input
          v-model="keyword"
          placeholder="输入企业名称、行业、关键词"
          class="search-input"
          clearable
          @keyup.enter="search"
        >
          <template #append>
            <el-button type="primary" @click="search">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 企业搜索 -->
      <el-tab-pane label="企业" name="enterprise">
        <div class="filter-section">
          <div class="filter-row">
            <div class="filter-item">
              <label>行业</label>
              <el-select v-model="filters.industry" placeholder="全部" clearable @change="handleFilterChange" size="small">
                <el-option v-for="opt in industryOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>地区</label>
              <el-select v-model="filters.location" placeholder="全部" clearable @change="handleFilterChange" size="small">
                <el-option v-for="opt in locationOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>规模</label>
              <el-select v-model="filters.companyScale" placeholder="全部" clearable @change="handleFilterChange" size="small">
                <el-option v-for="opt in companyScaleOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>排序</label>
              <el-select v-model="filters.sortBy" placeholder="相关性" @change="handleFilterChange" size="small">
                <el-option v-for="opt in sortOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </div>
          </div>
        </div>

        <div v-loading="loading" class="results-section">
          <div class="results-info">
            <span>共找到 <strong>{{ total }}</strong> 条企业结果</span>
          </div>

          <div v-if="enterpriseResults.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无搜索结果" />
          </div>

          <div class="enterprise-list">
            <div
              v-for="enterprise in enterpriseResults"
              :key="enterprise.enterpriseId || enterprise.id"
              class="enterprise-card"
              @click="handleViewDetail(enterprise)"
            >
              <div class="enterprise-header">
                <div class="enterprise-logo">
                  {{ (enterprise.enterpriseName || '?').charAt(0) }}
                </div>
                <div class="enterprise-info">
                  <h3>{{ enterprise.enterpriseName }}</h3>
                  <div class="enterprise-meta">
                    <el-tag size="small" type="info">{{ enterprise.enterpriseClassification || '其他' }}</el-tag>
                    <span v-if="enterprise.city" class="meta-text">{{ enterprise.city }}</span>
                    <span v-if="enterprise.province && enterprise.province !== enterprise.city" class="meta-text">{{ enterprise.province }}</span>
                  </div>
                </div>
              </div>

              <div class="enterprise-description">
                {{ enterprise.address || enterprise.enterpriseIntroduction || '暂无描述' }}
              </div>

              <div class="enterprise-actions" @click.stop>
                <el-button type="primary" size="small" round @click="handleViewDetail(enterprise)">查看详情</el-button>
                <el-button
                  :type="favoriteStatus[enterprise.enterpriseId || enterprise.id] ? 'warning' : 'default'"
                  size="small"
                  round
                  @click="handleFavorite(enterprise)"
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
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </el-tab-pane>

      <!-- 职位搜索 -->
      <el-tab-pane label="职位" name="position">
        <div class="filter-section">
          <div class="filter-row">
            <div class="filter-item">
              <label>职位类型</label>
              <el-select v-model="filters.jobType" placeholder="全部" clearable @change="handleFilterChange" size="small">
                <el-option v-for="opt in jobTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>薪资范围</label>
              <el-select v-model="filters.salaryRange" placeholder="全部" clearable @change="handleFilterChange" size="small">
                <el-option v-for="opt in salaryRangeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>工作经验</label>
              <el-select v-model="filters.workExperience" placeholder="全部" clearable @change="handleFilterChange" size="small">
                <el-option v-for="opt in workExperienceOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>学历</label>
              <el-select v-model="filters.education" placeholder="全部" clearable @change="handleFilterChange" size="small">
                <el-option v-for="opt in educationOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>排序</label>
              <el-select v-model="filters.sortBy" placeholder="相关性" @change="handleFilterChange" size="small">
                <el-option v-for="opt in sortOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </div>
          </div>
        </div>

        <div v-loading="loading" class="results-section">
          <div class="results-info">
            <span>共找到 <strong>{{ total }}</strong> 条职位结果</span>
          </div>

          <div v-if="positionResults.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无搜索结果" />
          </div>

          <div class="position-list">
            <div v-for="position in positionResults" :key="position.positionId" class="position-card">
              <div class="position-header">
                <h3>{{ position.positionName }}</h3>
                <span class="position-salary">{{ position.salary || '薪资面议' }}</span>
              </div>
              <div class="position-meta">
                <span class="meta-text">{{ position.enterpriseName || '未知企业' }}</span>
                <span class="meta-text">{{ position.city || '未知城市' }}</span>
                <el-tag v-if="position.jobType" size="small">{{ position.jobType }}</el-tag>
              </div>
              <div class="position-description">
                {{ position.description || '暂无职位描述' }}
              </div>
              <div class="position-actions">
                <el-button type="primary" size="small" round>查看详情</el-button>
                <el-button size="small" round>申请职位</el-button>
              </div>
            </div>
          </div>

          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </el-tab-pane>

      <!-- 活动搜索 -->
      <el-tab-pane label="校招活动" name="event">
        <div v-loading="loading" class="results-section">
          <div class="results-info">
            <span>共找到 <strong>{{ total }}</strong> 条活动结果</span>
          </div>

          <div v-if="eventResults.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无搜索结果" />
          </div>

          <div class="event-list">
            <div v-for="event in eventResults" :key="event.id || event.eventId" class="event-card">
              <div class="event-header">
                <h3>{{ event.title }}</h3>
                <el-tag type="success" size="small">{{ event.status === 'upcoming' ? '即将开始' : '已结束' }}</el-tag>
              </div>
              <div class="event-info">
                <div class="info-item">
                  <span>{{ event.location || '地点待定' }}</span>
                </div>
                <div class="info-item">
                  <span>{{ event.startTime }} - {{ event.endTime }}</span>
                </div>
              </div>
              <div class="event-description">
                {{ event.description || '暂无描述' }}
              </div>
              <div class="event-actions">
                <el-button type="primary" size="small" round>查看详情</el-button>
                <el-button size="small" round>报名</el-button>
              </div>
            </div>
          </div>

          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.search-result-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-header {
  margin-bottom: 24px;
}

.search-header h2 {
  margin-bottom: 16px;
  font-size: 24px;
  color: #303133;
}

.search-input-wrapper {
  display: flex;
  gap: 12px;
}

.search-input {
  max-width: 600px;
}

.filter-section {
  background: #f8f9fb;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 13px;
  font-weight: 500;
  color: #606266;
  white-space: nowrap;
}

.results-section {
  min-height: 400px;
}

.results-info {
  margin-bottom: 16px;
  font-size: 14px;
  color: #909399;
}

.results-info strong {
  color: #409eff;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

/* 企业卡片 */
.enterprise-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.enterprise-card {
  background: #fff;
  border-radius: 10px;
  border: 1px solid #ebeef5;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.enterprise-card:hover {
  border-color: #409eff;
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.enterprise-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 14px;
}

.enterprise-logo {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 600;
  flex-shrink: 0;
}

.enterprise-info h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 6px;
}

.enterprise-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.meta-text {
  font-size: 13px;
  color: #909399;
}

.enterprise-description {
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  margin-bottom: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.enterprise-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 职位卡片 */
.position-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.position-card {
  background: #fff;
  border-radius: 10px;
  border: 1px solid #ebeef5;
  padding: 20px;
  transition: all 0.3s ease;
}

.position-card:hover {
  border-color: #409eff;
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.position-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.position-header h3 {
  font-size: 16px;
  color: #303133;
}

.position-salary {
  color: #f56c6c;
  font-weight: 600;
  font-size: 15px;
}

.position-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 13px;
  color: #909399;
  align-items: center;
}

.position-description {
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  margin-bottom: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.position-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 活动卡片 */
.event-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.event-card {
  background: #fff;
  border-radius: 10px;
  border: 1px solid #ebeef5;
  padding: 20px;
  transition: all 0.3s ease;
}

.event-card:hover {
  border-color: #67c23a;
  box-shadow: 0 6px 16px rgba(103, 194, 58, 0.15);
  transform: translateY(-2px);
}

.event-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.event-header h3 {
  font-size: 16px;
  color: #303133;
}

.event-info {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
  color: #909399;
}

.event-description {
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  margin-bottom: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.event-actions {
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
  .search-result-container {
    padding: 16px;
  }
  .enterprise-list,
  .position-list,
  .event-list {
    grid-template-columns: 1fr;
  }
  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>