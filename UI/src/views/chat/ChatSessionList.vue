<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '@/store/modules/chat'
import ChatSessionList from '@/components/ChatSessionList.vue'
import NavigationHeader from '@/components/NavigationHeader.vue'

const router = useRouter()
const chatStore = useChatStore()
const sessions = ref([])
const loading = ref(true)

// 获取聊天会话列表
const fetchSessions = async () => {
  loading.value = true
  try {
    const res = await chatStore.getSessionList()
    if (res.success) {
      sessions.value = res.data
    }
  } catch (error) {
    // 错误处理由store中的拦截器处理
  } finally {
    loading.value = false
  }
}

// 选择会话
const selectSession = (session) => {
  router.push(`/chat/session/${session.id}`)
}

// 创建新会话
const createSession = () => {
  router.push('/chat/create')
}

// 删除会话
const deleteSession = (sessionId) => {
  // 更新本地会话列表
  sessions.value = sessions.value.filter(session => session.id !== sessionId)
}

// 清除会话消息
const clearSession = (sessionId) => {
  // 更新本地会话列表
  const session = sessions.value.find(session => session.id === sessionId)
  if (session) {
    session.lastMessage = ''
    session.unreadCount = 0
  }
}

// 页面加载时获取会话列表
onMounted(() => {
  fetchSessions()
})
</script>

<template>
  <div class="chat-list-container">
    <!-- 导航头 -->
    <NavigationHeader />
    <div class="page-content">
      <div class="page-header">
        <h2>聊天管理</h2>
      </div>
      
      <el-card class="chat-list-card">
        <template #header>
          <div class="card-header">
            <span>聊天会话</span>
          </div>
        </template>
        
        <div class="card-content">
          <el-skeleton :rows="5" animated v-if="loading" />
          
          <ChatSessionList
            v-else
            :sessions="sessions"
            @select="selectSession"
            @create="createSession"
            @delete="deleteSession"
            @clear="clearSession"
          />
          
          <el-empty v-if="!loading && sessions.length === 0" description="暂无聊天会话">
            <el-button type="primary" @click="createSession">创建第一个会话</el-button>
          </el-empty>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.chat-list-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.page-content {
  flex: 1;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.chat-list-card {
  height: calc(100% - 70px);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  height: calc(100% - 44px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
</style>