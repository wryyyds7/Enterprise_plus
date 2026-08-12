import axios from 'axios'
import auth from './modules/auth'
import ai from './modules/ai'
import chat from './modules/chat'
import enterprise from './modules/enterprise'
import user from './modules/user'
import search from './modules/search'
import common from './modules/common'
import activity from './modules/activity'
import advertisement from './modules/advertisement'
import forum from './modules/forum'
import event from './modules/event'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加token等认证信息
    let token = localStorage.getItem('token')
    if (token) {
      // 清理token中的空格和其他无效字符，确保Base64URL格式正确
      token = token.trim()
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    // 统一错误处理
    let errorMsg = '请求失败'
    
    if (error.response) {
      // HTTP状态码错误
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          errorMsg = '未授权，请重新登录'
          // 清除过期 token 并跳转登录页
          localStorage.removeItem('token')
          localStorage.removeItem('expireTime')
          localStorage.removeItem('userRoles')
          localStorage.removeItem('userInfo')
          window.location.href = '/login'
          break
        case 403:
          errorMsg = '拒绝访问'
          break
        case 404:
          errorMsg = '请求的资源不存在'
          break
        case 500:
          errorMsg = '服务器内部错误'
          break
        default:
          errorMsg = data?.msg || `请求错误 (${status})`
      }
    } else if (error.request) {
      // 网络错误
      errorMsg = '网络连接失败，请检查网络'
    } else {
      // 其他错误
      errorMsg = error.message || '请求失败'
    }
    
    // 可以在这里使用UI组件库的消息提示
    // ElMessage.error(errorMsg)
    
    return Promise.reject(error)
  }
)

export { request, auth, ai, chat, enterprise, user, search, common, activity, advertisement, forum, event }