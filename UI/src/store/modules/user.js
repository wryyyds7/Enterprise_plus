import { defineStore } from 'pinia'
import { user as userApi } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    userList: [],
    currentUser: null,
    loading: false,
    total: 0,
    page: 1,
    pageSize: 10
  }),

  getters: {
    getUserById: (state) => (id) => {
      return state.userList.find(user => user.userId === id)
    }
  },

  actions: {
    async getUserList(params) {
      this.loading = true
      try {
        const res = await userApi.getUserList(params)
        if (res.success) {
          this.userList = res.data.records || res.data
          this.total = res.data.total || this.userList.length
          this.page = params?.page || this.page
          this.pageSize = params?.pageSize || this.pageSize
        }
        return res
      } catch (error) {
        console.error('获取用户列表失败:', error)
        return { success: false, msg: '获取用户列表失败' }
      } finally {
        this.loading = false
      }
    },

    async searchUser(params) {
      this.loading = true
      try {
        const res = await userApi.searchUser(params)
        if (res.success) {
          this.userList = res.data.records || res.data
          this.total = res.data.total || this.userList.length
        }
        return res
      } catch (error) {
        console.error('搜索用户失败:', error)
        return { success: false, msg: '搜索用户失败' }
      } finally {
        this.loading = false
      }
    },

    async getUserInfo(userId) {
      this.loading = true
      try {
        const res = await userApi.getUserInfo(userId)
        if (res.success) {
          this.currentUser = res.data
        }
        return res
      } catch (error) {
        console.error('获取用户详情失败:', error)
        return { success: false, msg: '获取用户详情失败' }
      } finally {
        this.loading = false
      }
    },

    async addUser(data) {
      this.loading = true
      try {
        const res = await userApi.addUser(data)
        if (res.success) {
          await this.getUserList({ page: this.page, pageSize: this.pageSize })
        }
        return res
      } catch (error) {
        console.error('添加用户失败:', error)
        return { success: false, msg: '添加用户失败' }
      } finally {
        this.loading = false
      }
    },

    async updateUser(data) {
      this.loading = true
      try {
        const res = await userApi.updateUser(data)
        if (res.success) {
          await this.getUserList({ page: this.page, pageSize: this.pageSize })
        }
        return res
      } catch (error) {
        console.error('更新用户失败:', error)
        return { success: false, msg: '更新用户失败' }
      } finally {
        this.loading = false
      }
    },

    async deleteUser(userId) {
      this.loading = true
      try {
        const res = await userApi.deleteUser(userId)
        if (res.success) {
          await this.getUserList({ page: this.page, pageSize: this.pageSize })
        }
        return res
      } catch (error) {
        console.error('删除用户失败:', error)
        return { success: false, msg: '删除用户失败' }
      } finally {
        this.loading = false
      }
    },

    async updateUserStatus(params) {
      this.loading = true
      try {
        const res = await userApi.updateUserStatus(params)
        if (res.success) {
          await this.getUserList({ page: this.page, pageSize: this.pageSize })
        }
        return res
      } catch (error) {
        console.error('更新用户状态失败:', error)
        return { success: false, msg: '更新用户状态失败' }
      } finally {
        this.loading = false
      }
    }
  }
})