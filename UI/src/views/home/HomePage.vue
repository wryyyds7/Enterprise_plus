<template>
  <div class="home-page">
    <!-- 导航头 -->
    <NavigationHeader />
    
    <!-- 主要内容区域 -->
    <div class="home-content-container">
      <!-- 轮播图 -->
      <section class="home-banner">
        <el-carousel height="400px" indicator-position="outside" arrow="always">
          <el-carousel-item v-for="event in bannerEvents" :key="event.id">
            <div class="banner-item" :style="{ backgroundImage: `url(${event.imageUrl})` }">
              <div class="banner-content">
                <h3>{{ event.title }}</h3>
                <p>{{ event.description }}</p>
                <el-button type="primary" @click="handleBannerClick(event)">{{ event.actionText || '了解详情' }}</el-button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <!-- 职业分类导航 -->
      <section class="career-category">
        <div class="section-header">
          <h2>职业分类</h2>
        </div>
        <div class="category-grid">
          <el-card v-for="category in careerCategories" :key="category.id" class="category-card" @click="handleCategoryClick(category)">
            <template #header>
              <div class="category-header">
                <h3>{{ category.name }}</h3>
              </div>
            </template>
            <div class="category-tags">
              <span v-for="tag in category.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </el-card>
        </div>
      </section>

      <!-- 页面头部 -->
      <header class="home-header">
        <h1>校招企业推荐平台</h1>
        <p>为大学生推荐最适合的校招企业</p>
      </header>

      <!-- 主要内容区域 -->
      <main class="home-content">
        <!-- 个性化企业推荐区域 -->
        <section class="recommendation-section">
          <div class="section-header">
            <h2>为你推荐</h2>
            <router-link to="/home/recommendation" class="more-link">查看更多</router-link>
          </div>
          <div class="enterprise-grid">
            <!-- 企业推荐卡片列表 -->
            <div class="enterprise-card" v-for="enterprise in recommendedEnterprises" :key="enterprise.id">
              <div class="enterprise-logo">
                <img :src="enterprise.logo" :alt="enterprise.name" />
              </div>
              <div class="enterprise-info">
                <h3 class="enterprise-name">{{ enterprise.name }}</h3>
                <p class="enterprise-industry">{{ enterprise.industry }}</p>
                <div class="enterprise-tags">
                  <span v-for="tag in enterprise.tags" :key="tag" class="tag">{{ tag }}</span>
                </div>
                <div class="enterprise-actions">
                  <button class="detail-btn" @click="viewEnterpriseDetail(enterprise.id)">查看详情</button>
                  <button class="favorite-btn" @click="toggleFavorite(enterprise.id)">
                    {{ isFavorite(enterprise.id) ? '已收藏' : '收藏' }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 校园招聘活动区域 -->
        <section class="events-section">
          <div class="section-header">
            <h2>校园招聘活动</h2>
            <router-link to="/home/events" class="more-link">查看更多</router-link>
          </div>
          <div class="event-list">
            <!-- 活动列表 -->
            <div class="event-card" v-for="event in upcomingEvents" :key="event.id">
              <div class="event-date">
                <span class="month">{{ event.date.month }}</span>
                <span class="day">{{ event.date.day }}</span>
              </div>
              <div class="event-info">
                <h3 class="event-name">{{ event.name }}</h3>
                <p class="event-location">{{ event.location }}</p>
                <p class="event-time">{{ event.time }}</p>
                <div class="event-actions">
                  <button class="detail-btn" @click="viewEventDetail(event.id)">查看详情</button>
                  <button class="register-btn" @click="registerEvent(event.id)">立即报名</button>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 热门搜索区域 -->
        <section class="search-section">
          <div class="section-header">
            <h2>热门搜索</h2>
          </div>
          <div class="hot-tags">
            <span v-for="tag in hotSearchTags" :key="tag" class="hot-tag" @click="search(tag)">
              {{ tag }}
            </span>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import NavigationHeader from '@/components/NavigationHeader.vue'
import eventApi from '@/api/modules/event'
import homeApi from '@/api/modules/home'
import enterpriseApi from '@/api/modules/enterprise'

const router = useRouter()

// 轮播图事件数据
const bannerEvents = ref([])

// 职业分类数据
const careerCategories = ref([
  {
    id: 1,
    name: '互联网/AI',
    tags: ['Java', 'Python', '人工智能', '前端开发', '后端开发', '算法工程师'],
    industry: '互联网'
  },
  {
    id: 2,
    name: '产品',
    tags: ['产品经理', '产品专员/助理', '产品总监', '用户研究', '电商产品经理', 'AI产品经理'],
    industry: '产品'
  },
  {
    id: 3,
    name: '销售',
    tags: ['销售专员', '电话销售', '网络销售', '销售经理', '大客户销售'],
    industry: '销售'
  },
  {
    id: 4,
    name: '金融',
    tags: ['金融分析师', '银行柜员', '理财顾问', '证券经纪人', '保险代理人'],
    industry: '金融'
  },
  {
    id: 5,
    name: '教育',
    tags: ['教师', '教育顾问', '课程顾问', '培训师', '教育产品经理'],
    industry: '教育'
  },
  {
    id: 6,
    name: '制造业',
    tags: ['机械工程师', '电气工程师', '自动化工程师', '质量工程师', '生产管理'],
    industry: '制造业'
  }
])

// 推荐企业数据
const recommendedEnterprises = ref([])

// 收藏状态
const favoriteStatus = ref({})

// 即将举行的活动
const upcomingEvents = ref([])

// 热门搜索标签
const hotSearchTags = ref(['互联网', '金融', '制造业', '人工智能', '软件工程师', '产品经理'])

// 处理轮播图点击
const handleBannerClick = (event) => {
  console.log('Banner clicked:', event)
  // 根据事件类型跳转到不同页面
  if (event.type === 'recruitment') {
    router.push('/home/events')
  } else if (event.type === 'industry') {
    router.push({ path: '/search/result', query: { keyword: event.industry || '互联网' } })
  } else if (event.type === 'enterprise') {
    router.push('/home/enterprise-recommendation')
  }
}

// 处理职业分类点击
const handleCategoryClick = (category) => {
  console.log('Category clicked:', category)
  // 跳转到搜索结果页，带上行业和标签条件，优先显示同一城市的职位
  router.push({ 
    path: '/search/result', 
    query: { 
      keyword: category.name, 
      industry: category.industry,
      // 默认使用当前城市，这里可以从用户信息或浏览器获取
      location: '长沙' // 示例：优先显示长沙的职位
    } 
  })
}

// 查看企业详情
const viewEnterpriseDetail = (enterpriseId) => {
  router.push(`/home/enterprise/${enterpriseId}`)
}

// 查看活动详情
const viewEventDetail = (eventId) => {
  router.push(`/home/event/${eventId}`)
}

// 收藏/取消收藏企业
const toggleFavorite = async (enterpriseId) => {
  const isFav = favoriteStatus.value[enterpriseId]
  try {
    if (isFav) {
      await homeApi.unfavoriteEnterprise(enterpriseId)
      favoriteStatus.value[enterpriseId] = false
      ElMessage.success('取消收藏成功')
    } else {
      await homeApi.favoriteEnterprise({ enterpriseId })
      favoriteStatus.value[enterpriseId] = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error(isFav ? '取消收藏失败' : '收藏失败')
  }
}

// 检查企业是否已收藏
const isFavorite = (enterpriseId) => {
  return favoriteStatus.value[enterpriseId] || false
}

// 报名活动
const registerEvent = (eventId) => {
  router.push({ path: '/home/campusRecruitmentEvent', query: { eventId } })
}

// 搜索
const search = (keyword) => {
  router.push({ path: '/search/result', query: { keyword } })
}

// 加载数据
const loadData = async () => {
  try {
    // 加载轮播图事件 - 使用事件模块的API
    const bannerResult = await eventApi.getEventList({ position: 'homepage' })
    if (bannerResult.code === 200 && bannerResult.data) {
      // 转换事件数据格式，适配轮播图组件
      bannerEvents.value = (bannerResult.data.records || bannerResult.data).map(event => ({
        id: event.id,
        title: event.title || '未命名事件',
        description: event.description || '暂无描述',
        imageUrl: event.imageUrl || 'https://picsum.photos/id/1005/1200/400',
        actionText: '了解详情',
        type: event.eventType || 'default'
      }))
    }
    
    // 加载推荐企业
    const enterpriseResult = await homeApi.getEnterpriseRecommendations()
    const isSuccess = enterpriseResult.code === 200 || enterpriseResult.success
    if (isSuccess && enterpriseResult.data) {
      const data = enterpriseResult.data
      if (Array.isArray(data)) {
        recommendedEnterprises.value = data.slice(0, 4)
      } else if (data.records) {
        recommendedEnterprises.value = data.records.slice(0, 4)
      } else if (data.data && Array.isArray(data.data)) {
        recommendedEnterprises.value = data.data.slice(0, 4)
      } else {
        recommendedEnterprises.value = []
      }
    } else {
      // 推荐为空时，加载企业列表作为冷启动 fallback
      const listResult = await enterpriseApi.getEnterpriseList({ pageNum: 1, pageSize: 4 })
      if (listResult.code === 200 && listResult.data) {
        recommendedEnterprises.value = (listResult.data.records || listResult.data || []).slice(0, 4)
      }
    }
    
    // 加载即将举行的活动
    const eventResult = await eventApi.getEventList({ pageNum: 1, pageSize: 3 })
    if (eventResult.code === 200 && eventResult.data) {
      upcomingEvents.value = (eventResult.data.records || eventResult.data).map(event => ({
        id: event.id,
        name: event.title || '未命名活动',
        location: event.location || '未知地点',
        time: event.startTime ? new Date(event.startTime).toLocaleString() : '未知时间',
        date: {
          month: event.startTime ? new Date(event.startTime).getMonth() + 1 : '0',
          day: event.startTime ? new Date(event.startTime).getDate() : '0'
        }
      }))
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.home-content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  flex: 1;
}

/* 轮播图样式 */
.home-banner {
  margin-bottom: 30px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.banner-item {
  height: 400px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  color: white;
}

.banner-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.banner-content {
  position: relative;
  z-index: 2;
  text-align: center;
  max-width: 800px;
  padding: 20px;
}

.banner-content h3 {
  font-size: 32px;
  margin-bottom: 16px;
  font-weight: bold;
}

.banner-content p {
  font-size: 18px;
  margin-bottom: 24px;
  opacity: 0.9;
}

/* 职业分类样式 */
.career-category {
  margin-bottom: 30px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.category-card {
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.category-header {
  text-align: center;
}

.category-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.category-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-top: 10px;
}

.category-tags .tag {
  background-color: #e9ecef;
  color: #495057;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.home-header {
  text-align: center;
  margin-bottom: 40px;
}

.home-header h1 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 10px;
}

.home-header p {
  font-size: 1.2rem;
  color: #666;
}

.home-content {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.recommendation-section,
.events-section,
.search-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 1.8rem;
  color: #333;
  margin: 0;
}

.more-link {
  color: #409eff;
  text-decoration: none;
  font-size: 1rem;
}

.more-link:hover {
  text-decoration: underline;
}

.enterprise-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.enterprise-card {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  transition: transform 0.2s ease;
}

.enterprise-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.enterprise-logo {
  margin-bottom: 15px;
}

.enterprise-logo img {
  width: 80px;
  height: 80px;
  object-fit: contain;
}

.enterprise-name {
  font-size: 1.2rem;
  margin: 0 0 5px 0;
  color: #333;
}

.enterprise-industry {
  color: #666;
  margin: 0 0 10px 0;
}

.enterprise-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 5px;
  margin-bottom: 15px;
}

.tag {
  background-color: #e9ecef;
  color: #495057;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
}

.enterprise-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.detail-btn,
.favorite-btn,
.register-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s ease;
}

.detail-btn {
  background-color: #409eff;
  color: white;
}

.detail-btn:hover {
  background-color: #66b1ff;
}

.favorite-btn {
  background-color: #f56c6c;
  color: white;
}

.favorite-btn:hover {
  background-color: #f78989;
}

.register-btn {
  background-color: #67c23a;
  color: white;
}

.register-btn:hover {
  background-color: #85ce61;
}

.event-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.event-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  transition: transform 0.2s ease;
}

.event-card:hover {
  transform: translateX(5px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.event-date {
  background-color: #409eff;
  color: white;
  text-align: center;
  padding: 10px;
  border-radius: 8px;
  min-width: 80px;
}

.event-date .month {
  display: block;
  font-size: 0.9rem;
  font-weight: bold;
}

.event-date .day {
  display: block;
  font-size: 1.8rem;
  font-weight: bold;
}

.event-info {
  flex: 1;
}

.event-name {
  font-size: 1.3rem;
  margin: 0 0 5px 0;
  color: #333;
}

.event-time,
.event-location {
  color: #666;
  margin: 5px 0;
  font-size: 0.9rem;
}

.event-actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hot-tag {
  background-color: #e9ecef;
  color: #495057;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.hot-tag:hover {
  background-color: #dee2e6;
  color: #212529;
  transform: translateY(-2px);
}
</style>
