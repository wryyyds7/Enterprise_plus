<template>
  <div class="chat-message-detail">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>消息详情</h2>
          <el-button type="primary" @click="handleBack">返回</el-button>
        </div>
      </template>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>

      <div v-else-if="message" class="message-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="消息ID">{{ message.msgId }}</el-descriptions-item>
          <el-descriptions-item label="会话ID">{{ message.sessionId }}</el-descriptions-item>
          <el-descriptions-item label="发送者ID">{{ message.senderId }}</el-descriptions-item>
          <el-descriptions-item label="接收者ID">{{ message.receiverId }}</el-descriptions-item>
          <el-descriptions-item label="消息类型">{{ getMessageTypeLabel(message.msgType) }}</el-descriptions-item>
          <el-descriptions-item label="消息状态">{{ getMessageStatusLabel(message.msgStatus) }}</el-descriptions-item>
          <el-descriptions-item label="发送时间" :span="2">{{ formatDate(message.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="消息内容" :span="2">
            <div class="message-content">{{ message.msgContent }}</div>
          </el-descriptions-item>
        </el-descriptions>

        <div class="message-actions">
          <el-button type="primary" @click="handleReply">回复消息</el-button>
          <el-button @click="handleForward">转发消息</el-button>
          <el-button type="danger" @click="handleDelete">删除消息</el-button>
        </div>
      </div>

      <div v-else class="empty-container">
        <el-empty description="未找到消息详情" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import chatApi from '@/api/modules/chat'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const message = ref(null)

// 消息类型映射
const messageTypeMap = {
  1: '文本消息',
  2: '图片消息',
  3: '文件消息',
  4: '系统消息'
}

// 消息状态映射
const messageStatusMap = {
  1: '发送中',
  2: '已发送',
  3: '已读',
  4: '发送失败'
}

// 获取消息类型标签
const getMessageTypeLabel = (type) => {
  return messageTypeMap[type] || '未知类型'
}

// 获取消息状态标签
const getMessageStatusLabel = (status) => {
  return messageStatusMap[status] || '未知状态'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

// 获取消息详情
const getMessageDetail = async () => {
  const msgId = route.params.messageId
  if (!msgId) {
    ElMessage.error('消息ID不能为空')
    return
  }

  try {
    loading.value = true
    const response = await chatApi.getMessageInfo(msgId)
    if (response && response.data) {
      message.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取消息详情失败：' + (error.message || '未知错误'))
    console.error('获取消息详情失败：', error)
  } finally {
    loading.value = false
  }
}

// 返回上一页
const handleBack = () => {
  router.back()
}

// 回复消息
const handleReply = () => {
  if (!message.value) return
  // 跳转到会话页面并填充回复内容
  router.push({
    path: `/chat/session/${message.value.sessionId}`,
    query: { replyTo: message.value.msgId, replyContent: message.value.msgContent }
  })
}

// 转发消息
const handleForward = () => {
  if (!message.value) return
  ElMessage.success('转发功能待实现')
  // 这里可以实现转发逻辑
}

// 删除消息
const handleDelete = () => {
  if (!message.value) return
  
  ElMessageBox.confirm('确定要删除这条消息吗？', '删除消息', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await chatApi.deleteMessage([message.value.msgId])
      ElMessage.success('消息删除成功')
      router.back()
    } catch (error) {
      ElMessage.error('消息删除失败：' + (error.message || '未知错误'))
    }
  }).catch(() => {
    // 取消删除
  })
}

onMounted(() => {
  getMessageDetail()
})
</script>

<style scoped>
.chat-message-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container {
  margin: 20px 0;
}

.message-detail {
  margin-top: 20px;
}

.message-content {
  white-space: pre-wrap;
  word-break: break-word;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.message-actions {
  margin-top: 30px;
  display: flex;
  gap: 10px;
}

.empty-container {
  margin: 50px 0;
  text-align: center;
}
</style>