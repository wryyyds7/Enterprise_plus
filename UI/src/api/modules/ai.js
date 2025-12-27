import { request } from '../index'

export default {
  // AI对话
  aiChat: (data) => request.post('/aiChat', data)
}