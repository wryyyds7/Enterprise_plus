import { request } from '../index'

export default {
  // 获取事件列表
  getEventList: (params) => request.get('/event', { params }),
  // 获取事件详情
  getEventInfo: (eventId) => request.get(`/event/${eventId}`),
  // 创建事件
  createEvent: (data) => request.post('/event', data),
  // 更新事件
  updateEvent: (data) => request.put('/event', data),
  // 删除事件
  deleteEvent: (eventId) => request.delete(`/event/${eventId}`),
  // 批量删除事件
  batchDeleteEvent: (data) => request.delete('/event/batch', data),
  // 根据事件类型查询事件列表
  getEventListByType: (eventType) => request.get(`/event/type/${eventType}`),
  // 根据展示位置查询事件列表
  getEventListByPosition: (displayPosition) => request.get(`/event/position/${displayPosition}`),
  // 更新事件点击次数
  incrementClickCount: (eventId) => request.put(`/event/click/${eventId}`),
  // 更新事件浏览次数
  incrementViewCount: (eventId) => request.put(`/event/view/${eventId}`)
}