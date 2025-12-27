<script setup>
import { computed } from 'vue'
import { User, ChatRound, InfoFilled, Document, CircleClose, Loading } from '@element-plus/icons-vue'

const props = defineProps({
  message: {
    type: Object,
    required: true,
    default: () => ({
      id: '',
      content: '',
      sender: '',
      timestamp: Date.now(),
      status: 'sent', // sent, sending, failed
      type: 'text' // text, image, file
    })
  }
})

// 计算消息样式类
const messageClass = computed(() => {
  return {
    'message-item': true,
    'message-item--user': props.message.sender === 'user',
    'message-item--ai': props.message.sender === 'ai',
    'message-item--system': props.message.sender === 'system'
  }
})

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取消息状态图标
const getStatusIcon = () => {
  switch (props.message.status) {
    case 'sending':
      return 'Loading'
    case 'failed':
      return 'CircleClose'
    default:
      return null
  }
}
</script>

<template>
  <div :class="messageClass">
    <!-- 消息头像 -->
    <div class="message-avatar">
      <el-avatar :size="40">
        <el-icon v-if="message.sender === 'user'">
          <User />
        </el-icon>
        <el-icon v-else-if="message.sender === 'ai'">
          <ChatRound />
        </el-icon>
        <el-icon v-else>
          <InfoFilled />
        </el-icon>
      </el-avatar>
    </div>
    
    <!-- 消息内容 -->
    <div class="message-content">
      <div class="message-bubble">
        <!-- 文本消息 -->
        <div v-if="message.type === 'text'" class="message-text">
          {{ message.content }}
        </div>
        
        <!-- 图片消息 -->
        <div v-else-if="message.type === 'image'" class="message-image">
          <el-image
            :src="message.content"
            fit="cover"
            class="image-preview"
            :preview-src-list="[message.content]"
          />
        </div>
        
        <!-- 文件消息 -->
        <div v-else-if="message.type === 'file'" class="message-file">
          <el-link :href="message.content.url" target="_blank">
            <el-icon class="file-icon"><Document /></el-icon>
            <span>{{ message.content.name }}</span>
            <span class="file-size">{{ message.content.size }}</span>
          </el-link>
        </div>
      </div>
      
      <!-- 消息时间和状态 -->
      <div class="message-meta">
        <span class="message-time">{{ formatTime(message.timestamp) }}</span>
        <el-icon v-if="getStatusIcon()" :class="`message-status message-status--${message.status}`">
          <component :is="getStatusIcon()" />
        </el-icon>
      </div>
    </div>
  </div>
</template>

<style scoped>
.message-item {
  display: flex;
  margin-bottom: 16px;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item--user {
  flex-direction: row-reverse;
}

.message-avatar {
  margin: 0 8px;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.message-item--user .message-content {
  align-items: flex-end;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  background-color: #f0f0f0;
  position: relative;
}

.message-item--user .message-bubble {
  background-color: #409eff;
  color: white;
}

.message-item--ai .message-bubble {
  background-color: #ecf5ff;
  border: 1px solid #d9ecff;
}

.message-item--system .message-bubble {
  background-color: #f5f5f5;
  color: #909399;
  font-size: 12px;
}

.message-text {
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-image {
  max-width: 200px;
}

.image-preview {
  border-radius: 8px;
  cursor: pointer;
}

.message-file {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-icon {
  color: #409eff;
}

.file-size {
  font-size: 12px;
  color: #909399;
}

.message-item--user .file-size {
  color: rgba(255, 255, 255, 0.8);
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.message-item--user .message-meta {
  color: rgba(255, 255, 255, 0.7);
}

.message-status {
  font-size: 14px;
}

.message-status--sending {
  animation: spin 1s linear infinite;
  color: #409eff;
}

.message-status--failed {
  color: #f56c6c;
  cursor: pointer;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>