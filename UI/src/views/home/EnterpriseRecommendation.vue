 <template>
  <div class="enterprise-recommendation">
    <NavigationHeader />
    <div class="recommendation-container">
      <div class="recommendation-header">
      <h2>个性化企业推荐</h2>
      <div class="filter-options">
        <el-select v-model="filters.major" placeholder="选择专业" style="width: 150px; margin-right: 10px;">
          <el-option label="计算机科学" value="computer-science" />
          <el-option label="电子工程" value="electronic-engineering" />
          <el-option label="机械工程" value="mechanical-engineering" />
          <el-option label="金融" value="finance" />
        </el-select>
        <el-select v-model="filters.industry" placeholder="选择行业" style="width: 150px; margin-right: 10px;">
          <el-option label="互联网" value="internet" />
          <el-option label="金融科技" value="fintech" />
          <el-option label="制造业" value="manufacturing" />
          <el-option label="教育" value="education" />
        </el-select>
        <el-select v-model="filters.sortBy" placeholder="排序方式" style="width: 150px;">
          <el-option label="推荐度" value="relevance" />
          <el-option label="企业规模" value="size" />
          <el-option label="行业排名" value="industry-rank" />
        </el-select>
      </div>
    </div>
    
    <div class="enterprise-list">
      <el-card v-for="enterprise in enterprises" :key="enterprise.id" class="enterprise-card">
        <template #header>
          <div class="card-header">
            <div class="enterprise-logo">
              <img :src="enterprise.logo" :alt="enterprise.name" />
            </div>
            <div class="enterprise-info">
              <h3>{{ enterprise.name }}</h3>
              <div class="enterprise-tags">
                <el-tag v-for="tag in enterprise.tags" :key="tag" size="small" :type="getTagType(tag)">
                  {{ tag }}
                </el-tag>
              </div>
            </div>
            <div class="favorite-button">
              <el-button 
                type="primary" 
                :icon="enterprise.isFavorited ? 'StarFilled' : 'Star'"
                :class="{ 'is-favorited': enterprise.isFavorited }"
                @click="toggleFavorite(enterprise)"
              >
                {{ enterprise.isFavorited ? '已收藏' : '收藏' }}
              </el-button>
            </div>
          </div>
        </template>
        
        <div class="card-body">
          <p class="enterprise-description">{{ enterprise.description }}</p>
          <div class="enterprise-details">
            <div class="detail-item">
              <span class="label">所属行业：</span>
              <span class="value">{{ enterprise.industry }}</span>
            </div>
            <div class="detail-item">
              <span class="label">企业规模：</span>
              <span class="value">{{ enterprise.size }}</span>
            </div>
            <div class="detail-item">
              <span class="label">校招岗位：</span>
              <span class="value">{{ enterprise.positionTypes.join(', ') }}</span>
            </div>
            <div class="detail-item">
              <span class="label">地理位置：</span>
              <span class="value">{{ enterprise.location }}</span>
            </div>
          </div>
        </div>
        
        <template #footer>
          <el-button type="primary" @click="viewEnterpriseDetail(enterprise.id)">查看详情</el-button>
        </template>
      </el-card>
    </div>
    
    <div class="pagination">
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalEnterprises"
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

// 筛选条件
const filters = ref({
  major: '',
  industry: '',
  sortBy: 'relevance'
})

// 企业列表
const enterprises = ref([])

// 分页信息
const currentPage = ref(1)
const pageSize = ref(10)
const totalEnterprises = ref(0)

// 模拟数据
const mockEnterprises = [
  {
    id: 1,
    name: '科技有限公司',
    logo: 'https://via.placeholder.com/80x80',
    description: '一家专注于人工智能和机器学习的高科技企业，致力于为客户提供最先进的技术解决方案。',
    industry: '互联网',
    size: '1000-5000人',
    positionTypes: ['前端开发', '后端开发', '算法工程师'],
    location: '北京',
    tags: ['国企', '上市公司'],
    isFavorited: false
  },
  {
    id: 2,
    name: '金融科技公司',
    logo: 'https://via.placeholder.com/80x80',
    description: '领先的金融科技服务提供商，为金融机构提供创新的技术解决方案。',
    industry: '金融科技',
    size: '500-1000人',
    positionTypes: ['金融分析师', '软件开发', '产品经理'],
    location: '上海',
    tags: ['上市公司'],
    isFavorited: true
  },
  {
    id: 3,
    name: '智能制造企业',
    logo: 'https://via.placeholder.com/80x80',
    description: '专注于工业自动化和智能制造的企业，为制造业客户提供完整的解决方案。',
    industry: '制造业',
    size: '2000-5000人',
    positionTypes: ['机械工程师', '电气工程师', '自动化工程师'],
    location: '广州',
    tags: ['国企'],
    isFavorited: false
  },
  {
    id: 4,
    name: '教育科技有限公司',
    logo: 'https://via.placeholder.com/80x80',
    description: '致力于教育创新的科技公司，为学校和学生提供在线教育平台和服务。',
    industry: '教育',
    size: '100-500人',
    positionTypes: ['前端开发', '教育产品经理', '内容编辑'],
    location: '杭州',
    tags: ['创业公司'],
    isFavorited: false
  }
]

// 获取企业推荐列表
const getEnterpriseRecommendations = async () => {
  const loading = ElLoading.service({
    lock: true,
    text: '加载中...',
    background: 'rgba(255, 255, 255, 0.7)'
  })
  
  try {
    const response = await homeApi.getEnterpriseRecommendations({
      major: filters.value.major,
      industry: filters.value.industry,
      sortBy: filters.value.sortBy,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    
    enterprises.value = response.data || []
    totalEnterprises.value = response.total || 0
  } catch (error) {
    console.error('获取企业推荐列表失败:', error)
    ElMessage.warning('获取数据失败，使用模拟数据')
    // API调用失败时使用mock数据作为后备
    enterprises.value = mockEnterprises
    totalEnterprises.value = mockEnterprises.length
  } finally {
    loading.close()
  }
}

// 切换收藏状态
const toggleFavorite = async (enterprise) => {
  const action = enterprise.isFavorited ? '取消收藏' : '收藏'
  
  try {
    if (enterprise.isFavorited) {
      // 取消收藏
      await homeApi.unfavoriteEnterprise(enterprise.id)
    } else {
      // 添加收藏
      await homeApi.favoriteEnterprise({ enterpriseId: enterprise.id })
    }
    
    // 更新本地状态
    enterprise.isFavorited = !enterprise.isFavorited
    ElMessage.success(`${action}成功！`)
  } catch (error) {
    console.error(`${action}失败:`, error)
    ElMessage.error(`${action}失败，请稍后重试`)
  }
}

// 查看企业详情
const viewEnterpriseDetail = (enterpriseId) => {
  router.push(`/home/enterprise/${enterpriseId}`)
}

// 分页处理
const handleCurrentChange = (page) => {
  currentPage.value = page
  getEnterpriseRecommendations()
}

// 获取标签类型
const getTagType = (tag) => {
  const tagTypes = {
    '国企': 'success',
    '上市公司': 'primary',
    '创业公司': 'warning'
  }
  return tagTypes[tag] || 'info'
}

// 组件挂载时获取数据
onMounted(() => {
  getEnterpriseRecommendations()
})
</script>

<style scoped>
.enterprise-recommendation {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.recommendation-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  flex: 1;
}

.recommendation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.recommendation-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.filter-options {
  display: flex;
  gap: 10px;
}

.enterprise-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.enterprise-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 15px;
}

.enterprise-logo {
  width: 80px;
  height: 80px;
  overflow: hidden;
  border-radius: 8px;
}

.enterprise-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.enterprise-info {
  flex: 1;
}

.enterprise-info h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #333;
}

.enterprise-tags {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.favorite-button {
  margin-left: auto;
}

.favorite-button .is-favorited {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.card-body {
  flex: 1;
  padding: 15px 0;
}

.enterprise-description {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.enterprise-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  font-size: 13px;
  display: flex;
}

.detail-item .label {
  font-weight: 500;
  color: #333;
  min-width: 80px;
}

.detail-item .value {
  color: #666;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>