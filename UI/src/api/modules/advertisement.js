import { request } from '../index'

export default {
  // 获取广告列表
  getAdvertisementList: (params) => request.get('/api/advertisement', { params }),
  // 获取广告详情
  getAdvertisementInfo: (adId) => request.get(`/api/advertisement/${adId}`),
  // 创建广告
  createAdvertisement: (data) => request.post('/api/advertisement', data),
  // 更新广告
  updateAdvertisement: (data) => request.put('/api/advertisement', data),
  // 删除广告
  deleteAdvertisement: (adId) => request.delete(`/api/advertisement/${adId}`),
  // 批量删除广告
  batchDeleteAdvertisement: (data) => request.delete('/api/advertisement/batch', data),
  // 根据展示位置查询广告列表
  getAdvertisementByPosition: (displayPosition) => request.get(`/api/advertisement/position/${displayPosition}`),
  // 更新广告点击次数
  incrementClickCount: (adId) => request.put(`/api/advertisement/click/${adId}`),
  // 更新广告浏览次数
  incrementViewCount: (adId) => request.put(`/api/advertisement/view/${adId}`)
}