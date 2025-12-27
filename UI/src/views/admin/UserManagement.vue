<template>
  <div class="user-management-container">
    <!-- 导航头 -->
    <NavigationHeader />
    
    <div class="page-content">
      <div class="page-header">
        <h2>用户管理</h2>
      </div>

    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="filterForm.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="filterForm.userType" placeholder="请选择用户类型">
            <el-option label="全部" value="" />
            <el-option label="普通用户" value="STUDENT" />
            <el-option label="企业用户" value="ENTERPRISE" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户状态">
          <el-select v-model="filterForm.status" placeholder="请选择用户状态">
            <el-option label="全部" value="" />
            <el-option label="启用" value="ACTIVE" />
            <el-option label="禁用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="user-list-card">
      <el-table :data="paginatedUsers" stripe style="width: 100%">
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="userName" label="用户名" min-width="120" />
        <el-table-column prop="nickName" label="昵称" min-width="120" />
        <el-table-column prop="userType" label="用户类型" width="120">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.userType)">
              {{ getUserTypeLabel(scope.row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phonenumber" label="手机号码" width="150" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="status" label="用户状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="ACTIVE"
              inactive-value="INACTIVE"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="viewUserDetail(scope.row.userId)">
              查看详情
            </el-button>
            <el-button size="small" @click="editUser(scope.row.userId)">
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredUsers.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from '@/router'
import NavigationHeader from '@/components/NavigationHeader.vue'

// 筛选表单
const filterForm = ref({
  userName: '',
  userType: '',
  status: ''
})

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)

// 模拟用户数据
const users = ref([
  {
    userId: 1,
    userName: 'wry12345',
    nickName: '王小明',
    userType: 'STUDENT',
    phonenumber: '13800138001',
    email: 'wry12345@example.com',
    status: 'ACTIVE',
    createTime: '2025-12-01 10:30:00',
    updateTime: '2025-12-01 10:30:00'
  },
  {
    userId: 2,
    userName: 'alibabagroup',
    nickName: '阿里巴巴',
    userType: 'ENTERPRISE',
    phonenumber: '13900139002',
    email: 'contact@alibaba.com',
    status: 'ACTIVE',
    createTime: '2025-12-02 09:15:00',
    updateTime: '2025-12-02 09:15:00'
  },
  {
    userId: 3,
    userName: 'admin001',
    nickName: '系统管理员',
    userType: 'ADMIN',
    phonenumber: '13700137003',
    email: 'admin@example.com',
    status: 'ACTIVE',
    createTime: '2025-12-03 14:20:00',
    updateTime: '2025-12-03 14:20:00'
  },
  {
    userId: 4,
    userName: 'tencenttech',
    nickName: '腾讯科技',
    userType: 'ENTERPRISE',
    phonenumber: '13600136004',
    email: 'hr@tencent.com',
    status: 'ACTIVE',
    createTime: '2025-12-04 11:05:00',
    updateTime: '2025-12-04 11:05:00'
  },
  {
    userId: 5,
    userName: 'zhangsan',
    nickName: '张三',
    userType: 'STUDENT',
    phonenumber: '13500135005',
    email: 'zhangsan@example.com',
    status: 'INACTIVE',
    createTime: '2025-12-05 16:40:00',
    updateTime: '2025-12-06 10:30:00'
  },
  {
    userId: 6,
    userName: 'lisi',
    nickName: '李四',
    userType: 'STUDENT',
    phonenumber: '13400134006',
    email: 'lisi@example.com',
    status: 'ACTIVE',
    createTime: '2025-12-06 10:15:00',
    updateTime: '2025-12-06 10:15:00'
  },
  {
    userId: 7,
    userName: 'bytedance',
    nickName: '字节跳动',
    userType: 'ENTERPRISE',
    phonenumber: '13300133007',
    email: 'campus@bytedance.com',
    status: 'ACTIVE',
    createTime: '2025-12-07 13:25:00',
    updateTime: '2025-12-07 13:25:00'
  },
  {
    userId: 8,
    userName: 'wangwu',
    nickName: '王五',
    userType: 'STUDENT',
    phonenumber: '13200132008',
    email: 'wangwu@example.com',
    status: 'ACTIVE',
    createTime: '2025-12-08 09:50:00',
    updateTime: '2025-12-08 09:50:00'
  },
  {
    userId: 9,
    userName: 'xiaomi',
    nickName: '小米科技',
    userType: 'ENTERPRISE',
    phonenumber: '13100131009',
    email: 'recruit@xiaomi.com',
    status: 'INACTIVE',
    createTime: '2025-12-09 14:10:00',
    updateTime: '2025-12-10 16:20:00'
  },
  {
    userId: 10,
    userName: 'zhaoliu',
    nickName: '赵六',
    userType: 'STUDENT',
    phonenumber: '13000130010',
    email: 'zhaoliu@example.com',
    status: 'ACTIVE',
    createTime: '2025-12-10 11:45:00',
    updateTime: '2025-12-10 11:45:00'
  },
  {
    userId: 11,
    userName: 'netease',
    nickName: '网易',
    userType: 'ENTERPRISE',
    phonenumber: '18800188001',
    email: 'campus@netease.com',
    status: 'ACTIVE',
    createTime: '2025-12-11 15:30:00',
    updateTime: '2025-12-11 15:30:00'
  },
  {
    userId: 12,
    userName: 'admin002',
    nickName: '测试管理员',
    userType: 'ADMIN',
    phonenumber: '18900189002',
    email: 'testadmin@example.com',
    status: 'INACTIVE',
    createTime: '2025-12-12 10:20:00',
    updateTime: '2025-12-12 14:40:00'
  }
])

// 筛选后的用户列表
const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchesName = !filterForm.value.userName || 
      user.userName.toLowerCase().includes(filterForm.value.userName.toLowerCase()) ||
      user.nickName.toLowerCase().includes(filterForm.value.userName.toLowerCase())
    const matchesType = !filterForm.value.userType || 
      user.userType === filterForm.value.userType
    const matchesStatus = !filterForm.value.status || 
      user.status === filterForm.value.status
    return matchesName && matchesType && matchesStatus
  })
})

// 分页后的用户列表
const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredUsers.value.slice(start, end)
})

// 获取用户类型标签类型
const getTypeTagType = (userType) => {
  switch (userType) {
    case 'STUDENT':
      return 'primary'
    case 'ENTERPRISE':
      return 'success'
    case 'ADMIN':
      return 'warning'
    default:
      return 'info'
  }
}

// 获取用户类型标签文本
const getUserTypeLabel = (userType) => {
  switch (userType) {
    case 'STUDENT':
      return '普通用户'
    case 'ENTERPRISE':
      return '企业用户'
    case 'ADMIN':
      return '管理员'
    default:
      return '未知'
  }
}

// 处理筛选
const handleFilter = () => {
  currentPage.value = 1
}

// 重置筛选
const resetFilter = () => {
  filterForm.value = {
    userName: '',
    userType: '',
    status: ''
  }
  currentPage.value = 1
}

// 处理用户状态变更
const handleStatusChange = (user) => {
  const newStatus = user.status === 'ACTIVE' ? '启用' : '禁用'
  user.updateTime = new Date().toLocaleString()
  ElMessage.success(`用户${newStatus}成功`)
}

// 查看用户详情
const viewUserDetail = (userId) => {
  router.push(`/admin/user/${userId}`)
}

// 编辑用户信息
const editUser = (userId) => {
  router.push(`/admin/user/${userId}/edit`)
}

// 删除用户
const deleteUser = (user) => {
  ElMessageBox.confirm('确定要删除该用户吗？此操作不可恢复！', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    const index = users.value.findIndex(u => u.userId === user.userId)
    if (index !== -1) {
      users.value.splice(index, 1)
      ElMessage.success('用户删除成功')
    }
  }).catch(() => {
    ElMessage.info('已取消删除操作')
  })
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current
}
</script>

<style scoped>
.user-management-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.page-content {
  padding: 20px;
  flex: 1;
}

.page-header {
  margin-bottom: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.user-list-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>