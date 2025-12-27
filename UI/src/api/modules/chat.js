import { request } from '../index'

export default {
  // 获取会话列表
  getSessionList: (params) => request.get('/jobChatSession/list', { params }),
  // 获取会话信息
  getSessionInfo: (sessionId) => request.get(`/jobChatSession/${sessionId}`),
  // 创建会话
  createSession: (data) => request.post('/jobChatSession', data),
  // 更新会话
  updateSession: (data) => request.put('/jobChatSession', data),
  // 删除会话
  deleteSession: (sessionIds) => request.delete(`/jobChatSession/${sessionIds}`),
  // 获取消息列表
  getMessageList: (params) => request.get('/jobChatMessage/list', { params }),
  // 获取消息详情
  getMessageInfo: (msgId) => request.get(`/jobChatMessage/${msgId}`),
  // 创建消息
  createMessage: (data) => request.post('/jobChatMessage', data),
  // 更新消息
  updateMessage: (data) => request.put('/jobChatMessage', data),
  // 删除消息
  deleteMessage: (msgIds) => request.delete(`/jobChatMessage/${msgIds}`)
}