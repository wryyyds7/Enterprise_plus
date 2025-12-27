<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/modules/auth'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

// 表单数据
const loginForm = reactive({
  userName: '',
  password: '',
  rememberMe: false
})

// 表单验证规则
const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ]
}

// 表单引用
const loginFormRef = ref()

// 登录方法
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await authStore.login(loginForm)
        if (res.success) {
          ElMessage.success('登录成功')
          // 跳转逻辑在authStore.login中已处理
        } else {
          ElMessage.error(res.msg || '登录失败')
        }
      } catch (error) {
        ElMessage.error('登录失败，请检查网络连接或服务器状态')
      }
    }
  })
}
</script>

<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <h2 class="login-title">企offer登录</h2>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-position="top"
        class="login-form"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input
            v-model="loginForm.userName"
            placeholder="请输入用户名"
            :prefix-icon="User"
          ></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
        </el-form-item>
        <el-form-item>
          <div class="register-links">
            <span>新用户？</span>
            <el-link type="primary" @click="router.push('/register/user')">用户注册</el-link>
            <span>|</span>
            <el-link type="primary" @click="router.push('/register/enterprise')">企业注册</el-link>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="authStore.loading" class="login-button">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.login-form-wrapper {
  width: 400px;
  padding: 40px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.login-form {
  width: 100%;
}

.login-link {
  float: right;
}

.login-button {
  width: 100%;
}

.register-links {
  text-align: center;
  margin-top: 10px;
}

.register-links span {
  margin: 0 5px;
  color: #606266;
}
</style>