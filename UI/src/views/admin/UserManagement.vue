<template>
  <div class="user-management-container">
    <div class="page-content">
      <div class="page-header">
        <h2>用户管理</h2>
        <div class="header-actions">
          <el-button type="primary" @click="handleAddUser">
            <el-icon><Plus /></el-icon>新增用户
          </el-button>
          <el-button type="danger" @click="handleBatchDelete" :disabled="selectedUsers.length === 0">
            <el-icon><Delete /></el-icon>批量删除
          </el-button>
        </div>
      </div>

    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="用户名/昵称">
          <el-input v-model="filterForm.userName" placeholder="请输入用户名或昵称" clearable />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="filterForm.userType" placeholder="请选择用户类型" clearable>
            <el-option label="全部" value="" />
            <el-option label="普通用户" value="STUDENT" />
            <el-option label="企业用户" value="ENTERPRISE" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户状态">
          <el-select v-model="filterForm.status" placeholder="请选择用户状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="启用" value="ACTIVE" />
            <el-option label="禁用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="filterForm.phonenumber" placeholder="请输入手机号码" clearable />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="filterForm.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 筛选结果统计 -->
    <div class="filter-result">
      <span>共 {{ total }} 条记录</span>
      <span v-if="isFilterActive" class="filter-conditions">
        <el-tag v-if="filterForm.userName" closable @close="handleClearFilter('userName')">
          用户名/昵称: {{ filterForm.userName }}
        </el-tag>
        <el-tag v-if="filterForm.userType" closable @close="handleClearFilter('userType')">
          用户类型: {{ getUserTypeLabel(filterForm.userType) }}
        </el-tag>
        <el-tag v-if="filterForm.status" closable @close="handleClearFilter('status')">
          状态: {{ filterForm.status === 'ACTIVE' ? '启用' : '禁用' }}
        </el-tag>
        <el-tag v-if="filterForm.phonenumber" closable @close="handleClearFilter('phonenumber')">
          手机号码: {{ filterForm.phonenumber }}
        </el-tag>
        <el-tag v-if="filterForm.email" closable @close="handleClearFilter('email')">
          邮箱: {{ filterForm.email }}
        </el-tag>
        <el-button type="text" size="small" @click="resetFilter">清除所有筛选</el-button>
      </span>
    </div>

    <!-- 用户列表 -->
    <el-card class="user-list-card">
      <el-table
        v-loading="loading"
        :data="users"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="userId" label="用户ID" width="100" sortable />
        <el-table-column prop="userName" label="用户名" min-width="120" sortable />
        <el-table-column prop="nickName" label="昵称" min-width="120" sortable />
        <el-table-column prop="userType" label="用户类型" width="120">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.userType)">
              {{ getUserTypeLabel(scope.row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phonenumber" label="手机号码" width="150" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="status" label="用户状态" width="120">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="ACTIVE"
              inactive-value="INACTIVE"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" sortable />
        <el-table-column prop="updateTime" label="更新时间" width="180" sortable />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="viewUserDetail(scope.row.userId)">
              <el-icon><View /></el-icon>详情
            </el-button>
            <el-button size="small" @click="editUser(scope.row.userId)">
              <el-icon><EditPen /></el-icon>编辑
            </el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="table-footer">
        <div class="batch-operations">
          <span v-if="selectedUsers.length > 0" class="selected-count">
            已选择 {{ selectedUsers.length }} 项
          </span>
        </div>
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, View, EditPen } from '@element-plus/icons-vue'
import router from '@/router'
import userApi from '@/api/modules/user'

// 筛选表单
const filterForm = ref({
  userName: '',
  userType: '',
  status: '',
  phonenumber: '',
  email: ''
})

// 是否有筛选条件
const isFilterActive = computed(() => {
  return Object.values(filterForm.value).some(value => value !== '')
})

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 用户数据
const users = ref([])

// 选中的用户
const selectedUsers = ref([])

// 获取用户列表
const getUsers = async () => {
  try {
    loading.value = true
    const params = {
      ...filterForm.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    const response = await userApi.getUserList(params)
    users.value = response.data.records || response.data || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取用户列表失败: ' + (error.message || '未知错误'))
    users.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  getUsers()
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
  getUsers()
}

// 重置筛选
const resetFilter = () => {
  filterForm.value = {
    userName: '',
    userType: '',
    status: '',
    phonenumber: '',
    email: ''
  }
  currentPage.value = 1
  getUsers()
}

// 清除单个筛选条件
const handleClearFilter = (field) => {
  filterForm.value[field] = ''
  currentPage.value = 1
  getUsers()
}

// 处理用户状态变更
const handleStatusChange = async (user) => {
  const newStatus = user.status === 'ACTIVE' ? '启用' : '禁用'
  try {
    loading.value = true
    await userApi.updateUserStatus({
      userId: user.userId,
      status: user.status
    })
    ElMessage.success(`用户${newStatus}成功`)
    getUsers()
  } catch (error) {
    // 恢复原来的状态
    user.status = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
    ElMessage.error(`用户${newStatus}失败: ` + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 查看用户详情
const viewUserDetail = (userId) => {
  router.push(`/admin/user/${userId}`)
}

// 编辑用户信息
const editUser = (userId) => {
  router.push(`/admin/user/${userId}/edit`)
}

// 新增用户
const handleAddUser = () => {
  router.push('/admin/user/add')
}

// 删除用户
const deleteUser = (user) => {
  ElMessageBox.confirm('确定要删除该用户吗？此操作不可恢复！', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true
      await userApi.deleteUser([user.userId])
      ElMessage.success('用户删除成功')
      getUsers()
    } catch (error) {
      ElMessage.error('删除用户失败: ' + (error.message || '未知错误'))
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消删除操作')
  })
}

// 批量删除用户
const handleBatchDelete = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要删除的用户')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的${selectedUsers.value.length}个用户吗？此操作不可恢复！`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true
      const userIds = selectedUsers.value.map(user => user.userId)
      await userApi.deleteUser(userIds)
      ElMessage.success(`成功删除${selectedUsers.value.length}个用户`)
      selectedUsers.value = []
      getUsers()
    } catch (error) {
      ElMessage.error('批量删除用户失败: ' + (error.message || '未知错误'))
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消批量删除操作')
  })
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getUsers()
}

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  getUsers()
}
</script>

<style scoped>
.user-management-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-content {
  padding: 20px;
  flex: 1;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.header-actions .el-button {
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
}

.filter-card .el-form {
  padding: 10px;
}

.filter-card .el-form-item {
  margin-right: 20px;
  margin-bottom: 15px;
}

/* 筛选结果统计 */
.filter-result {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 12px 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #666;
}

.filter-conditions {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-conditions .el-tag {
  margin: 0;
}

.filter-conditions .el-button {
  padding: 4px 12px;
  font-size: 12px;
  color: #409eff;
}

/* 用户列表卡片 */
.user-list-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
}

.user-list-card .el-table {
  margin-bottom: 20px;
}

.user-list-card .el-table__header-wrapper th {
  background-color: #f5f7fa;
  font-weight: bold;
  color: #333;
}

.user-list-card .el-table__body-wrapper tr:hover > td {
  background-color: #f5f7fa;
}

/* 表格底部 */
.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}

.batch-operations {
  display: flex;
  align-items: center;
  gap: 15px;
}

.selected-count {
  color: #666;
  font-size: 14px;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .filter-card .el-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-card .el-form-item {
    margin-right: 0;
    width: 100%;
  }
  
  .filter-card .el-form-item .el-input,
  .filter-card .el-form-item .el-select {
    width: 100%;
  }
  
  .filter-result {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .filter-conditions {
    width: 100%;
    flex-wrap: wrap;
  }
  
  .table-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .batch-operations {
    width: 100%;
    justify-content: space-between;
  }
  
  .pagination-container {
    width: 100%;
    justify-content: center;
  }
}
</style>