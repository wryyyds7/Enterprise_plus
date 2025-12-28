import { request } from '../index'

export default {
  // 数据统计相关API
  getLoginFlowStatistics: () => request.get('/admin/statistics/loginFlow'),
  getUserStatistics: () => request.get('/admin/statistics/users'),
  getEnterpriseStatistics: () => request.get('/admin/statistics/enterprises'),
  getPositionStatistics: () => request.get('/admin/statistics/positions'),
  getEventStatistics: () => request.get('/admin/statistics/events'),
  getFavoriteStatistics: () => request.get('/admin/statistics/favorites'),
  getRegistrationStatistics: () => request.get('/admin/statistics/registrations')
}