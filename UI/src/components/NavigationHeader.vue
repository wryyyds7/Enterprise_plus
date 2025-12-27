<template>
  <el-header class="navigation-header">
    <div class="header-container">
      <!-- Logo -->
      <div class="logo">
        <router-link to="/home">
          <span class="logo-text">校招企业推荐平台</span>
        </router-link>
      </div>

      <!-- 搜索栏 -->
      <div class="search-container">
        <el-dropdown trigger="click" @command="handleFilterCommand">
          <el-button type="info" class="filter-button">
            筛选
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item divided>
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
              <el-dropdown-item divided>
                <span>地区：</span>
                <el-select v-model="searchFilters.location" placeholder="选择地区" style="width: 150px;">
                  <el-option label="全部" value=""></el-option>
                  <el-option label="北京" value="北京"></el-option>
                  <el-option label="上海" value="上海"></el-option>
                  <el-option label="广州" value="广州"></el-option>
                  <el-option label="深圳" value="深圳"></el-option>
                  <el-option label="杭州" value="杭州"></el-option>
                </el-select>
              </el-dropdown-item>
              <el-dropdown-item divided command="apply">
                <el-button type="primary" size="small" style="width: 100%;">应用筛选</el-button>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索企业名称、行业、关键词" 
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activePath"
        class="nav-menu"
        mode="horizontal"
        router
      >
        <el-menu-item index="/home">首页</el-menu-item>
        <el-menu-item index="/home/recommendation">企业推荐</el-menu-item>
        <el-menu-item index="/home/events">校招活动</el-menu-item>
        <el-sub-menu index="/home/enterprise">
          <template #title>企业中心</template>
          <el-menu-item index="/home/enterprise/list">企业列表</el-menu-item>
          <el-menu-item index="/home/enterprise/favorites">我的收藏</el-menu-item>
        </el-sub-menu>
          <el-sub-menu index="/learning">
          <template #title>学习中心</template>
          <el-menu-item index="/learning/path/list">学习路径</el-menu-item>
          <el-menu-item index="/learning/resource/list">学习资源</el-menu-item>
          <el-menu-item index="/learning/skill/list">技能管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/api/forum">
          <template #title>论坛</template>
          <el-menu-item index="/api/forum/section">论坛板块</el-menu-item>
          <el-menu-item index="/api/forum/topic">主题列表</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/api/activity">活动中心</el-menu-item>
        <el-menu-item index="/chat/session/list">聊天</el-menu-item>
        <el-menu-item index="/ai/chat">AI助手</el-menu-item>
        <el-menu-item index="/profile">我的</el-menu-item>
        <el-menu-item v-if="isAdmin" index="/admin/dashboard">管理员中心</el-menu-item>
      </el-menu>

      <!-- 用户信息 -->
      <div class="user-info">
        <el-dropdown>
          <span class="user-dropdown">
            <el-avatar :size="40">
              {{ userInfo.nickName ? userInfo.nickName.substring(0, 1) : 'U' }}
            </el-avatar>
            <span class="user-name">{{ userInfo.nickName || userInfo.userName }}</span>
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleUserCenter">
                <el-icon><user /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item @click="handleMyFavorites">
                <el-icon><star /></el-icon>
                我的收藏
              </el-dropdown-item>
              <el-dropdown-item @click="handleMyEvents">
                <el-icon><calendar /></el-icon>
                我的活动
              </el-dropdown-item>
              <el-dropdown-item v-if="isAdmin" @click="handleAdminCenter">
                <el-icon><setting /></el-icon>
                管理员中心
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon><switch-button /></el-icon>
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
import { ArrowDown, User, Star, Calendar, Setting, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { useAuthStore } from '@/store/modules/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const authStore = useAuthStore()

// 搜索功能
const searchKeyword = ref('')
const searchFilters = ref({
  industry: '',
  location: ''
})

// 处理搜索
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/search/result', 
      query: {
        keyword: searchKeyword.value.trim(),
        industry: searchFilters.value.industry,
        location: searchFilters.value.location
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
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  flex: 0 0 auto;
  margin-right: 50px;
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

/* 搜索栏样式 */
.search-container {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  max-width: 500px;
}

.search-input {
  flex: 1;
  width: 100%;
  max-width: 400px;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
}

.search-input:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.filter-button {
  white-space: nowrap;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.filter-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.nav-menu {
  flex: 2;
  background-color: transparent;
  border-bottom: none;
  margin-right: 30px;
}

/* 导航菜单项样式 */
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

/* 用户信息样式 */
.user-info {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
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
</style>