<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  placeholder: {
    type: String,
    default: '请输入消息...'
  },
  disabled: {
    type: Boolean,
    default: false
  },
  showSendButton: {
    type: Boolean,
    default: true
  },
  maxLength: {
    type: Number,
    default: 1000
  }
})

const emit = defineEmits(['send', 'input'])

const message = ref('')
const isSending = ref(false)

const remainingChars = computed(() => props.maxLength - message.value.length)
const isInputValid = computed(() => message.value.trim().length > 0 && !isSending.value)

const sendMessage = async () => {
  if (!isInputValid.value || props.disabled) return
  
  isSending.value = true
  try {
    emit('send', message.value.trim())
    message.value = ''
  } catch (error) {
    ElMessage.error('发送失败，请重试')
  } finally {
    isSending.value = false
  }
}

const handleInput = (e) => {
  message.value = e.target.value
  emit('input', message.value)
}

const handleKeyPress = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}
</script>

<template>
  <div class="chat-input-container">
    <el-input
      v-model="message"
      :placeholder="placeholder"
      :disabled="disabled || isSending"
      :maxlength="maxLength"
      type="textarea"
      rows="3"
      resize="none"
      @input="handleInput"
      @keydown.enter.exact="handleKeyPress"
      @keydown.enter.shift=""
      class="chat-input"
    >
      <template #append>
        <div class="input-footer">
          <span class="char-count">{{ remainingChars }}</span>
          <el-button
            v-if="showSendButton"
            type="primary"
            :disabled="!isInputValid"
            @click="sendMessage"
            :loading="isSending"
          >
            发送
          </el-button>
        </div>
      </template>
    </el-input>
  </div>
</template>

<style scoped>
.chat-input-container {
  width: 100%;
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.05);
}

.chat-input {
  border-radius: 8px;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 8px 0;
}

.char-count {
  font-size: 12px;
  color: #909399;
}
</style>