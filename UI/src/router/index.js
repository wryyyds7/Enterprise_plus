import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'

// 导入路由模块
import authRoutes from './modules/auth'
import aiRoutes from './modules/ai'
import chatRoutes from './modules/chat'
import homeRoutes from './modules/home'
import adminRoutes from './modules/admin'
import enterpriseRoutes from './modules/enterprise'
import userRoutes from './modules/user'
import searchRoutes from './modules/search'
import activityRoutes from './modules/activity'
import advertisementRoutes from './modules/advertisement'
import forumRoutes from './modules/forum'
import eventRoutes from './modules/event'

// 公共路由
const publicRoutes = [
  { path: '/', redirect: '/login' },
  ...authRoutes
]

// 私有路由（需要登录）
const privateRoutes = [
  ...aiRoutes,
  ...chatRoutes,
  ...homeRoutes,
  ...adminRoutes,
  ...enterpriseRoutes,
  ...userRoutes,
  ...searchRoutes,
  ...activityRoutes,
  ...advertisementRoutes,
  ...forumRoutes,
  ...eventRoutes
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...publicRoutes, ...privateRoutes]
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 检查是否已登录
    if (!authStore.isAuthenticated) {
      next('/login')
      return
    }
    
    // 检查token是否过期
    if (authStore.isTokenExpired) {
      try {
        await authStore.refreshToken()
      } catch (error) {
        // 刷新token失败，暂时不清除认证信息，允许继续访问
        // 后续请求可能会因为token过期而失败，但登录流程可以正常完成
        console.error('刷新token失败:', error)
      }
    }
    
    // 检查角色权限
    if (to.meta.roles && to.meta.roles.length > 0) {
      const hasPermission = to.meta.roles.some(role => authStore.hasRole(role))
      if (!hasPermission) {
        next('/403')
        return
      }
    }
    
    next()
  } else {
    // 公共路由，直接放行
    next()
  }
})

export default router