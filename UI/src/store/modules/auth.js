import { defineStore } from 'pinia'
import { auth as authApi } from '@/api'
import router from '@/router'
import { getRedirectPathByRole } from '@/utils/redirect'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    userInfo: null,           // 用户详细信息
    token: localStorage.getItem('token') || '',  // 认证token
    expireTime: localStorage.getItem('expireTime') || null,  // token过期时间
    isAuthenticated: !!localStorage.getItem('token'),  // 是否已认证
    userRoles: JSON.parse(localStorage.getItem('userRoles') || '[]'),  // 用户角色
    loading: false  // 加载状态
  }),
  
  getters: {
    // 检查token是否过期
    isTokenExpired: (state) => {
      if (!state.expireTime) return false // 如果没有过期时间，认为token有效
      return new Date().getTime() > new Date(state.expireTime).getTime()
    },
    // 检查用户是否有某个角色
    hasRole: (state) => (role) => {
      return state.userRoles.includes(role)
    }
  },
  
  actions: {
    async login(userData) {
      try {
        this.loading = true
        const res = await authApi.login(userData)
        if (res.success) {
          this.token = res.data.token
          this.userInfo = res.data.userInfo || res.data
          this.expireTime = res.data.expireTime
          this.isAuthenticated = true
          this.userRoles = res.data.roles || []
          
          // 保存到localStorage
          localStorage.setItem('token', res.data.token)
          if (res.data.expireTime) {
            localStorage.setItem('expireTime', res.data.expireTime)
          }
          if (res.data.roles) {
            localStorage.setItem('userRoles', JSON.stringify(res.data.roles))
          }
          
          // 根据用户角色获取跳转路径并执行跳转
          const redirectPath = getRedirectPathByRole(this.userRoles, this.userInfo)
          router.push(redirectPath)
        }
        return res
      } catch (error) {
        console.error('登录失败:', error)
        return { success: false, msg: '登录失败' }
      } finally {
        this.loading = false
      }
    },
    
    async register(userData) {
      try {
        this.loading = true
        const res = await authApi.register(userData)
        return res
      } catch (error) {
        console.error('注册失败:', error)
        return { success: false, msg: '注册失败' }
      } finally {
        this.loading = false
      }
    },
    
    async logout() {
      try {
        const res = await authApi.logout(this.token)
        // 无论后端是否成功，都清除前端认证信息
        this.clearAuthInfo()
        return res
      } catch (error) {
        console.error('登出失败:', error)
        // 即使请求失败，也清除前端认证信息
        this.clearAuthInfo()
        return { success: false, msg: '登出失败' }
      }
    },
    
    // 清除认证信息
    clearAuthInfo() {
      this.token = ''
      this.userInfo = null
      this.expireTime = null
      this.isAuthenticated = false
      this.userRoles = []
      
      // 清除localStorage
      localStorage.removeItem('token')
      localStorage.removeItem('expireTime')
      localStorage.removeItem('userRoles')
      
      // 跳转到登录页
      router.push('/login')
    },
    
    // 刷新token
    async refreshToken() {
      try {
        const res = await authApi.refreshToken(this.token)
        if (res.success) {
          this.token = res.data.token
          this.expireTime = res.data.expireTime
          
          // 更新localStorage
          localStorage.setItem('token', res.data.token)
          if (res.data.expireTime) {
            localStorage.setItem('expireTime', res.data.expireTime)
          }
        }
        return res
      } catch (error) {
        console.error('刷新token失败:', error)
        // 不清除认证信息，仅返回失败结果
        return { success: false, msg: '刷新token失败' }
      }
    },
    
    // 更新用户信息
    async updateUserInfo(userData) {
      try {
        const res = await authApi.updateUserInfo(userData)
        if (res.success) {
          this.userInfo = { ...this.userInfo, ...res.data }
        }
        return res
      } catch (error) {
        console.error('更新用户信息失败:', error)
        return { success: false, msg: '更新用户信息失败' }
      }
    }
  }
})