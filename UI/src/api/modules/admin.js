import { request } from '../index'

export default {
  // 企业管理相关API
  getEnterpriseList: (params) => request.get('/admin/enterprise/list', { params }),
  auditEnterprise: (data) => request.post('/admin/enterprise/audit', data),
  
  // 用户管理相关API
  getUserList: (params) => request.get('/admin/user/list', { params }),
  updateUserStatus: (data) => request.put('/admin/user/updateStatus', data),
  
  // 活动管理相关API
  getEventList: (params) => request.get('/admin/event/list', { params }),
  addEvent: (data) => request.post('/admin/event', data),
  updateEvent: (data) => request.put('/admin/event', data),
  deleteEvent: (eventId) => request.delete(`/admin/event/${eventId}`),
  
  // 内容管理相关API
  getAnnouncementList: (params) => request.get('/admin/content/announcement/list', { params }),
  addAnnouncement: (data) => request.post('/admin/content/announcement', data),
  updateAnnouncement: (data) => request.put('/admin/content/announcement', data),
  deleteAnnouncement: (announcementId) => request.delete(`/admin/content/announcement/${announcementId}`),
  
  // 数据统计相关API
  getLoginFlowStatistics: () => request.get('/admin/statistics/loginFlow'),
  getUserStatistics: () => request.get('/admin/statistics/users'),
  getEnterpriseStatistics: () => request.get('/admin/statistics/enterprises'),
  getPositionStatistics: () => request.get('/admin/statistics/positions'),
  getEventStatistics: () => request.get('/admin/statistics/events'),
  getFavoriteStatistics: () => request.get('/admin/statistics/favorites'),
  getRegistrationStatistics: () => request.get('/admin/statistics/registrations')
}