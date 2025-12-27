import { request } from '../index'

export default {
  // 用户登录
  login: (data) => request.post('/in/login', data),
  // 用户注册
  register: (data) => request.post('/in/register', data),
  // 用户登出
  logout: () => request.post('/in/logout'),
  // 刷新token
  refreshToken: (token) => request.post('/refreshToken', { token }),
  // 更新用户信息
  updateUserInfo: (data) => request.put('/user/update', data)
}