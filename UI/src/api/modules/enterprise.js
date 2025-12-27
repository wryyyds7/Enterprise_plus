import { request } from '../index'

export default {
  // 获取企业列表
  getEnterpriseList: (params) => request.post('/system/enterprise/list', params),
  // 搜索企业
  searchEnterprise: (params) => request.get('/system/enterprise/searchEnterpriseByName', { params }),
  // 获取企业详情
  getEnterpriseInfo: (enterpriseId) => request.get(`/system/enterprise/id/${enterpriseId}`),
  // 创建企业
  createEnterprise: (data) => request.post('/system/enterprise/insert', data),
  // 更新企业
  updateEnterprise: (data) => request.put('/system/enterprise/update', data),
  // 删除企业
  deleteEnterprise: (enterpriseIds) => request.delete(`/system/enterprise/id/${enterpriseIds}`),
  // 获取职位列表
  getPositionList: (params) => request.get('/position/list', { params }),
  // 获取职位详情
  getPositionInfo: (positionId) => request.get(`/position/positionId/${positionId}`),
  // 创建职位
  createPosition: (data) => request.post('/position', data),
  // 更新职位
  updatePosition: (data) => request.put('/position', data),
  // 删除职位
  deletePosition: (positionIds) => request.delete(`/position/${positionIds}`),
  // 收藏企业
  favoriteEnterprise: (enterpriseId) => request.post(`/enterprise/favorite/${enterpriseId}`),
  // 取消收藏企业
  unfavoriteEnterprise: (enterpriseId) => request.delete(`/enterprise/favorite/${enterpriseId}`)
}