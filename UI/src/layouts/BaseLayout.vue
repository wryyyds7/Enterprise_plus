<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/modules/auth'
import { 
  OfficeBuilding, User, DataAnalysis, Calendar, Bell, PieChart, 
  ChatDotRound, Message, Search, Menu, Fold, ArrowDown 
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const isCollapse = ref(false)

// 导航菜单数据
const menuItems = [
  { 
    path: '/admin/dashboard', 
    name: '仪表盘', 
    icon: 'DataAnalysis', 
    requiresAuth: true, 
    roles: ['ADMIN'] 
  },
  { 
    path: '/admin/enterprises', 
    name: '企业管理', 
    icon: 'OfficeBuilding', 
    requiresAuth: true, 
    roles: ['ADMIN'] 
  },
  { 
    path: '/admin/users', 
    name: '用户管理', 
    icon: 'User', 
    requiresAuth: true, 
    roles: ['ADMIN'] 
  },
  { 
    path: '/admin/content-management', 
    name: '活动管理', 
    icon: 'Calendar', 
    requiresAuth: true, 
    roles: ['ADMIN'] 
  },
  { 
    path: '/admin/event-management', 
    name: '事件管理', 
    icon: 'Bell', 
    requiresAuth: true, 
    roles: ['ADMIN'] 
  },
  { 
    path: '/admin/statistics', 
    name: '统计页面', 
    icon: 'PieChart', 
    requiresAuth: true, 
    roles: ['ADMIN'] 
  },
  { 
    path: '/admin/forum-management', 
    name: '论坛管理', 
    icon: 'ChatDotRound', 
    requiresAuth: true, 
    roles: ['ADMIN'] 
  },
  { 
    path: '/ai', 
    name: 'AI对话', 
    icon: 'ChatDotRound', 
    requiresAuth: true 
  },
  { 
    path: '/chat', 
    name: '聊天管理', 
    icon: 'Message', 
    requiresAuth: true 
  },
  { 
    path: '/search', 
    name: '搜索功能', 
    icon: 'Search', 
    requiresAuth: true 
  }
]

// 检查用户是否有权限访问菜单
const hasAccess = (item) => {
  if (!item.requiresAuth) return true
  if (!authStore.isAuthenticated) return false
  if (item.roles && !item.roles.some(role => authStore.hasRole(role))) return false
  return true
}

// 登出功能
const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="base-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
      <div class="logo">
        <el-icon><OfficeBuilding /></el-icon>
        <span v-if="!isCollapse">校招企业推荐平台</span>
      </div>
      <el-menu
        :collapse="isCollapse"
        default-active="/admin/dashboard"
        class="menu"
        router
        background-color="#001529"
        text-color="#fff"
        active-text-color="#409eff"
      >
        <el-menu-item
          v-for="item in menuItems"
          :key="item.path"
          :index="item.path"
          :disabled="!hasAccess(item)"
        >
          <template #title>
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.name }}</span>
          </template>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <el-button
          @click="isCollapse = !isCollapse"
          type="text"
          :icon="isCollapse ? Menu : Fold"
          class="collapse-btn"
        />
        <el-dropdown class="user-dropdown">
          <el-button type="text" class="user-btn">
            <el-icon><User /></el-icon>
            <span v-if="!isCollapse">用户</span>
            <el-icon v-if="!isCollapse" class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/user/profile')">个人中心</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-aside>
    
    <!-- 主内容区 -->
    <el-main class="main-content">
      <router-view />
    </el-main>
  </div>
</template>

<style scoped>
.base-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: #f5f7fa;
}

/* 侧边栏样式 */
.sidebar {
  background-color: #001529;
  color: white;
  display: flex;
  flex-direction: column;
  width: 200px;
  transition: width 0.3s ease;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 1000;
}

.sidebar:has(.el-menu--collapse) {
  width: 64px;
}

/* Logo样式 */
.logo {
  height: 64px;
  line-height: 64px;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 0 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo .el-icon {
  font-size: 24px;
  color: #409eff;
}

.logo span {
  color: white;
}

/* 菜单样式 */
.menu {
  flex: 1;
  border-right: none;
  background-color: transparent;
  overflow-y: auto;
}

.menu .el-menu-item {
  color: rgba(255, 255, 255, 0.85);
  height: 50px;
  line-height: 50px;
  margin: 0;
  padding: 0 20px;
  transition: all 0.3s ease;
}

.menu .el-menu-item:hover {
  background-color: #1890ff;
  color: white;
}

.menu .el-menu-item.is-active {
  background-color: #1890ff;
  color: white;
}

.menu .el-menu-item .el-icon {
  margin-right: 8px;
  font-size: 16px;
}

/* 侧边栏底部 */
.sidebar-footer {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  margin-top: auto;
}

.collapse-btn {
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 10px;
  transition: all 0.3s ease;
}

.collapse-btn:hover {
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
}

.user-dropdown {
  width: 100%;
  text-align: center;
}

.user-btn {
  color: rgba(255, 255, 255, 0.85);
  width: 100%;
  justify-content: center;
  padding: 0;
  height: 40px;
  transition: all 0.3s ease;
}

.user-btn:hover {
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
}

.user-btn .el-icon {
  margin-right: 8px;
}

/* 主内容区样式 */
.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
  margin-left: 200px;
  transition: margin-left 0.3s ease;
  min-height: 100vh;
}

.base-layout:has(.sidebar:has(.el-menu--collapse)) .main-content {
  margin-left: 64px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }
  
  .sidebar:has(.el-menu--collapse) {
    transform: translateX(0);
  }
  
  .main-content {
    margin-left: 0;
  }
}
</style>