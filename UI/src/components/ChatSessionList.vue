<script setup>
import { ref, computed } from 'vue'
import { useChatStore } from '../store/modules/chat'
import { useRouter } from 'vue-router'

const props = defineProps({
  sessions: {
    type: Array,
    default: () => []
  },
  selectedSessionId: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['select', 'delete', 'clear'])

const chatStore = useChatStore()
const router = useRouter()
const showDeleteConfirm = ref(false)
const sessionToDelete = ref(null)

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const now = new Date()
  const diffInHours = (now - date) / (1000 * 60 * 60)
  
  if (diffInHours < 1) {
    return `${Math.floor(diffInHours * 60)}分钟前`
  } else if (diffInHours < 24) {
    return `${Math.floor(diffInHours)}小时前`
  } else {
    return date.toLocaleDateString('zh-CN', {
      month: '2-digit',
      day: '2-digit'
    })
  }
}

// 选择会话
const selectSession = (session) => {
  emit('select', session)
}

// 确认删除会话
const confirmDelete = (session) => {
  sessionToDelete.value = session
  showDeleteConfirm.value = true
}

// 删除会话
const deleteSession = async () => {
  if (!sessionToDelete.value) return
  
  try {
    await chatStore.deleteSession(sessionToDelete.value.id)
    emit('delete', sessionToDelete.value.id)
    showDeleteConfirm.value = false
    sessionToDelete.value = null
  } catch (error) {
    // 错误处理由store中的拦截器处理
  }
}

// 清除会话消息
const clearSession = async (session) => {
  try {
    await chatStore.clearSessionMessages(session.id)
    emit('clear', session.id)
  } catch (error) {
    // 错误处理由store中的拦截器处理
  }
}
</script>

<template>
  <div class="chat-session-list">
    <!-- 会话列表标题 -->
    <div class="session-header">
      <h3>会话列表</h3>
      <el-button type="text" size="small" @click="$emit('create')">
        <el-icon><Plus /></el-icon> 新建会话
      </el-button>
    </div>
    
    <!-- 会话列表 -->
    <div class="session-container">
      <div
        v-for="session in sessions"
        :key="session.id"
        :class="[
          'session-item',
          { 'session-item--active': session.id === selectedSessionId }
        ]"
        @click="selectSession(session)"
      >
        <!-- 会话信息 -->
        <div class="session-info">
          <div class="session-title">
            <span>{{ session.name || '未命名会话' }}</span>
            <span v-if="session.unreadCount > 0" class="unread-count">
              {{ session.unreadCount }}
            </span>
          </div>
          <div class="session-last-message">
            <span class="last-message">{{ session.lastMessage || '暂无消息' }}</span>
            <span class="session-time">{{ formatTime(session.lastUpdateTime) }}</span>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="session-actions">
          <el-dropdown trigger="click" @command="(cmd) => {
            if (cmd === 'delete') confirmDelete(session)
            if (cmd === 'clear') clearSession(session)
          }">
            <el-button type="text" size="small" :icon="'More'" />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="clear">清除消息</el-dropdown-item>
                <el-dropdown-item command="delete" divided danger>删除会话</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="sessions.length === 0" class="empty-sessions">
        <el-empty description="暂无会话" :image-size="100" />
        <el-button type="primary" size="small" @click="$emit('create')">
          创建第一个会话
        </el-button>
      </div>
    </div>
    
    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="showDeleteConfirm"
      title="删除会话"
      width="400px"
    >
      <div class="delete-dialog-content">
        <p>确定要删除会话 "{{ sessionToDelete?.name || '未命名会话' }}" 吗？</p>
        <p class="delete-warning">此操作将删除会话中的所有消息，且无法恢复。</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDeleteConfirm = false">取消</el-button>
          <el-button type="danger" @click="deleteSession">确定删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.chat-session-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  border-right: 1px solid #e4e7ed;
}

.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.session-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.session-container {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.session-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 4px;
}

.session-item:hover {
  background-color: #f5f7fa;
}

.session-item--active {
  background-color: #ecf5ff !important;
  border-left: 4px solid #409eff;
}

.session-info {
  flex: 1;
  min-width: 0;
}

.session-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.session-title span:first-child {
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.unread-count {
  background-color: #f56c6c;
  color: white;
  font-size: 12px;
  padding: 0 6px;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
}

.session-last-message {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.last-message {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 8px;
}

.session-time {
  white-space: nowrap;
}

.session-actions {
  opacity: 0;
  transition: opacity 0.3s;
}

.session-item:hover .session-actions {
  opacity: 1;
}

.empty-sessions {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  gap: 16px;
}

.delete-dialog-content {
  padding: 16px 0;
}

.delete-warning {
  color: #f56c6c;
  font-size: 14px;
  margin-top: 8px;
}
</style>