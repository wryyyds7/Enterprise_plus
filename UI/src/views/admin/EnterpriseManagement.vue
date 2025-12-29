<template>
  <div class="enterprise-management-container">
    <div class="page-content">
      <div class="page-header">
        <h2>企业管理</h2>
        <div class="header-actions">
          <el-button type="primary" @click="handleAddEnterprise">
            <el-icon><Plus /></el-icon>新增企业
          </el-button>
          <el-button @click="handleExport">
            <el-icon><Download /></el-icon>导出企业
          </el-button>
          <el-button @click="handleUpdateDirectory">
            <el-icon><RefreshRight /></el-icon>更新企业名录
          </el-button>
        </div>
      </div>

      <!-- 筛选条件 -->
      <el-card class="filter-card">
        <el-form :model="filterForm" inline>
          <el-form-item label="企业名称">
            <el-input v-model="filterForm.enterpriseName" placeholder="请输入企业名称" clearable />
          </el-form-item>
          <el-form-item label="行业">
            <el-select v-model="filterForm.industry" placeholder="请选择行业" clearable>
              <el-option label="全部" value="" />
              <el-option label="互联网" value="互联网" />
              <el-option label="金融" value="金融" />
              <el-option label="教育" value="教育" />
              <el-option label="医疗" value="医疗" />
              <el-option label="制造业" value="制造业" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="企业标签">
            <el-input v-model="filterForm.tags" placeholder="请输入企业标签" clearable />
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select v-model="filterForm.auditStatus" placeholder="请选择审核状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="待审核" value="PENDING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
            </el-select>
          </el-form-item>
          <el-form-item label="企业类型">
            <el-select v-model="filterForm.enterpriseType" placeholder="请选择企业类型" clearable>
              <el-option label="全部" value="" />
              <el-option label="互联网" value="INTERNET" />
              <el-option label="金融" value="FINANCE" />
              <el-option label="教育" value="EDUCATION" />
              <el-option label="制造业" value="MANUFACTURING" />
            </el-select>
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
          <el-tag v-if="filterForm.enterpriseName" closable @close="handleClearFilter('enterpriseName')">
            企业名称: {{ filterForm.enterpriseName }}
          </el-tag>
          <el-tag v-if="filterForm.industry" closable @close="handleClearFilter('industry')">
            行业: {{ filterForm.industry }}
          </el-tag>
          <el-tag v-if="filterForm.tags" closable @close="handleClearFilter('tags')">
            标签: {{ filterForm.tags }}
          </el-tag>
          <el-tag v-if="filterForm.auditStatus" closable @close="handleClearFilter('auditStatus')">
            审核状态: {{ getStatusLabel(filterForm.auditStatus) }}
          </el-tag>
          <el-tag v-if="filterForm.enterpriseType" closable @close="handleClearFilter('enterpriseType')">
            企业类型: {{ getTypeLabel(filterForm.enterpriseType) }}
          </el-tag>
          <el-button type="text" size="small" @click="resetFilter">清除所有筛选</el-button>
        </span>
      </div>

      <!-- 企业列表 -->
      <el-card class="enterprise-list-card">
        <el-table
          v-loading="loading"
          :data="enterprises"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="enterpriseId" label="企业ID" width="100" sortable />
          <el-table-column prop="enterpriseName" label="企业名称" min-width="150" sortable />
          <el-table-column prop="industry" label="所属行业" width="120" sortable />
          <el-table-column prop="enterpriseType" label="企业类型" width="120">
            <template #default="scope">
              <el-tag :type="getTypeTagType(scope.row.enterpriseType)">
                {{ getTypeLabel(scope.row.enterpriseType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="contactPerson" label="联系人" width="100" />
          <el-table-column prop="contactPhone" label="联系电话" width="150" />
          <el-table-column prop="auditStatus" label="审核状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.auditStatus)">
                {{ getStatusLabel(scope.row.auditStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" sortable />
          <el-table-column prop="updateTime" label="更新时间" width="180" sortable />
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
              <el-button size="small" type="primary" @click="viewEnterpriseDetail(scope.row.enterpriseId)">
                <el-icon><View /></el-icon>详情
              </el-button>
              <el-button size="small" v-if="scope.row.auditStatus === 'PENDING'" @click="handleAudit(scope.row, 'APPROVED')">
                <el-icon><Check /></el-icon>通过
              </el-button>
              <el-button size="small" type="danger" v-if="scope.row.auditStatus === 'PENDING'" @click="handleAudit(scope.row, 'REJECTED')">
                <el-icon><Close /></el-icon>拒绝
              </el-button>
              <el-button size="small" @click="editEnterprise(scope.row.enterpriseId)">
                <el-icon><EditPen /></el-icon>编辑
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.enterpriseId)">
                <el-icon><Delete /></el-icon>删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 批量操作和分页 -->
        <div class="table-footer">
          <div class="batch-operations">
            <el-button size="small" type="danger" @click="handleBatchDelete" :disabled="selectedEnterprises.length === 0">
              <el-icon><Delete /></el-icon>批量删除
            </el-button>
            <span v-if="selectedEnterprises.length > 0" class="selected-count">
              已选择 {{ selectedEnterprises.length }} 项
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
import { Plus, Download, RefreshRight, View, Check, Close, EditPen, Delete } from '@element-plus/icons-vue'
import router from '@/router'
import enterpriseApi from '@/api/modules/enterprise'

// 筛选表单
const filterForm = ref({
  enterpriseName: '',
  industry: '',
  tags: '',
  auditStatus: '',
  enterpriseType: ''
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

// 企业数据
const enterprises = ref([])

// 选中的企业
const selectedEnterprises = ref([])

// 获取企业列表
const getEnterpriseList = async () => {
  try {
    loading.value = true
    const params = {
      ...filterForm.value,
      page: currentPage.value,
      size: pageSize.value
    }
    const response = await enterpriseApi.getEnterpriseList(params)
    enterprises.value = response.data.records || response.data || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取企业列表失败: ' + (error.message || '未知错误'))
    enterprises.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  getEnterpriseList()
})

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 'APPROVED':
      return 'success'
    case 'PENDING':
      return 'warning'
    case 'REJECTED':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态标签文本
const getStatusLabel = (status) => {
  switch (status) {
    case 'APPROVED':
      return '已通过'
    case 'PENDING':
      return '待审核'
    case 'REJECTED':
      return '已拒绝'
    default:
      return '未知'
  }
}

// 获取类型标签类型
const getTypeTagType = (type) => {
  switch (type) {
    case 'INTERNET':
      return 'primary'
    case 'FINANCE':
      return 'success'
    case 'EDUCATION':
      return 'warning'
    case 'MANUFACTURING':
      return 'info'
    default:
      return 'info'
  }
}

// 获取类型标签文本
const getTypeLabel = (type) => {
  switch (type) {
    case 'INTERNET':
      return '互联网'
    case 'FINANCE':
      return '金融'
    case 'EDUCATION':
      return '教育'
    case 'MANUFACTURING':
      return '制造业'
    default:
      return '未知'
  }
}

// 处理筛选
const handleFilter = () => {
  currentPage.value = 1
  getEnterpriseList()
}

// 重置筛选
const resetFilter = () => {
  filterForm.value = {
    enterpriseName: '',
    industry: '',
    tags: '',
    auditStatus: '',
    enterpriseType: ''
  }
  currentPage.value = 1
  getEnterpriseList()
}

// 清除单个筛选条件
const handleClearFilter = (field) => {
  filterForm.value[field] = ''
  currentPage.value = 1
  getEnterpriseList()
}

// 处理审核 - 注意：后端没有专门的审核接口，通过更新企业信息实现
const handleAudit = (enterprise, status) => {
  const newStatus = status === 'APPROVED' ? '已通过' : '已拒绝'
  ElMessageBox.confirm(`确定要${newStatus}该企业吗？`, '审核确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      loading.value = true
      // 调用更新企业接口，更新审核状态
      await enterpriseApi.updateEnterprise({
        enterpriseId: enterprise.enterpriseId,
        auditStatus: status
      })
      ElMessage.success(`企业${newStatus}成功`)
      // 重新获取企业列表
      getEnterpriseList()
    } catch (error) {
      ElMessage.error(`企业${newStatus}失败: ` + (error.message || '未知错误'))
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消审核操作')
  })
}

// 查看企业详情
const viewEnterpriseDetail = (enterpriseId) => {
  router.push(`/admin/enterprise/detail/${enterpriseId}`)
}

// 编辑企业信息
const editEnterprise = (enterpriseId) => {
  router.push(`/admin/enterprise/form/${enterpriseId}`)
}

// 新增企业
const handleAddEnterprise = () => {
  router.push(`/admin/enterprise/form`)
}

// 导出企业
const handleExport = () => {
  try {
    enterpriseApi.exportEnterprise(filterForm.value)
    ElMessage.success('导出企业成功')
  } catch (error) {
    ElMessage.error('导出企业失败: ' + (error.message || '未知错误'))
  }
}

// 更新企业名录
const handleUpdateDirectory = () => {
  ElMessageBox.confirm('确定要更新企业名录吗？', '更新确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      loading.value = true
      await enterpriseApi.updateEnterpriseDirectory()
      ElMessage.success('更新企业名录成功')
      getEnterpriseList()
    } catch (error) {
      ElMessage.error('更新企业名录失败: ' + (error.message || '未知错误'))
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消更新操作')
  })
}

// 删除企业
const handleDelete = (enterpriseId) => {
  ElMessageBox.confirm('确定要删除该企业吗？此操作不可恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true
      await enterpriseApi.deleteEnterprise([enterpriseId])
      ElMessage.success('删除企业成功')
      getEnterpriseList()
    } catch (error) {
      ElMessage.error('删除企业失败: ' + (error.message || '未知错误'))
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消删除操作')
  })
}

// 批量删除企业
const handleBatchDelete = () => {
  if (selectedEnterprises.value.length === 0) {
    ElMessage.warning('请先选择要删除的企业')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的${selectedEnterprises.value.length}家企业吗？此操作不可恢复。`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true
      const enterpriseIds = selectedEnterprises.value.map(enterprise => enterprise.enterpriseId)
      await enterpriseApi.deleteEnterprise(enterpriseIds)
      ElMessage.success(`成功删除${selectedEnterprises.value.length}家企业`)
      selectedEnterprises.value = []
      getEnterpriseList()
    } catch (error) {
      ElMessage.error('批量删除企业失败: ' + (error.message || '未知错误'))
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消批量删除操作')
  })
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedEnterprises.value = selection
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getEnterpriseList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  getEnterpriseList()
}
</script>

<style scoped>
.enterprise-management-container {
  padding: 0;
  margin: 0;
  min-height: 100vh;
}

.page-content {
  padding: 20px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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

/* 筛选卡片 */
.filter-card {
  margin-bottom: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
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

/* 企业列表卡片 */
.enterprise-list-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
}

/* 表格 */
.enterprise-list-card .el-table {
  margin-bottom: 20px;
}

.enterprise-list-card .el-table__header-wrapper th {
  background-color: #f5f7fa;
  font-weight: bold;
  color: #333;
}

.enterprise-list-card .el-table__body-wrapper tr:hover > td {
  background-color: #f5f7fa;
}

/* 表格底部 */
.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}

/* 批量操作 */
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
}

/* 按钮样式 */
.header-actions .el-button {
  display: flex;
  align-items: center;
  gap: 5px;
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