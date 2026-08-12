import { defineStore } from 'pinia'
import { ai as aiApi } from '@/api'

export const useAiStore = defineStore('ai', {
  state: () => ({
    chatHistory: [],
    currentQuestion: '',
    loading: false
  }),
  
  getters: {
    hasChatHistory: (state) => state.chatHistory.length > 0
  },
  
  actions: {
    async sendMessage(message) {
      this.loading = true
      try {
        const res = await aiApi.aiChat({ message })
        const isSuccess = res.code === 200 || res.success
        if (isSuccess) {
          this.chatHistory.push({
            id: Date.now(),
            type: 'user',
            content: message
          })
          this.chatHistory.push({
            id: Date.now() + 1,
            type: 'ai',
            content: res.data?.response || res.data?.content || res.data || 'AI回复为空'
          })
        }
        return res
      } catch (error) {
        console.error('AI对话失败:', error)
        return { success: false, msg: 'AI对话失败' }
      } finally {
        this.loading = false
      }
    },
    
    clearChatHistory() {
      this.chatHistory = []
    },
    
    setCurrentQuestion(question) {
      this.currentQuestion = question
    }
  }
})