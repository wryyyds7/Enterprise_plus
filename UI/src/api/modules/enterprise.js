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
  // 导出企业
  exportEnterprise: (params) => request.post('/system/enterprise/export', params),
  // 获取企业标签
  getEnterpriseTags: (enterpriseId) => request.get(`/system/enterprise/id/${enterpriseId}/tags`),
  // 添加企业标签
  addEnterpriseTags: (enterpriseId, tags) => request.post(`/system/enterprise/id/${enterpriseId}/tags`, tags),
  // 删除企业标签
  removeEnterpriseTags: (enterpriseId) => request.delete(`/system/enterprise/id/${enterpriseId}/tags`),
  // 更新企业名录
  updateEnterpriseDirectory: () => request.post('/system/enterprise/updateDirectory'),
  // 前端用户搜索企业
  userList: (data) => request.post('/system/enterprise/user/list', data),
  // 获取推荐企业
  recommendEnterprises: (data) => request.post('/system/enterprise/recommend/list', data),
  
  // 获取职位列表
  getPositionList: (params) => request.get('/system/position/list', { params }),
  // 获取职位详情
  getPositionInfo: (positionId) => request.get(`/system/position/${positionId}`),
  // 创建职位
  createPosition: (data) => request.post('/system/position', data),
  // 更新职位
  updatePosition: (data) => request.put('/system/position', data),
  // 删除职位
  deletePosition: (positionIds) => request.delete(`/system/position/${positionIds}`),
  // 导出职位
  exportPosition: (params) => request.post('/system/position/export', params),
  // 获取职位标签
  getPositionTags: (positionId) => request.get(`/system/position/${positionId}/tags`),
  // 添加职位标签
  addPositionTags: (positionId, tags) => request.post(`/system/position/${positionId}/tags`, tags),
  // 删除职位标签
  removePositionTags: (positionId) => request.delete(`/system/position/${positionId}/tags`)
}