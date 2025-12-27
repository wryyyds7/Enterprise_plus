import { request } from '../index'

export default {
  // 企业搜索 - 使用enterprise模块的搜索方法，支持自动新增企业
  searchEnterprise: (params) => request.get('/system/enterprise/searchEnterpriseByName', { params: { enterpriseName: params.keyword } }),
  // 活动搜索
  searchEvent: (params) => request.get('/home/search/event', { params })
}