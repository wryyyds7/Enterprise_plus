import { request } from '../index'

export default {
  // 获取用户列表（管理员）
  getUserList: (params) => request.get('/users/admin/searchUserByPage', { params }),
  // 搜索用户
  searchUser: (params) => request.get('/users/user/searchUser', { params }),
  // 获取用户信息
  getUserInfo: (userId) => request.get('/users/user/searchUser', { params: { userId } }),
  // 删除用户（管理员）
  deleteUser: (userId) => request.delete(`/users/admin/deleteUser/${userId}`),
  // 更新用户信息
  updateUser: (data) => request.put('/users/user/updateUser', data),
  // 更新用户状态（管理员）
  updateUserStatus: (data) => request.put('/users/admin/updateUserStatus', data),
  // 添加用户（管理员）
  addUser: (data) => request.post('/users/admin/add', data),
  // 获取用户求职偏好
  getJobPreference: (userId) => request.get('/users/user/jobPreference', { params: { userId } }),
  // 更新用户求职偏好
  updateJobPreference: (data) => request.put('/users/user/jobPreference', data)
}