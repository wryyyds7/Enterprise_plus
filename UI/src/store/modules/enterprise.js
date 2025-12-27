import { defineStore } from 'pinia'
import { enterprise as enterpriseApi } from '@/api'

export const useEnterpriseStore = defineStore('enterprise', {
  state: () => ({
    enterpriseList: [],
    positionList: [],
    currentEnterprise: null,
    currentPosition: null,
    loading: false,
    total: 0,
    page: 1,
    pageSize: 10
  }),

  getters: {
    getEnterpriseById: (state) => (id) => {
      return state.enterpriseList.find(enterprise => enterprise.enterpriseId === id)
    },
    getPositionById: (state) => (id) => {
      return state.positionList.find(position => position.positionId === id)
    }
  },

  actions: {
    async getEnterpriseList(params) {
      this.loading = true
      try {
        const res = await enterpriseApi.getEnterpriseList(params)
        if (res.success) {
          this.enterpriseList = res.data.records || res.data
          this.total = res.data.total || this.enterpriseList.length
          this.page = params?.page || this.page
          this.pageSize = params?.pageSize || this.pageSize
        }
        return res
      } catch (error) {
        console.error('获取企业列表失败:', error)
        return { success: false, msg: '获取企业列表失败' }
      } finally {
        this.loading = false
      }
    },

    async searchEnterprise(params) {
      this.loading = true
      try {
        const res = await enterpriseApi.searchEnterprise(params)
        if (res.success) {
          this.enterpriseList = res.data.records || res.data
          this.total = res.data.total || this.enterpriseList.length
        }
        return res
      } catch (error) {
        console.error('搜索企业失败:', error)
        return { success: false, msg: '搜索企业失败' }
      } finally {
        this.loading = false
      }
    },

    async getEnterpriseInfo(enterpriseId) {
      this.loading = true
      try {
        const res = await enterpriseApi.getEnterpriseInfo(enterpriseId)
        if (res.success) {
          this.currentEnterprise = res.data
        }
        return res
      } catch (error) {
        console.error('获取企业详情失败:', error)
        return { success: false, msg: '获取企业详情失败' }
      } finally {
        this.loading = false
      }
    },

    async createEnterprise(data) {
      this.loading = true
      try {
        const res = await enterpriseApi.createEnterprise(data)
        if (res.success) {
          await this.getEnterpriseList({ page: this.page, pageSize: this.pageSize })
        }
        return res
      } catch (error) {
        console.error('创建企业失败:', error)
        return { success: false, msg: '创建企业失败' }
      } finally {
        this.loading = false
      }
    },

    async updateEnterprise(data) {
      this.loading = true
      try {
        const res = await enterpriseApi.updateEnterprise(data)
        if (res.success) {
          await this.getEnterpriseList({ page: this.page, pageSize: this.pageSize })
        }
        return res
      } catch (error) {
        console.error('更新企业失败:', error)
        return { success: false, msg: '更新企业失败' }
      } finally {
        this.loading = false
      }
    },

    async deleteEnterprise(enterpriseIds) {
      this.loading = true
      try {
        const res = await enterpriseApi.deleteEnterprise(enterpriseIds)
        if (res.success) {
          await this.getEnterpriseList({ page: this.page, pageSize: this.pageSize })
        }
        return res
      } catch (error) {
        console.error('删除企业失败:', error)
        return { success: false, msg: '删除企业失败' }
      } finally {
        this.loading = false
      }
    },

    async getPositionList(params) {
      this.loading = true
      try {
        const res = await enterpriseApi.getPositionList(params)
        if (res.success) {
          this.positionList = res.data.records || res.data
          this.total = res.data.total || this.positionList.length
        }
        return res
      } catch (error) {
        console.error('获取职位列表失败:', error)
        return { success: false, msg: '获取职位列表失败' }
      } finally {
        this.loading = false
      }
    },

    async getPositionInfo(positionId) {
      this.loading = true
      try {
        const res = await enterpriseApi.getPositionInfo(positionId)
        if (res.success) {
          this.currentPosition = res.data
        }
        return res
      } catch (error) {
        console.error('获取职位详情失败:', error)
        return { success: false, msg: '获取职位详情失败' }
      } finally {
        this.loading = false
      }
    },

    async createPosition(data) {
      this.loading = true
      try {
        const res = await enterpriseApi.createPosition(data)
        if (res.success) {
          await this.getPositionList({ page: this.page, pageSize: this.pageSize })
        }
        return res
      } catch (error) {
        console.error('创建职位失败:', error)
        return { success: false, msg: '创建职位失败' }
      } finally {
        this.loading = false
      }
    },

    async updatePosition(data) {
      this.loading = true
      try {
        const res = await enterpriseApi.updatePosition(data)
        if (res.success) {
          await this.getPositionList({ page: this.page, pageSize: this.pageSize })
        }
        return res
      } catch (error) {
        console.error('更新职位失败:', error)
        return { success: false, msg: '更新职位失败' }
      } finally {
        this.loading = false
      }
    },

    async deletePosition(positionIds) {
      this.loading = true
      try {
        const res = await enterpriseApi.deletePosition(positionIds)
        if (res.success) {
          await this.getPositionList({ page: this.page, pageSize: this.pageSize })
        }
        return res
      } catch (error) {
        console.error('删除职位失败:', error)
        return { success: false, msg: '删除职位失败' }
      } finally {
        this.loading = false
      }
    }
  }
})