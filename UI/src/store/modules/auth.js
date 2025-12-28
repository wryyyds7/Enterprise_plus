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
        console.log('开始登录，用户数据:', userData)
        const res = await authApi.login(userData)
        console.log('登录API返回结果:', res)
        
        // 检查登录是否成功，处理不同的返回格式
        const isSuccess = res.code === 200 || res.success || (!res.code && !res.success && res.data?.token) || res.data?.userInfo || res.data
        console.log('登录是否成功:', isSuccess)
        
        // 直接从res.data中获取token和其他信息
        const token = res.data?.token || res.token
        console.log('获取到的token:', token)
        
        if (isSuccess && token) {
          // 从res中提取用户信息，兼容不同的返回格式
          const userInfo = res.data?.userInfo || res.data || res
          console.log('获取到的用户信息:', userInfo)
          
          this.token = token
          this.userInfo = userInfo
          this.expireTime = res.data?.expireTime || res.expireTime || null
          this.isAuthenticated = true
          
          // 获取用户角色
          this.userRoles = res.data?.roles || res.roles || userInfo?.roles || []
          console.log('获取到的用户角色:', this.userRoles)
          
          // 如果返回数据中没有明确的角色，根据用户类型判断
          if (this.userRoles.length === 0) {
            // 假设userInfo中包含userType字段
            if (userInfo?.userType === 'admin' || userInfo?.userType === 'ADMIN') {
              this.userRoles = ['ADMIN']
            } else if (userInfo?.userType === 'enterprise' || userInfo?.userType === 'ENTERPRISE') {
              this.userRoles = ['ENTERPRISE']
            } else {
              this.userRoles = ['USER']
            }
            console.log('根据用户类型推断的角色:', this.userRoles)
          }
          
          // 保存到localStorage
          localStorage.setItem('token', token)
          if (this.expireTime) {
            localStorage.setItem('expireTime', this.expireTime)
          }
          localStorage.setItem('userRoles', JSON.stringify(this.userRoles))
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
          
          // 根据用户角色获取跳转路径并执行跳转
          const redirectPath = getRedirectPathByRole(this.userRoles, this.userInfo)
          console.log('登录成功，跳转到:', redirectPath)
          router.push(redirectPath)
          
          // 返回统一格式的成功响应
          return { ...res, success: true, msg: res.message || res.msg || '登录成功' }
        } else {
          console.log('登录失败，返回结果:', res)
          // 返回统一格式的失败响应
          return { ...res, success: false, msg: res.message || res.msg || '登录失败' }
        }
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