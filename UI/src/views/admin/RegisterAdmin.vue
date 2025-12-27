<template>
  <div class="register-container">
    <div class="register-form-wrapper">
      <h2 class="register-title">新增管理员</h2>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="100px"
        class="register-form"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="registerForm.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="registerForm.nickName" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="authStore.loading" class="register-button">
            新增管理员
          </el-button>
          <el-button @click="handleCancel" class="cancel-button">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElForm, ElFormItem, ElInput, ElButton } from 'element-plus'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import userApi from '@/api/modules/user'

const router = useRouter()
const authStore = useAuthStore()

const registerFormRef = ref()

const registerForm = reactive({
  userName: '',
  nickName: '',
  email: '',
  password: '',
  confirmPassword: '',
  userType: '00' // 管理员用户类型
})

const registerRules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const userData = { ...registerForm }
        const res = await userApi.addUser(userData)
        if (res.success) {
          ElMessage.success('管理员添加成功')
          router.push('/admin/dashboard')
        } else {
          ElMessage.error(res.message || '添加失败')
        }
      } catch (error) {
        ElMessage.error('添加失败，请稍后重试')
        console.error('添加管理员失败:', error)
      }
    }
  })
}

const handleCancel = () => {
  router.push('/admin/dashboard')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px);
  background-color: #f5f7fa;
}

.register-form-wrapper {
  background-color: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.register-form {
  margin-top: 20px;
}

.register-button {
  width: 45%;
  margin-right: 10%;
}

.cancel-button {
  width: 45%;
}
</style>