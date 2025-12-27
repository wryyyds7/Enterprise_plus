<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '@/store/modules/chat'
import { ElMessage, ElForm, ElFormItem, ElInput, ElButton, ElSelect, ElOption } from 'element-plus'
import NavigationHeader from '@/components/NavigationHeader.vue'

const router = useRouter()
const chatStore = useChatStore()
const loading = ref(false)

// 表单数据
const form = ref({
  name: '',
  description: '',
  participants: []
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入会话名称', trigger: 'blur' },
    { min: 2, max: 20, message: '会话名称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  participants: [
    { required: true, message: '请选择至少一个参与者', trigger: 'change' },
    { min: 1, message: '请选择至少一个参与者', trigger: 'change' }
  ]
}

// 表单引用
const formRef = ref()

// 可用用户列表（实际应用中从API获取）
const availableUsers = ref([
  { id: '1', name: '张三', avatar: 'user1' },
  { id: '2', name: '李四', avatar: 'user2' },
  { id: '3', name: '王五', avatar: 'user3' },
  { id: '4', name: '赵六', avatar: 'user4' }
])

// 创建会话
const createSession = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    
    loading.value = true
    try {
      const sessionData = {
        name: form.value.name,
        description: form.value.description,
        participantIds: form.value.participants
      }
      
      const res = await chatStore.createSession(sessionData)
      if (res.success) {
        ElMessage.success('会话创建成功')
        router.push(`/chat/session/${res.data.id}`)
      } else {
        ElMessage.error(res.msg || '会话创建失败')
      }
    } catch (error) {
      // 错误处理由store中的拦截器处理
    } finally {
      loading.value = false
    }
  })
}

// 返回会话列表
const goBack = () => {
  router.push('/chat')
}
</script>

<template>
  <div class="create-chat-container">
    <!-- 导航头 -->
    <NavigationHeader />
    <div class="page-content">
      <div class="page-header">
        <h2>创建新聊天会话</h2>
      </div>
      
      <el-card class="create-chat-card">
        <template #header>
          <div class="card-header">
            <span>会话信息</span>
          </div>
        </template>
        
        <div class="card-content">
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            class="create-chat-form"
          >
            <el-form-item label="会话名称" prop="name">
              <el-input
                v-model="form.name"
                placeholder="请输入会话名称"
                maxlength="20"
              />
            </el-form-item>
            
            <el-form-item label="会话描述" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                rows="3"
                placeholder="请输入会话描述"
                maxlength="100"
              />
            </el-form-item>
            
            <el-form-item label="参与者" prop="participants">
              <el-select
                v-model="form.participants"
                multiple
                filterable
                collapse-tags
                placeholder="请选择参与者"
                style="width: 100%"
              >
                <el-option
                  v-for="user in availableUsers"
                  :key="user.id"
                  :label="user.name"
                  :value="user.id"
                >
                  <div class="select-option">
                    <el-avatar :size="24" :icon="'User'">
                      {{ user.name.charAt(0) }}
                    </el-avatar>
                    <span>{{ user.name }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item>
              <div class="form-actions">
                <el-button @click="goBack">取消</el-button>
                <el-button type="primary" @click="createSession" :loading="loading">创建会话</el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.create-chat-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
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

.create-chat-card {
  max-width: 600px;
  margin: 0 auto;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  padding: 20px;
}

.create-chat-form {
  margin-top: 20px;
}

.select-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 30px;
}
</style>