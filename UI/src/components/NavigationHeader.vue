<template>
  <el-header class="navigation-header">
    <div class="header-container">
      <!-- Logo -->
      <div class="logo">
        <router-link to="/home">
          <el-icon class="logo-icon"><OfficeBuilding /></el-icon>
          <span class="logo-text">校招企业推荐平台</span>
        </router-link>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activePath"
        class="nav-menu"
        mode="horizontal"
        router
        overflow-x="auto"
      >
        <!-- 普通用户导航菜单 -->
        <template v-if="!isAdmin">
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/home/recommendation">企业推荐</el-menu-item>
          <el-menu-item index="/home/events">校招活动</el-menu-item>
          <el-menu-item index="/search/result">搜索</el-menu-item>
          <el-sub-menu index="/activity">
            <template #title>活动中心</template>
            <el-menu-item index="/api/activity">活动列表</el-menu-item>
            <el-menu-item index="/home/events/my">我的活动</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="/learning">
            <template #title>学习中心</template>
            <el-menu-item index="/learning/path/list">学习路径</el-menu-item>
            <el-menu-item index="/learning/resource/list">学习资源</el-menu-item>
            <el-menu-item index="/learning/skill/list">技能管理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="/forum">
            <template #title>论坛</template>
            <el-menu-item index="/api/forum/section">论坛板块</el-menu-item>
            <el-menu-item index="/api/forum/topic">主题列表</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/chat/session/list">聊天</el-menu-item>
          <el-menu-item index="/ai/chat">AI助手</el-menu-item>
        </template>
        <!-- 管理员导航菜单 -->
        <template v-else>
          <el-menu-item index="/home">首页</el-menu-item>
          <el-sub-menu index="/admin">
            <template #title>管理中心</template>
            <el-menu-item index="/admin/dashboard">仪表盘</el-menu-item>
            <el-menu-item index="/admin/enterprise-management">企业管理</el-menu-item>
            <el-menu-item index="/admin/user-management">用户管理</el-menu-item>
            <el-menu-item index="/admin/content-management">活动管理</el-menu-item>
            <el-menu-item index="/admin/event-management">事件管理</el-menu-item>
            <el-menu-item index="/admin/statistics">统计页面</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/search/result">搜索</el-menu-item>
          <el-menu-item index="/ai/chat">AI助手</el-menu-item>
        </template>
      </el-menu>

      <!-- 搜索栏 -->
      <div class="search-container">
        <el-dropdown trigger="click" @command="handleFilterCommand">
          <el-button type="primary" class="filter-button">
            筛选
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu class="filter-dropdown">
              <el-dropdown-item>
                <span>搜索类型：</span>
                <el-select v-model="searchType" placeholder="选择搜索类型" style="width: 150px;">
                  <el-option label="全部" value="all"></el-option>
                  <el-option label="企业" value="enterprise"></el-option>
                  <el-option label="职位" value="position"></el-option>
                  <el-option label="活动" value="activity"></el-option>
                </el-select>
              </el-dropdown-item>
              <el-dropdown-item>
                <span>行业：</span>
                <el-select v-model="searchFilters.industry" placeholder="选择行业" style="width: 150px;">
                  <el-option label="全部" value=""></el-option>
                  <el-option label="互联网" value="互联网"></el-option>
                  <el-option label="金融" value="金融"></el-option>
                  <el-option label="教育" value="教育"></el-option>
                  <el-option label="医疗" value="医疗"></el-option>
                  <el-option label="制造业" value="制造业"></el-option>
                  <el-option label="其他" value="其他"></el-option>
                </el-select>
              </el-dropdown-item>
              <el-dropdown-item>
                <span>地区：</span>
                <el-select v-model="searchFilters.location" placeholder="选择地区" style="width: 150px;">
                  <el-option label="全部" value=""></el-option>
                  <el-option label="北京" value="北京"></el-option>
                  <el-option label="上海" value="上海"></el-option>
                  <el-option label="广州" value="广州"></el-option>
                  <el-option label="深圳" value="深圳"></el-option>
                  <el-option label="杭州" value="杭州"></el-option>
                  <el-option label="成都" value="成都"></el-option>
                  <el-option label="武汉" value="武汉"></el-option>
                  <el-option label="西安" value="西安"></el-option>
                </el-select>
              </el-dropdown-item>
              <el-dropdown-item>
                <span>职位类型：</span>
                <el-select v-model="searchFilters.jobType" placeholder="选择职位类型" style="width: 150px;">
                  <el-option label="全部" value=""></el-option>
                  <el-option label="技术岗" value="技术岗"></el-option>
                  <el-option label="产品岗" value="产品岗"></el-option>
                  <el-option label="运营岗" value="运营岗"></el-option>
                  <el-option label="设计岗" value="设计岗"></el-option>
                  <el-option label="市场岗" value="市场岗"></el-option>
                  <el-option label="销售岗" value="销售岗"></el-option>
                </el-select>
              </el-dropdown-item>
              <el-dropdown-item>
                <span>薪资范围：</span>
                <el-select v-model="searchFilters.salary" placeholder="选择薪资范围" style="width: 150px;">
                  <el-option label="全部" value=""></el-option>
                  <el-option label="5k以下" value="5k以下"></el-option>
                  <el-option label="5k-10k" value="5k-10k"></el-option>
                  <el-option label="10k-15k" value="10k-15k"></el-option>
                  <el-option label="15k-20k" value="15k-20k"></el-option>
                  <el-option label="20k以上" value="20k以上"></el-option>
                </el-select>
              </el-dropdown-item>
              <el-dropdown-item divided command="reset">
                <el-button type="default" size="small" style="width: 100%;">重置筛选</el-button>
              </el-dropdown-item>
              <el-dropdown-item divided command="apply">
                <el-button type="primary" size="small" style="width: 100%;">应用筛选</el-button>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索企业名称、职位、活动、关键词" 
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prepend>
            <el-select v-model="searchType" placeholder="全部" size="small" style="width: 100px;">
              <el-option label="全部" value="all"></el-option>
              <el-option label="企业" value="enterprise"></el-option>
              <el-option label="职位" value="position"></el-option>
              <el-option label="活动" value="activity"></el-option>
            </el-select>
          </template>
          <template #append>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>搜索
            </el-button>
          </template>
        </el-input>
      </div>

      <!-- 用户信息 -->
      <div class="user-info">
        <el-dropdown>
          <span class="user-dropdown">
            <el-avatar :size="40" :src="userInfo.avatar">
              {{ userInfo.nickName ? userInfo.nickName.substring(0, 1) : userInfo.userName.substring(0, 1) || 'U' }}
            </el-avatar>
            <span class="user-name">{{ userInfo.nickName || userInfo.userName }}</span>
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleUserCenter">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item @click="handleMyFavorites">
                <el-icon><Star /></el-icon>
                我的收藏
              </el-dropdown-item>
              <el-dropdown-item @click="handleMyEvents">
                <el-icon><Calendar /></el-icon>
                我的活动
              </el-dropdown-item>
              <el-dropdown-item v-if="isAdmin" @click="handleAdminCenter">
                <el-icon><Setting /></el-icon>
                管理员中心
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown, User, Star, Calendar, Setting, SwitchButton, Search, OfficeBuilding } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { useAuthStore } from '@/store/modules/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const authStore = useAuthStore()

// 搜索功能
const searchKeyword = ref('')
const searchType = ref('all') // 搜索类型：all, enterprise, position, activity
const searchFilters = ref({
  industry: '',
  location: '',
  jobType: '',
  salary: ''
})

// 处理搜索
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/search/result', 
      query: {
        keyword: searchKeyword.value.trim(),
        searchType: searchType.value,
        industry: searchFilters.value.industry,
        location: searchFilters.value.location,
        jobType: searchFilters.value.jobType,
        salary: searchFilters.value.salary
      }
    })
  } else {
    ElMessage.warning('请输入搜索关键词')
  }
}

// 处理筛选命令
const handleFilterCommand = (command) => {
  if (command === 'apply') {
    // 应用筛选并执行搜索
    handleSearch()
  } else if (command === 'reset') {
    // 重置筛选条件
    searchFilters.value = {
      industry: '',
      location: '',
      jobType: '',
      salary: ''
    }
    searchType.value = 'all'
  }
}

// 判断是否为管理员
const isAdmin = computed(() => {
  const user = authStore.userInfo || userStore.currentUser
  return user?.userType === 'ADMIN' || user?.userRoles?.includes('ADMIN')
})

// 获取当前用户信息
const userInfo = computed(() => {
  return authStore.userInfo || userStore.currentUser || {
    userId: null,
    userName: '',
    nickName: '',
    userType: '',
    avatar: ''
  }
})

// 加载用户信息
onMounted(async () => {
  if (authStore.isAuthenticated && authStore.userInfo?.userId) {
    await userStore.getUserInfo(authStore.userInfo.userId)
  }
})

// 获取当前激活的路径
const activePath = computed(() => {
  return route.path
})

// 处理个人中心
const handleUserCenter = () => {
  router.push('/profile')
}

// 处理我的收藏
const handleMyFavorites = () => {
  router.push('/home/enterprise/favorites')
}

// 处理我的活动
const handleMyEvents = () => {
  router.push('/home/events/my')
}

// 处理管理员中心
const handleAdminCenter = () => {
  router.push('/admin/dashboard')
}

// 处理退出登录
const handleLogout = async () => {
  ElMessage.confirm('确定要退出登录吗？', '退出确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    // 调用登出API
    await authStore.logout()
    ElMessage.success('退出登录成功')
  }).catch(() => {
    ElMessage.info('已取消退出操作')
  })
}
</script>

<style scoped>
.navigation-header {
  background-color: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  padding: 0;
  height: 60px;
  line-height: 60px;
  position: sticky;
  top: 0;
  z-index: 1000;
  width: 100%;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  box-sizing: border-box;
}

/* Logo样式 */
.logo {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-right: 30px;
}

.logo-icon {
  font-size: 24px;
  color: #409eff;
}

.logo-text {
  font-size: 22px;
  font-weight: bold;
  color: #409eff;
  text-decoration: none;
  transition: color 0.3s ease;
}

.logo-text:hover {
  color: #66b1ff;
}

/* 导航菜单样式 */
.nav-menu {
  flex: 1;
  background-color: transparent;
  border-bottom: none;
  margin-right: 20px;
  overflow-x: auto;
  white-space: nowrap;
}

:deep(.el-menu-item), :deep(.el-sub-menu__title) {
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  color: #333;
}

:deep(.el-menu-item:hover), :deep(.el-sub-menu__title:hover) {
  color: #409eff;
  background-color: rgba(64, 158, 255, 0.1) !important;
}

:deep(.el-menu-item.is-active) {
  color: #409eff !important;
  background-color: rgba(64, 158, 255, 0.1) !important;
}

:deep(.el-menu-item.is-active::after) {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  height: 3px;
  background-color: #409eff;
  border-radius: 3px 3px 0 0;
}

/* 搜索栏样式 */
.search-container {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 10px;
  max-width: 500px;
}

.search-input {
  flex: 1;
  width: 100%;
  min-width: 200px;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
}

.search-input:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.search-input .el-input__prepend),
:deep(.search-input .el-input__append) {
  background-color: transparent;
}

:deep(.search-input .el-select__wrapper) {
  border-radius: 20px 0 0 20px;
  border-right: none;
}

:deep(.search-input .el-button) {
  border-radius: 0 20px 20px 0;
}

.filter-button {
  white-space: nowrap;
  border-radius: 20px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 5px;
}

.filter-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 筛选下拉菜单 */
.filter-dropdown {
  min-width: 300px;
  padding: 10px;
}

:deep(.filter-dropdown .el-dropdown-item) {
  padding: 10px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.filter-dropdown .el-dropdown-item:last-child) {
  border-bottom: none;
}

:deep(.filter-dropdown .el-select) {
  margin-left: 10px;
}

/* 用户信息样式 */
.user-info {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 20px;
  transition: all 0.3s ease;
  background-color: transparent;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.user-name {
  margin: 0 8px;
  font-weight: 500;
  color: #333;
}

.user-dropdown .el-icon {
  font-size: 16px;
  color: #666;
  transition: color 0.3s ease;
}

.user-dropdown:hover .el-icon {
  color: #409eff;
}

/* 下拉菜单样式 */
:deep(.el-dropdown-menu) {
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  border: none;
  overflow: hidden;
}

:deep(.el-dropdown-menu__item) {
  transition: all 0.3s ease;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: #f0f9ff;
  color: #409eff;
}

:deep(.el-dropdown-menu__item.is-divided) {
  border-color: #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .header-container {
    padding: 0 15px;
  }
  
  .logo {
    margin-right: 20px;
  }
  
  .logo-text {
    font-size: 20px;
  }
  
  :deep(.el-menu-item), :deep(.el-sub-menu__title) {
    font-size: 14px;
    padding: 0 12px;
  }
  
  .search-container {
    max-width: 400px;
  }
  
  .search-input {
    min-width: 150px;
  }
}

@media (max-width: 992px) {
  .header-container {
    flex-wrap: wrap;
    height: auto;
    padding: 10px 15px;
    line-height: normal;
  }
  
  .logo {
    margin-right: 20px;
    margin-bottom: 10px;
  }
  
  .nav-menu {
    order: 3;
    flex: 1 0 100%;
    margin-right: 0;
    margin-bottom: 10px;
    overflow-x: auto;
  }
  
  .search-container {
    order: 2;
    max-width: 100%;
    flex: 1;
    margin-bottom: 10px;
  }
  
  .user-info {
    order: 1;
    margin-left: auto;
    margin-bottom: 10px;
  }
  
  .search-input {
    min-width: auto;
  }
}

@media (max-width: 768px) {
  .header-container {
    padding: 8px 10px;
  }
  
  .logo-text {
    font-size: 18px;
  }
  
  .logo-icon {
    font-size: 20px;
  }
  
  :deep(.el-menu-item), :deep(.el-sub-menu__title) {
    font-size: 13px;
    padding: 0 10px;
    height: 50px;
    line-height: 50px;
  }
  
  .search-container {
    gap: 8px;
  }
  
  .filter-button {
    font-size: 13px;
    padding: 6px 12px;
  }
  
  .search-input {
    font-size: 13px;
  }
  
  .user-name {
    display: none;
  }
}
</style>