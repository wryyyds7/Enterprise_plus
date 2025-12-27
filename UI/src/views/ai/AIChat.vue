<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { useAiStore } from '@/store/modules/ai'
import MessageItem from '@/components/MessageItem.vue'
import ChatInput from '@/components/ChatInput.vue'
import NavigationHeader from '@/components/NavigationHeader.vue'

const aiStore = useAiStore()

// 对话输入
const messageInput = ref('')
// 使用store中的对话历史和加载状态
const chatHistory = computed(() => aiStore.chatHistory)
const loading = computed(() => aiStore.loading)

// 发送消息
const sendMessage = async (message) => {
  try {
    const res = await aiStore.sendMessage(message)
    if (!res.success) {
      ElMessage.error(res.msg || '获取AI回复失败')
    }
  } catch (error) {
    ElMessage.error('AI对话失败，请检查网络连接或服务器状态')
  } finally {
    // 滚动到底部
    scrollToBottom()
  }
}

// 滚动到底部
const scrollToBottom = () => {
  setTimeout(() => {
    const chatContainer = document.querySelector('.chat-container')
    if (chatContainer) {
      chatContainer.scrollTop = chatContainer.scrollHeight
    }
  }, 100)
}
</script>

<template>
  <div class="ai-chat-container">
    <!-- 导航头 -->
    <NavigationHeader />
    <div class="page-content">
      <div class="chat-container">
        <MessageItem
          v-for="msg in chatHistory"
          :key="msg.id"
          :message="msg"
        />
        <div v-if="loading" class="loading-item">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>AI正在思考...</span>
        </div>
      </div>
      <ChatInput
        v-model="messageInput"
        :placeholder="'请输入您的问题...'"
        :disabled="loading"
        @send="sendMessage"
      />
    </div>
  </div>
</template>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-content {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20px;
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.loading-item {
  display: flex;
  align-items: center;
  color: #909399;
  margin: 10px 0;
}

.loading-item .el-icon {
  margin-right: 8px;
}
</style>