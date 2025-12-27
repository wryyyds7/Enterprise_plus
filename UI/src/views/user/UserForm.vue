<template>
  <div class="user-form-container">
    <el-card class="user-form-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑用户' : '新增用户' }}</span>
        </div>
      </template>

      <el-form
        ref="userFormRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        class="user-form"
      >
        <!-- 基础信息 -->
        <el-divider>基础信息</el-divider>
        
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        
        <el-form-item label="密码" :required="!isEdit" prop="password">
          <el-input 
            v-model="formData.password" 
            type="password" 
            placeholder="请输入密码" 
            :show-password="true" 
          />
        </el-form-item>
        
        <el-form-item label="确认密码" :required="!isEdit" prop="confirmPassword">
          <el-input 
            v-model="formData.confirmPassword" 
            type="password" 
            placeholder="请确认密码" 
            :show-password="true" 
          />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="生日" prop="birthday">
          <el-date-picker 
            v-model="formData.birthday" 
            type="date" 
            placeholder="选择生日" 
          />
        </el-form-item>
        
        <!-- 教育信息 -->
        <el-divider>教育信息</el-divider>
        
        <el-form-item label="学校" prop="school">
          <el-input v-model="formData.school" placeholder="请输入学校名称" />
        </el-form-item>
        
        <el-form-item label="专业" prop="major">
          <el-input v-model="formData.major" placeholder="请输入专业名称" />
        </el-form-item>
        
        <el-form-item label="学历" prop="education">
          <el-select v-model="formData.education" placeholder="请选择学历">
            <el-option label="专科" value="专科" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="毕业时间" prop="graduationTime">
          <el-date-picker 
            v-model="formData.graduationTime" 
            type="date" 
            placeholder="选择毕业时间" 
          />
        </el-form-item>
        
        <!-- 角色与状态 -->
        <el-divider>角色与状态</el-divider>
        
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-switch 
            v-model="formData.status" 
            active-value="1" 
            inactive-value="0" 
          />
        </el-form-item>
        
        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import userApi from '@/api/modules/user'

const router = useRouter()
const route = useRoute()
const userFormRef = ref(null)
const isEdit = ref(false)

// 表单数据
const formData = reactive({
  id: '',
  username: '',
  realName: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  gender: 'male',
  birthday: '',
  school: '',
  major: '',
  education: '',
  graduationTime: '',
  role: 'user',
  status: '1'
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  password: [
    { required: !isEdit, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: !isEdit, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== formData.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 页面加载时初始化数据
onMounted(() => {
  const userId = route.params.id
  if (userId) {
    isEdit.value = true
    fetchUserDetail(userId)
  }
})

// 获取用户详情
const fetchUserDetail = async (userId) => {
  try {
    // TODO: 调用真实API获取用户详情
    // const response = await userApi.searchUser({ userId })
    // Object.assign(formData, response.data, { confirmPassword: response.data.password })
    
    // 模拟数据
    const mockUser = {
      id: userId,
      username: 'student1',
      realName: '张三',
      password: '123456',
      email: 'zhangsan@example.com',
      phone: '13800138000',
      gender: 'male',
      birthday: '1999-01-01',
      school: '北京大学',
      major: '计算机科学与技术',
      education: '本科',
      graduationTime: '2024-06-30',
      role: 'user',
      status: '1'
    }
    Object.assign(formData, mockUser, { confirmPassword: mockUser.password })
  } catch (error) {
    ElMessage.error('获取用户详情失败：' + error.message)
    router.push('/user/list')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          // TODO: 调用真实API更新用户
          // await userApi.updateUser(formData)
          ElMessage.success('更新用户成功')
        } else {
          // TODO: 调用真实API新增用户
          // await userApi.addUser(formData)
          ElMessage.success('新增用户成功')
        }
        router.push('/user/list')
      } catch (error) {
        ElMessage.error((isEdit.value ? '更新' : '新增') + '用户失败：' + error.message)
      }
    }
  })
}

// 重置表单
const handleReset = () => {
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
}

// 取消操作
const handleCancel = () => {
  router.push('/user/list')
}
</script>

<style scoped>
.user-form-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.user-form-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-form {
  padding: 20px 0;
}

.user-form .el-form-item {
  margin-bottom: 20px;
}
</style>
