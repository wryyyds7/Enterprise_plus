<template>
  <div class="position-management-container">
    <div class="page-content">
      <div class="page-header">
        <h2>职位管理</h2>
        <div class="header-actions">
          <el-button type="primary" @click="handleAddPosition">
            <el-icon><Plus /></el-icon>新增职位
          </el-button>
          <el-button @click="handleBatchDelete" :disabled="selectedPositions.length === 0">
            <el-icon><Delete /></el-icon>批量删除
          </el-button>
        </div>
      </div>

      <!-- 筛选条件 -->
      <el-card class="filter-card">
        <el-form :model="filterForm" inline>
          <el-form-item label="职位名称">
            <el-input v-model="filterForm.positionName" placeholder="请输入职位名称" clearable />
          </el-form-item>
          <el-form-item label="企业名称">
            <el-input v-model="filterForm.enterpriseName" placeholder="请输入企业名称" clearable />
          </el-form-item>
          <el-form-item label="职位类型">
            <el-select v-model="filterForm.positionType" placeholder="请选择职位类型" clearable>
              <el-option label="全部" value="" />
              <el-option label="全职" value="FULL_TIME" />
              <el-option label="兼职" value="PART_TIME" />
              <el-option label="实习" value="INTERNSHIP" />
            </el-select>
          </el-form-item>
          <el-form-item label="薪资范围">
            <el-select v-model="filterForm.salaryRange" placeholder="请选择薪资范围" clearable>
              <el-option label="全部" value="" />
              <el-option label="3k以下" value="0-3000" />
              <el-option label="3k-5k" value="3000-5000" />
              <el-option label="5k-8k" value="5000-8000" />
              <el-option label="8k-12k" value="8000-12000" />
              <el-option label="12k-20k" value="12000-20000" />
              <el-option label="20k以上" value="20000-" />
            </el-select>
          </el-form-item>
          <el-form-item label="职位状态">
            <el-select v-model="filterForm.status" placeholder="请选择职位状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="启用" value="ACTIVE" />
              <el-option label="禁用" value="INACTIVE" />
              <el-option label="已过期" value="EXPIRED" />
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
          <el-tag v-if="filterForm.positionName" closable @close="handleClearFilter('positionName')">
            职位名称: {{ filterForm.positionName }}
          </el-tag>
          <el-tag v-if="filterForm.enterpriseName" closable @close="handleClearFilter('enterpriseName')">
            企业名称: {{ filterForm.enterpriseName }}
          </el-tag>
          <el-tag v-if="filterForm.positionType" closable @close="handleClearFilter('positionType')">
            职位类型: {{ getPositionTypeLabel(filterForm.positionType) }}
          </el-tag>
          <el-tag v-if="filterForm.salaryRange" closable @close="handleClearFilter('salaryRange')">
            薪资范围: {{ filterForm.salaryRange }}
          </el-tag>
          <el-tag v-if="filterForm.status" closable @close="handleClearFilter('status')">
            职位状态: {{ getStatusLabel(filterForm.status) }}
          </el-tag>
          <el-button type="text" size="small" @click="resetFilter">清除所有筛选</el-button>
        </span>
      </div>

      <!-- 职位列表 -->
      <el-card class="position-list-card">
        <el-table
          v-loading="loading"
          :data="positions"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="positionId" label="职位ID" width="100" sortable />
          <el-table-column prop="positionName" label="职位名称" min-width="150" sortable />
          <el-table-column prop="enterpriseName" label="企业名称" min-width="150" sortable />
          <el-table-column prop="positionType" label="职位类型" width="120">
            <template #default="scope">
              <el-tag :type="getPositionTypeTagType(scope.row.positionType)">
                {{ getPositionTypeLabel(scope.row.positionType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="salary" label="薪资" width="120" sortable />
          <el-table-column prop="city" label="工作城市" width="120" />
          <el-table-column prop="experience" label="工作经验" width="120" />
          <el-table-column prop="education" label="学历要求" width="120" />
          <el-table-column prop="status" label="职位状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.status)">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" sortable />
          <el-table-column prop="updateTime" label="更新时间" width="180" sortable />
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
              <el-button size="small" type="primary" @click="viewPositionDetail(scope.row.positionId)">
                <el-icon><View /></el-icon>详情
              </el-button>
              <el-button size="small" @click="editPosition(scope.row.positionId)">
                <el-icon><EditPen /></el-icon>编辑
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.positionId)">
                <el-icon><Delete /></el-icon>删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 批量操作和分页 -->
        <div class="table-footer">
          <div class="batch-operations">
            <el-button size="small" type="danger" @click="handleBatchDelete" :disabled="selectedPositions.length === 0">
              <el-icon><Delete /></el-icon>批量删除
            </el-button>
            <span v-if="selectedPositions.length > 0" class="selected-count">
              已选择 {{ selectedPositions.length }} 项
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
  positionName: '',
  enterpriseName: '',
  positionType: '',
  salaryRange: '',
  status: ''
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

// 职位数据
const positions = ref([])

// 选中的职位
const selectedPositions = ref([])

// 获取职位列表
const getPositionList = async () => {
  try {
    loading.value = true
    const params = {
      ...filterForm.value,
      page: currentPage.value,
      size: pageSize.value
    }
    const response = await enterpriseApi.getPositionList(params)
    positions.value = response.data.records || response.data || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取职位列表失败: ' + (error.message || '未知错误'))
    positions.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  getPositionList()
})

// 获取职位类型标签类型
const getPositionTypeTagType = (type) => {
  switch (type) {
    case 'FULL_TIME':
      return 'primary'
    case 'PART_TIME':
      return 'success'
    case 'INTERNSHIP':
      return 'warning'
    default:
      return 'info'
  }
}

// 获取职位类型标签文本
const getPositionTypeLabel = (type) => {
  switch (type) {
    case 'FULL_TIME':
      return '全职'
    case 'PART_TIME':
      return '兼职'
    case 'INTERNSHIP':
      return '实习'
    default:
      return '未知'
  }
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'success'
    case 'INACTIVE':
      return 'danger'
    case 'EXPIRED':
      return 'warning'
    default:
      return 'info'
  }
}

// 获取状态标签文本
const getStatusLabel = (status) => {
  switch (status) {
    case 'ACTIVE':
      return '启用'
    case 'INACTIVE':
      return '禁用'
    case 'EXPIRED':
      return '已过期'
    default:
      return '未知'
  }
}

// 处理筛选
const handleFilter = () => {
  currentPage.value = 1
  getPositionList()
}

// 重置筛选
const resetFilter = () => {
  filterForm.value = {
    positionName: '',
    enterpriseName: '',
    positionType: '',
    salaryRange: '',
    status: ''
  }
  currentPage.value = 1
  getPositionList()
}

// 清除单个筛选条件
const handleClearFilter = (field) => {
  filterForm.value[field] = ''
  currentPage.value = 1
  getPositionList()
}

// 查看职位详情
const viewPositionDetail = (positionId) => {
  router.push(`/admin/position/detail/${positionId}`)
}

// 编辑职位信息
const editPosition = (positionId) => {
  router.push(`/admin/position/form/${positionId}`)
}

// 新增职位
const handleAddPosition = () => {
  router.push(`/admin/position/form`)
}

// 导出职位
const handleExport = () => {
  try {
    // TODO: 实现导出功能
    ElMessage.success('导出职位成功')
  } catch (error) {
    ElMessage.error('导出职位失败: ' + (error.message || '未知错误'))
  }
}

// 删除职位
const handleDelete = (positionId) => {
  ElMessageBox.confirm('确定要删除该职位吗？此操作不可恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true
      await enterpriseApi.deletePosition(positionId)
      ElMessage.success('删除职位成功')
      // 重新获取职位列表
      getPositionList()
    } catch (error) {
      ElMessage.error('删除职位失败: ' + (error.message || '未知错误'))
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消删除操作')
  })
}

// 批量删除职位
const handleBatchDelete = () => {
  if (selectedPositions.value.length === 0) {
    ElMessage.warning('请先选择要删除的职位')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的${selectedPositions.value.length}个职位吗？此操作不可恢复。`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true
      const positionIds = selectedPositions.value.map(position => position.positionId)
      await enterpriseApi.deletePosition(positionIds)
      ElMessage.success(`成功删除${selectedPositions.value.length}个职位`)
      selectedPositions.value = []
      getPositionList()
    } catch (error) {
      ElMessage.error('批量删除职位失败: ' + (error.message || '未知错误'))
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消批量删除操作')
  })
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedPositions.value = selection
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getPositionList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  getPositionList()
}
</script>

<style scoped>
.position-management-container {
  padding: 0;
  margin: 0;
  min-height: 100vh;
  background-color: #f5f7fa;
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

/* 职位列表卡片 */
.position-list-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
}

/* 表格 */
.position-list-card .el-table {
  margin-bottom: 20px;
}

.position-list-card .el-table__header-wrapper th {
  background-color: #f5f7fa;
  font-weight: bold;
  color: #333;
}

.position-list-card .el-table__body-wrapper tr:hover > td {
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