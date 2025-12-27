import { defineStore } from 'pinia'
import { chat as chatApi } from '@/api'

export const useChatStore = defineStore('chat', {
  state: () => ({
    sessionList: [],
    currentSession: null,
    messageList: [],
    loading: false,
    page: 1,
    pageSize: 20,
    total: 0
  }),
  
  getters: {
    hasSessions: (state) => state.sessionList.length > 0,
    hasMessages: (state) => state.messageList.length > 0
  },
  
  actions: {
    // 获取会话列表
    async getSessionList(params = {}) {
      this.loading = true
      try {
        const res = await chatApi.getSessionList(params)
        if (res.success) {
          this.sessionList = res.data
        }
        return res
      } catch (error) {
        console.error('获取会话列表失败:', error)
        return { success: false, msg: '获取会话列表失败' }
      } finally {
        this.loading = false
      }
    },
    
    // 获取会话详情
    async getSessionInfo(sessionId) {
      this.loading = true
      try {
        const res = await chatApi.getSessionInfo(sessionId)
        if (res.success) {
          this.currentSession = res.data
        }
        return res
      } catch (error) {
        console.error('获取会话详情失败:', error)
        return { success: false, msg: '获取会话详情失败' }
      } finally {
        this.loading = false
      }
    },
    
    // 创建会话
    async createSession(data) {
      try {
        const res = await chatApi.createSession(data)
        if (res.success) {
          // 添加到会话列表
          this.sessionList.unshift(res.data)
        }
        return res
      } catch (error) {
        console.error('创建会话失败:', error)
        return { success: false, msg: '创建会话失败' }
      }
    },
    
    // 更新会话
    async updateSession(data) {
      try {
        const res = await chatApi.updateSession(data)
        if (res.success) {
          // 更新会话列表中的对应会话
          const index = this.sessionList.findIndex(session => session.id === data.id)
          if (index !== -1) {
            this.sessionList[index] = { ...this.sessionList[index], ...res.data }
          }
          // 如果是当前会话，也更新当前会话
          if (this.currentSession && this.currentSession.id === data.id) {
            this.currentSession = { ...this.currentSession, ...res.data }
          }
        }
        return res
      } catch (error) {
        console.error('更新会话失败:', error)
        return { success: false, msg: '更新会话失败' }
      }
    },
    
    // 删除会话
    async deleteSession(sessionIds) {
      try {
        const res = await chatApi.deleteSession(sessionIds)
        if (res.success) {
          // 从会话列表中移除
          if (Array.isArray(sessionIds)) {
            this.sessionList = this.sessionList.filter(session => !sessionIds.includes(session.id))
          } else {
            this.sessionList = this.sessionList.filter(session => session.id !== sessionIds)
          }
          // 如果删除的是当前会话，清空当前会话
          if (this.currentSession && (Array.isArray(sessionIds) ? sessionIds.includes(this.currentSession.id) : this.currentSession.id === sessionIds)) {
            this.currentSession = null
            this.messageList = []
          }
        }
        return res
      } catch (error) {
        console.error('删除会话失败:', error)
        return { success: false, msg: '删除会话失败' }
      }
    },
    
    // 获取消息列表
    async getMessageList(params = {}) {
      this.loading = true
      try {
        const res = await chatApi.getMessageList({ ...params, page: this.page, pageSize: this.pageSize })
        if (res.success) {
          this.messageList = params.page === 1 ? res.data : [...this.messageList, ...res.data]
          this.total = res.total || res.data.length
        }
        return res
      } catch (error) {
        console.error('获取消息列表失败:', error)
        return { success: false, msg: '获取消息列表失败' }
      } finally {
        this.loading = false
      }
    },
    
    // 创建消息
    async createMessage(data) {
      try {
        const res = await chatApi.createMessage(data)
        if (res.success) {
          // 添加到消息列表
          this.messageList.push(res.data)
        }
        return res
      } catch (error) {
        console.error('创建消息失败:', error)
        return { success: false, msg: '创建消息失败' }
      }
    },
    
    // 更新消息
    async updateMessage(data) {
      try {
        const res = await chatApi.updateMessage(data)
        if (res.success) {
          // 更新消息列表中的对应消息
          const index = this.messageList.findIndex(message => message.id === data.id)
          if (index !== -1) {
            this.messageList[index] = { ...this.messageList[index], ...res.data }
          }
        }
        return res
      } catch (error) {
        console.error('更新消息失败:', error)
        return { success: false, msg: '更新消息失败' }
      }
    },
    
    // 删除消息
    async deleteMessage(msgIds) {
      try {
        const res = await chatApi.deleteMessage(msgIds)
        if (res.success) {
          // 从消息列表中移除
          if (Array.isArray(msgIds)) {
            this.messageList = this.messageList.filter(message => !msgIds.includes(message.id))
          } else {
            this.messageList = this.messageList.filter(message => message.id !== msgIds)
          }
        }
        return res
      } catch (error) {
        console.error('删除消息失败:', error)
        return { success: false, msg: '删除消息失败' }
      }
    },
    
    // 设置当前会话
    setCurrentSession(session) {
      this.currentSession = session
      this.messageList = []
      this.page = 1
    },
    
    // 设置分页
    setPage(page) {
      this.page = page
    },
    
    setPageSize(pageSize) {
      this.pageSize = pageSize
    },
    
    // 清除消息列表
    clearMessages() {
      this.messageList = []
      this.page = 1
    }
  }
})