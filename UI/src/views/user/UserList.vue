<template>
  <div class="user-list-container">
    <el-card class="user-list-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="openUserForm()">
            <el-icon><Plus /></el-icon>新增用户
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="启用" value="1" />
              <el-option label="禁用" value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>搜索
            </el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 用户列表 -->
      <el-table
        v-loading="loading"
        :data="userList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="用户ID" width="100" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="真实姓名" min-width="100" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="role" label="角色" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'success'">
              {{ scope.row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="scope">
            <el-switch 
              v-model="scope.row.status" 
              active-value="1" 
              inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column prop="lastLoginTime" label="最后登录" min-width="180" />
        <el-table-column label="操作" min-width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewUser(scope.row)">
              <el-icon><View /></el-icon>查看
            </el-button>
            <el-button size="small" type="primary" @click="editUser(scope.row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="600px"
    >
      <el-form ref="userFormRef" :model="formData" label-width="100px" :rules="formRules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码" :required="!isEdit" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" :show-password="true" />
        </el-form-item>
        <el-form-item label="确认密码" :required="!isEdit" prop="confirmPassword">
          <el-input v-model="formData.confirmPassword" type="password" placeholder="请确认密码" :show-password="true" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="formData.status" active-value="1" inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Search, View, Edit, Delete } from '@element-plus/icons-vue'
import userApi from '@/api/modules/user'

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const userFormRef = ref(null)
const selectedUsers = ref([])

// 搜索表单
const searchForm = reactive({
  username: '',
  realName: '',
  status: ''
})

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 模拟用户列表数据
const mockUserList = [
  {
    id: 1,
    username: 'admin',
    realName: '系统管理员',
    email: 'admin@example.com',
    phone: '13800138000',
    role: 'admin',
    status: '1',
    createTime: '2024-01-01 00:00:00',
    lastLoginTime: '2024-01-15 14:30:00'
  },
  {
    id: 2,
    username: 'student1',
    realName: '张三',
    email: 'zhangsan@example.com',
    phone: '13800138001',
    role: 'user',
    status: '1',
    createTime: '2024-01-02 10:30:00',
    lastLoginTime: '2024-01-14 09:15:00'
  },
  {
    id: 3,
    username: 'student2',
    realName: '李四',
    email: 'lisi@example.com',
    phone: '13800138002',
    role: 'user',
    status: '0',
    createTime: '2024-01-03 15:20:00',
    lastLoginTime: '2024-01-13 16:45:00'
  }
]

const userList = ref([...mockUserList])

// 表单数据
const formData = reactive({
  id: '',
  username: '',
  realName: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
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

// 页面加载时获取用户列表
onMounted(() => {
  fetchUserList()
})

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    // TODO: 调用真实API获取用户列表
    // const response = await userApi.getUserList({
    //   pageNum: pagination.currentPage,
    //   pageSize: pagination.pageSize,
    //   ...searchForm
    // })
    // userList.value = response.data.list
    // pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('获取用户列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchUserList()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    username: '',
    realName: '',
    status: ''
  })
  pagination.currentPage = 1
  fetchUserList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchUserList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  fetchUserList()
}

// 选择用户
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 打开用户表单
const openUserForm = (user = null) => {
  if (user) {
    isEdit.value = true
    Object.assign(formData, { ...user, confirmPassword: user.password })
  } else {
    isEdit.value = false
    Object.assign(formData, {
      id: '',
      username: '',
      realName: '',
      password: '',
      confirmPassword: '',
      email: '',
      phone: '',
      role: 'user',
      status: '1'
    })
  }
  dialogVisible.value = true
}

// 查看用户
const viewUser = (user) => {
  // TODO: 跳转到用户详情页
  ElMessage.info('查看用户功能待实现')
}

// 编辑用户
const editUser = (user) => {
  openUserForm(user)
}

// 删除用户
const deleteUser = (user) => {
  ElMessageBox.confirm(`确定要删除用户「${user.username}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用真实API删除用户
      // await userApi.deleteUser({ userId: user.id })
      userList.value = userList.value.filter(item => item.id !== user.id)
      ElMessage.success('删除用户成功')
    } catch (error) {
      ElMessage.error('删除用户失败：' + error.message)
    }
  }).catch(() => {
    // 取消删除
  })
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
          const index = userList.value.findIndex(item => item.id === formData.id)
          if (index !== -1) {
            userList.value[index] = { ...formData }
          }
          ElMessage.success('更新用户成功')
        } else {
          // TODO: 调用真实API新增用户
          // await userApi.addUser(formData)
          const newUser = { ...formData, id: Date.now() }
          userList.value.unshift(newUser)
          ElMessage.success('新增用户成功')
        }
        dialogVisible.value = false
      } catch (error) {
        ElMessage.error((isEdit.value ? '更新' : '新增') + '用户失败：' + error.message)
      }
    }
  })
}

// 更改用户状态
const handleStatusChange = async (user) => {
  try {
    // TODO: 调用真实API更新用户状态
    // await userApi.updateUserStatus({ userId: user.id, status: user.status })
    ElMessage.success('更新用户状态成功')
  } catch (error) {
    ElMessage.error('更新用户状态失败：' + error.message)
    // 恢复原来的状态
    user.status = user.status === '1' ? '0' : '1'
  }
}
</script>

<style scoped>
.user-list-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.user-list-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fafafa;
  border-radius: 4px;
}

.pagination-section {
  margin-top: 20px;
  text-align: right;
}
</style>
