import { request } from '../index'

export default {
  // 单文件上传
  uploadFile: (data) => request.post('/upload', data),
  // 多文件上传
  uploadFiles: (data) => request.post('/uploads', data)
}