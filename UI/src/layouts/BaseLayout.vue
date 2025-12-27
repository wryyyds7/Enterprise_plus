<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/modules/auth'

const router = useRouter()
const authStore = useAuthStore()
const isCollapse = ref(false)

// 导航菜单数据
const menuItems = [
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
    path: '/enterprise',
    name: '企业管理',
    icon: 'OfficeBuilding',
    requiresAuth: true,
    roles: ['admin']
  },
  {
    path: '/user',
    name: '用户管理',
    icon: 'User',
    requiresAuth: true,
    roles: ['admin']
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
        <el-icon><ChatRound /></el-icon>
        <span v-if="!isCollapse">企业对话系统</span>
      </div>
      <el-menu
        :collapse="isCollapse"
        default-active="/ai"
        class="menu"
        router
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
    </el-aside>
    
    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            @click="isCollapse = !isCollapse"
            type="text"
            :icon="isCollapse ? 'MenuUnfold' : 'MenuFold'"
          />
        </div>
        <div class="header-right">
          <el-dropdown>
            <el-button type="text">
              <el-icon><User /></el-icon>
              <span>用户</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/user/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.base-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  background-color: #001529;
  color: white;
  display: flex;
  flex-direction: column;
}

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
}

.logo .el-icon {
  font-size: 24px;
}

.menu {
  flex: 1;
  border-right: none;
}

.menu .el-menu-item {
  color: white;
}

.menu .el-menu-item.is-active {
  background-color: #1890ff;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background-color: white;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);
}

.header-left, .header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.main-content {
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}
</style>