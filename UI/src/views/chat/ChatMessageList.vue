<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useChatStore } from '@/store/modules/chat'
import MessageItem from '@/components/MessageItem.vue'
import ChatInput from '@/components/ChatInput.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import NavigationHeader from '@/components/NavigationHeader.vue'

const route = useRoute()
const router = useRouter()
const chatStore = useChatStore()

const sessionId = computed(() => route.params.id)
const sessionInfo = ref(null)
const messages = ref([])
const loading = ref(true)
const sending = ref(false)

// 获取会话详情和消息
const fetchSessionData = async () => {
  if (!sessionId.value) {
    ElMessage.error('会话ID不能为空')
    router.push('/chat')
    return
  }
  
  loading.value = true
  try {
    // 获取会话详情
    const sessionRes = await chatStore.getSessionDetail(sessionId.value)
    if (sessionRes.success) {
      sessionInfo.value = sessionRes.data
    }
    
    // 获取消息列表
    const messagesRes = await chatStore.getSessionMessages(sessionId.value)
    if (messagesRes.success) {
      messages.value = messagesRes.data
    }
  } catch (error) {
    // 错误处理由store中的拦截器处理
  } finally {
    loading.value = false
  }
}

// 发送消息
const sendMessage = async (content) => {
  if (!sessionId.value || !content.trim()) return
  
  sending.value = true
  try {
    const userMessage = {
      id: Date.now(),
      content,
      sender: 'user',
      timestamp: Date.now(),
      status: 'sending'
    }
    messages.value.push(userMessage)
    
    const res = await chatStore.sendMessage(sessionId.value, { content })
    if (res.success) {
      // 更新消息状态
      const msgIndex = messages.value.findIndex(msg => msg.id === userMessage.id)
      if (msgIndex !== -1) {
        messages.value[msgIndex].status = 'sent'
      }
      
      // 添加对方回复（如果有）
      if (res.data && res.data.content) {
        const replyMessage = {
          id: Date.now() + 1,
          content: res.data.content,
          sender: 'other',
          timestamp: Date.now(),
          status: 'sent'
        }
        messages.value.push(replyMessage)
      }
    }
  } catch (error) {
    // 更新消息状态为失败
    const msgIndex = messages.value.findIndex(msg => msg.id === Date.now())
    if (msgIndex !== -1) {
      messages.value[msgIndex].status = 'failed'
    }
    // 错误处理由store中的拦截器处理
  } finally {
    sending.value = false
  }
}

// 返回会话列表
const goBack = () => {
  router.push('/chat')
}

// 页面加载时获取会话数据
onMounted(() => {
  fetchSessionData()
})
</script>

<template>
  <div class="chat-session-container">
    <!-- 导航头 -->
    <NavigationHeader />
    <div class="page-content">
      <div class="chat-header">
        <div class="header-left">
            <el-button type="text" @click="goBack" :icon="ArrowLeft">返回</el-button>
          <div v-if="sessionInfo" class="session-info">
            <h3>{{ sessionInfo.name }}</h3>
            <span class="session-status" :class="`status-${sessionInfo.status}`">
              {{ sessionInfo.status === 'active' ? '活跃' : '已关闭' }}
            </span>
          </div>
        </div>
      </div>
      
      <div class="chat-messages">
        <el-skeleton :rows="10" animated v-if="loading" />
        
        <div v-else class="messages-container">
          <MessageItem
            v-for="msg in messages"
            :key="msg.id"
            :message="msg"
          />
          
          <div v-if="!loading && messages.length === 0" class="empty-messages">
            <el-empty description="暂无消息" />
          </div>
        </div>
      </div>
      
      <ChatInput
        :disabled="loading || sending || !sessionInfo || sessionInfo.status !== 'active'"
        :placeholder="sessionInfo?.status !== 'active' ? '会话已关闭，无法发送消息' : '请输入消息...'"
        @send="sendMessage"
      />
    </div>
  </div>
</template>

<style scoped>
.chat-session-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 16px 20px;
  background-color: white;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.session-info {
  display: flex;
  flex-direction: column;
}

.session-info h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.session-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  margin-top: 4px;
  width: fit-content;
}

.status-active {
  background-color: #f0f9eb;
  color: #67c23a;
}

.status-closed {
  background-color: #fef0f0;
  color: #f56c6c;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.messages-container {
  max-width: 800px;
  margin: 0 auto;
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.empty-messages {
  padding: 40px 0;
}
</style>