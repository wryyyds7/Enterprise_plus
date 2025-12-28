<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElPagination, ElTabs, ElTabPane, ElCard, ElTag, ElSelect, ElOption, ElInput } from 'element-plus'
import searchApi from '@/api/modules/search'
import enterpriseApi from '@/api/modules/enterprise'

// 获取路由参数
const route = useRoute()
const router = useRouter()
const keyword = ref(route.query.keyword || '')

// 搜索结果数据
const enterpriseResults = ref([])
const paginatedEnterpriseResults = ref([])
const positionResults = ref([])
const paginatedPositionResults = ref([])
const eventResults = ref([])
const paginatedEventResults = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
// 收藏状态管理
const favoriteStatus = ref({})

// 筛选条件
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

// 行业选项
const industryOptions = [
  { label: '互联网', value: '互联网' },
  { label: '金融', value: '金融' },
  { label: '教育', value: '教育' },
  { label: '医疗', value: '医疗' },
  { label: '制造业', value: '制造业' },
  { label: '其他', value: '其他' }
]

// 地区选项
const locationOptions = [
  { label: '北京', value: '北京' },
  { label: '上海', value: '上海' },
  { label: '广州', value: '广州' },
  { label: '深圳', value: '深圳' },
  { label: '杭州', value: '杭州' },
  { label: '长沙', value: '长沙' },
  { label: '湖南', value: '湖南' },
  { label: '其他', value: '其他' }
]

// 职位类型选项
const jobTypeOptions = [
  { label: '全部', value: '' },
  { label: '全职', value: '全职' },
  { label: '兼职', value: '兼职' },
  { label: '实习', value: '实习' }
]

// 薪资范围选项
const salaryRangeOptions = [
  { label: '全部', value: '' },
  { label: '3k以下', value: '3k以下' },
  { label: '3k-5k', value: '3k-5k' },
  { label: '5k-8k', value: '5k-8k' },
  { label: '8k-12k', value: '8k-12k' },
  { label: '12k-20k', value: '12k-20k' },
  { label: '20k以上', value: '20k以上' }
]

// 工作经验选项
const workExperienceOptions = [
  { label: '全部', value: '' },
  { label: '应届毕业生', value: '应届毕业生' },
  { label: '1年以下', value: '1年以下' },
  { label: '1-3年', value: '1-3年' },
  { label: '3-5年', value: '3-5年' },
  { label: '5年以上', value: '5年以上' }
]

// 学历要求选项
const educationOptions = [
  { label: '全部', value: '' },
  { label: '大专', value: '大专' },
  { label: '本科', value: '本科' },
  { label: '硕士', value: '硕士' },
  { label: '博士', value: '博士' }
]

// 公司规模选项
const companyScaleOptions = [
  { label: '全部', value: '' },
  { label: '100人以下', value: '100人以下' },
  { label: '100-500人', value: '100-500人' },
  { label: '500-1000人', value: '500-1000人' },
  { label: '1000-5000人', value: '1000-5000人' },
  { label: '5000人以上', value: '5000人以上' }
]

// 排序选项
const sortOptions = [
  { label: '相关性', value: 'relevance' },
  { label: '企业规模', value: 'scale' },
  { label: '推荐度', value: 'recommendation' },
  { label: '薪资待遇', value: 'salary' }
]



// 执行搜索
const search = async () => {
  if (!keyword.value) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  loading.value = true
  
  try {
            // 调用真实API
            if (activeTab.value === 'enterprise') {
                // 使用多条件搜索API
                const response = await enterpriseApi.getEnterpriseList({ enterpriseName: keyword.value })
                
                // 适配API响应格式
                if (response.code === 200 && response.data) {
                    enterpriseResults.value = response.data.records || []
                    total.value = response.data.total || 0
                    // 应用分页
                    applyPagination()
                    ElMessage.success('搜索成功')
                } else {
                    ElMessage.warning('搜索结果为空')
                    enterpriseResults.value = []
                    total.value = 0
                }
            } else if (activeTab.value === 'position') {
                // 职位搜索，调用position模块的搜索方法
                const response = await enterpriseApi.getPositionList({ positionName: keyword.value })
                
                // 适配API响应格式
                if (response.code === 200 && response.data) {
                    positionResults.value = response.data.records || []
                    total.value = response.data.total || 0
                    // 应用分页
                    applyPagination()
                    ElMessage.success('搜索成功')
                } else {
                    ElMessage.warning('搜索结果为空')
                    positionResults.value = []
                    total.value = 0
                }
            } else {
                const response = await searchApi.searchEvent({ keyword: keyword.value })
                eventResults.value = response.data || []
                total.value = eventResults.value.length || 0
                // 应用分页
                applyPagination()
            }
            
        } catch (error) {
            ElMessage.error('搜索API调用失败：' + error.message)
            console.error('Search API error:', error)
            enterpriseResults.value = []
            positionResults.value = []
            eventResults.value = []
            total.value = 0
            applyPagination()
        } finally {
            loading.value = false
        }
}

// 分页变化
const handlePageChange = (page) => {
  currentPage.value = page
  // 重新应用分页逻辑
  applyPagination()
}

// 标签切换
const handleTabChange = (tab) => {
  activeTab.value = tab
  currentPage.value = 1
  search()
}

// 筛选条件变化
const handleFilterChange = () => {
  currentPage.value = 1
  search()
}

// 应用前端分页
const applyPagination = () => {
  if (activeTab.value === 'enterprise') {
    const startIndex = (currentPage.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    paginatedEnterpriseResults.value = enterpriseResults.value.slice(startIndex, endIndex)
  } else if (activeTab.value === 'position') {
    const startIndex = (currentPage.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    paginatedPositionResults.value = positionResults.value.slice(startIndex, endIndex)
  } else {
    const startIndex = (currentPage.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    paginatedEventResults.value = eventResults.value.slice(startIndex, endIndex)
  }
}

// 查看企业详情
const handleViewDetail = (enterprise) => {
  router.push({
    name: 'EnterpriseDetail',
    params: { enterpriseId: enterprise.enterpriseId || enterprise.id }
  })
}

// 处理收藏/取消收藏
const handleFavorite = async (enterprise) => {
  const enterpriseId = enterprise.enterpriseId || enterprise.id
  const isFavorited = favoriteStatus.value[enterpriseId]
  
  try {
    if (isFavorited) {
      // 取消收藏
      await enterpriseApi.unfavoriteEnterprise(enterpriseId)
      favoriteStatus.value[enterpriseId] = false
      ElMessage.success('取消收藏成功')
    } else {
      // 收藏
      await enterpriseApi.favoriteEnterprise(enterpriseId)
      favoriteStatus.value[enterpriseId] = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error(isFavorited ? '取消收藏失败' : '收藏失败')
    console.error('收藏操作失败:', error)
  }
}

// 初始加载
onMounted(() => {
  if (keyword.value) {
    search()
  }
})
</script>

<template>
  <div class="search-result-container">
    <div class="search-header">
      <h2>搜索结果</h2>
      <div class="search-input-wrapper">
        <el-input 
          v-model="keyword" 
          placeholder="输入企业名称、行业、关键词" 
          style="width: 400px; margin-right: 10px"
        >
          <template #append>
            <el-button type="primary" @click="search">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>
    
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="企业" name="enterprise">
        <div class="filter-section">
          <div class="filter-item">
            <label>行业：</label>
            <el-select v-model="filters.industry" placeholder="选择行业" @change="handleFilterChange">
              <el-option label="全部" value=""></el-option>
              <el-option v-for="option in industryOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <label>地区：</label>
            <el-select v-model="filters.location" placeholder="选择地区" @change="handleFilterChange">
              <el-option label="全部" value=""></el-option>
              <el-option v-for="option in locationOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <label>职位类型：</label>
            <el-select v-model="filters.jobType" placeholder="选择职位类型" @change="handleFilterChange">
              <el-option v-for="option in jobTypeOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <label>薪资范围：</label>
            <el-select v-model="filters.salaryRange" placeholder="选择薪资范围" @change="handleFilterChange">
              <el-option v-for="option in salaryRangeOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <label>工作经验：</label>
            <el-select v-model="filters.workExperience" placeholder="选择工作经验" @change="handleFilterChange">
              <el-option v-for="option in workExperienceOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <label>学历要求：</label>
            <el-select v-model="filters.education" placeholder="选择学历要求" @change="handleFilterChange">
              <el-option v-for="option in educationOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <label>公司规模：</label>
            <el-select v-model="filters.companyScale" placeholder="选择公司规模" @change="handleFilterChange">
              <el-option v-for="option in companyScaleOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <label>排序：</label>
            <el-select v-model="filters.sortBy" placeholder="选择排序" @change="handleFilterChange">
              <el-option v-for="option in sortOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
        </div>
        
        <div class="results-section">
          <div class="results-info">
            <span>共找到 {{ total }} 条企业结果</span>
          </div>
          
          <div class="enterprise-list">
            <el-card 
              v-for="enterprise in paginatedEnterpriseResults" 
              :key="enterprise.id" 
              class="enterprise-card"
            >
              <div class="enterprise-header">
                <img :src="enterprise.logo || 'https://picsum.photos/id/237/100/100'" alt="企业logo" class="enterprise-logo">
                <div class="enterprise-info">
                  <h3>{{ enterprise.enterpriseName }}</h3>
                  <div class="enterprise-meta">
                    <span class="meta-item">{{ enterprise.enterpriseClassification || '其他' }}</span>
                    <span class="meta-item">{{ enterprise.city || '其他' }}</span>
                    <span class="meta-item">{{ enterprise.province || '' }}</span>
                    <span class="recommendation-score">推荐度：{{ enterprise.recommendationScore || '暂无' }}</span>
                  </div>
                </div>
              </div>
              
              <div class="enterprise-tags">
                <!-- 后端没有tags字段，暂时不显示 -->
              </div>
              
              <div class="enterprise-description">
                {{ enterprise.address || '暂无描述' }}
              </div>
              
              <div class="enterprise-actions">
                <el-button type="primary" size="small" @click="handleViewDetail(enterprise)">查看详情</el-button>
                <el-button 
                  :type="favoriteStatus[enterprise.enterpriseId || enterprise.id] ? 'warning' : ''" 
                  size="small" 
                  @click="handleFavorite(enterprise)"
                >
                  {{ favoriteStatus[enterprise.enterpriseId || enterprise.id] ? '已收藏' : '收藏' }}
                </el-button>
              </div>
            </el-card>
          </div>
          
          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="pageSize = $event; search()"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="职位" name="position">
        <div class="filter-section">
          <div class="filter-item">
            <label>职位类型：</label>
            <el-select v-model="filters.jobType" placeholder="选择职位类型" @change="handleFilterChange">
              <el-option v-for="option in jobTypeOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <label>薪资范围：</label>
            <el-select v-model="filters.salaryRange" placeholder="选择薪资范围" @change="handleFilterChange">
              <el-option v-for="option in salaryRangeOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <label>工作经验：</label>
            <el-select v-model="filters.workExperience" placeholder="选择工作经验" @change="handleFilterChange">
              <el-option v-for="option in workExperienceOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <label>学历要求：</label>
            <el-select v-model="filters.education" placeholder="选择学历要求" @change="handleFilterChange">
              <el-option v-for="option in educationOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <label>排序：</label>
            <el-select v-model="filters.sortBy" placeholder="选择排序" @change="handleFilterChange">
              <el-option v-for="option in sortOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </div>
        </div>
        
        <div class="results-section">
          <div class="results-info">
            <span>共找到 {{ total }} 条职位结果</span>
          </div>
          
          <div class="position-list">
            <el-card 
              v-for="position in paginatedPositionResults" 
              :key="position.positionId" 
              class="position-card"
            >
              <div class="position-header">
                <h3>{{ position.positionName }}</h3>
                <span class="position-salary">{{ position.salary || '薪资面议' }}</span>
              </div>
              
              <div class="position-meta">
                <span class="meta-item">{{ position.enterpriseName || '未知企业' }}</span>
                <span class="meta-item">{{ position.city || '未知城市' }}</span>
                <span class="meta-item">{{ position.jobType || '全职' }}</span>
              </div>
              
              <div class="position-description">
                {{ position.description || '暂无职位描述' }}
              </div>
              
              <div class="position-actions">
                <el-button type="primary" size="small">查看详情</el-button>
                <el-button size="small">申请职位</el-button>
              </div>
            </el-card>
          </div>
          
          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="pageSize = $event; search()"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="校招活动" name="event">
        <div class="results-section">
          <div class="results-info">
            <span>共找到 {{ total }} 条活动结果</span>
          </div>
          
          <div class="event-list">
            <el-card 
              v-for="event in paginatedEventResults" 
              :key="event.id" 
              class="event-card"
            >
              <div class="event-header">
                <h3>{{ event.title }}</h3>
                <el-tag type="success" size="small">{{ event.status === 'upcoming' ? '即将开始' : '已结束' }}</el-tag>
              </div>
              
              <div class="event-info">
                <div class="info-item">
                  <i class="el-icon-location"></i>
                  <span>{{ event.location }}</span>
                </div>
                <div class="info-item">
                  <i class="el-icon-time"></i>
                  <span>{{ event.startTime }} - {{ event.endTime }}</span>
                </div>
                <div class="info-item">
                  <i class="el-icon-office-building"></i>
                  <span>主办方：{{ event.organizer }}</span>
                </div>
                <div class="info-item">
                  <i class="el-icon-user"></i>
                  <span>参与企业：{{ event.participatingEnterprises }}家</span>
                </div>
              </div>
              
              <div class="event-description">
                {{ event.description }}
              </div>
              
              <div class="event-actions">
                <el-button type="primary" size="small">查看详情</el-button>
                <el-button size="small">报名</el-button>
              </div>
            </el-card>
          </div>
          
          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="pageSize = $event; search()"
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
  padding: 20px;
}

.search-header {
  margin-bottom: 30px;
}

.search-header h2 {
  margin-bottom: 15px;
  color: #333;
}

.filter-section {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-item label {
  font-weight: 500;
  color: #606266;
}

.results-section {
  margin-bottom: 30px;
}

.results-info {
  margin-bottom: 20px;
  font-size: 14px;
  color: #606266;
}

.enterprise-list,
.event-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.enterprise-card,
.position-card,
.event-card {
  transition: transform 0.2s;
}

.enterprise-card:hover,
.position-card:hover,
.event-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.enterprise-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.enterprise-logo {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  margin-right: 15px;
  object-fit: cover;
}

.enterprise-info h3 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 18px;
}

.enterprise-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 14px;
  color: #606266;
}

.recommendation-score {
  color: #f56c6c;
  font-weight: 500;
}

.enterprise-tags {
  margin-bottom: 15px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.enterprise-description,
.event-description {
  margin-bottom: 15px;
  color: #606266;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.enterprise-actions,
.event-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 职位卡片样式 */
.position-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.position-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.position-salary {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}

.position-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
  font-size: 14px;
  color: #606266;
}

.position-description {
  margin-bottom: 15px;
  color: #606266;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.position-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.position-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.event-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.event-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.event-info {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>