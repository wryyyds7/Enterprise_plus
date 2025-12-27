import { defineStore } from 'pinia'
import { search as searchApi } from '@/api'

export const useSearchStore = defineStore('search', {
  state: () => ({
    searchResults: [],
    searchHistory: [],
    loading: false,
    total: 0,
    page: 1,
    pageSize: 10
  }),

  getters: {
    getRecentSearches: (state) => {
      return state.searchHistory.slice(0, 10)
    }
  },

  actions: {
    async searchEnterprise(params) {
      this.loading = true
      try {
        const res = await searchApi.searchEnterprise(params)
        if (res.success) {
          this.searchResults = res.data.records || res.data
          this.total = res.data.total || this.searchResults.length
          this.page = params?.page || this.page
          this.pageSize = params?.pageSize || this.pageSize
          
          // 保存搜索历史
          if (params?.keyword && !this.searchHistory.includes(params.keyword)) {
            this.searchHistory.unshift(params.keyword)
            this.saveSearchHistory()
          }
        }
        return res
      } catch (error) {
        console.error('搜索企业失败:', error)
        return { success: false, msg: '搜索企业失败' }
      } finally {
        this.loading = false
      }
    },

    // 保存搜索历史到localStorage
    saveSearchHistory() {
      localStorage.setItem('searchHistory', JSON.stringify(this.searchHistory))
    },

    // 从localStorage加载搜索历史
    loadSearchHistory() {
      const history = localStorage.getItem('searchHistory')
      if (history) {
        this.searchHistory = JSON.parse(history)
      }
    },

    // 清除搜索历史
    clearSearchHistory() {
      this.searchHistory = []
      localStorage.removeItem('searchHistory')
    },

    // 删除单个搜索历史记录
    deleteSearchHistory(keyword) {
      this.searchHistory = this.searchHistory.filter(item => item !== keyword)
      this.saveSearchHistory()
    }
  }
})