import { request } from '../index'

export default {
  // 获取活动列表
  getActivityList: (params) => request.get('/api/activity', { params }),
  // 获取活动详情
  getActivityInfo: (activityId) => request.get(`/api/activity/${activityId}`),
  // 创建活动
  createActivity: (data) => request.post('/api/activity', data),
  // 更新活动
  updateActivity: (data) => request.put('/api/activity', data),
  // 删除活动
  deleteActivity: (activityId) => request.delete(`/api/activity/${activityId}`),
  // 批量删除活动
  batchDeleteActivity: (data) => request.delete('/api/activity/batch', data),
  // 报名活动
  registerActivity: (data) => request.post('/api/activity/register', data),
  // 取消报名
  cancelRegistration: (activityId, userId) => request.delete(`/api/activity/register/${activityId}/${userId}`),
  // 收藏活动
  favoriteActivity: (data) => request.post('/api/activity/favorite', data),
  // 取消收藏
  cancelFavorite: (activityId, userId) => request.delete(`/api/activity/favorite/${activityId}/${userId}`),
  // 更新活动点赞次数
  incrementLikeCount: (activityId) => request.put(`/api/activity/like/${activityId}`),
  // 查询用户是否已报名活动
  isRegistered: (activityId, userId) => request.get(`/api/activity/isRegistered/${activityId}/${userId}`),
  // 查询用户是否已收藏活动
  isFavorited: (activityId, userId) => request.get(`/api/activity/isFavorited/${activityId}/${userId}`)
}