import { request } from '../index'

export default {
  // 企业推荐相关API
  getEnterpriseRecommendations: (params) => request.get('/home/enterpriseRecommendation/list', { params }),
  favoriteEnterprise: (data) => request.post('/home/enterpriseRecommendation/favorite', data),
  unfavoriteEnterprise: (id) => request.delete(`/home/enterpriseRecommendation/favorite/${id}`),
  getEnterpriseDetail: (enterpriseId) => request.get(`/home/enterpriseRecommendation/detail/${enterpriseId}`),
  
  // 校园招聘活动相关API
  getCampusEvents: (params) => request.get('/home/campusRecruitmentEvent/list', { params }),
  registerEvent: (data) => request.post('/home/campusRecruitmentEvent/register', data),
  getEventDetail: (eventId) => request.get(`/home/campusRecruitmentEvent/detail/${eventId}`),
  favoriteEvent: (data) => request.post('/home/campusRecruitmentEvent/favorite', data),
  unfavoriteEvent: (id) => request.delete(`/home/campusRecruitmentEvent/favorite/${id}`),
  
  // 用户收藏相关API
  getUserFavoriteEnterprises: (params) => request.get('/user/favorites/enterprises', { params }),
  getUserFavoriteEvents: (params) => request.get('/user/favorites/events', { params }),
  
  // 搜索相关API
  searchEnterprise: (params) => request.get('/home/search/enterprise', { params }),
  searchEvent: (params) => request.get('/home/search/event', { params })
}