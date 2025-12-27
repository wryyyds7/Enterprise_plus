<template>
  <div class="enterprise-management-container">
    <!-- 导航头 -->
    <NavigationHeader />
    
    <div class="page-content">
      <div class="page-header">
        <h2>企业管理</h2>
      </div>

      <!-- 筛选条件 -->
      <el-card class="filter-card">
        <el-form :model="filterForm" inline>
          <el-form-item label="企业名称">
            <el-input v-model="filterForm.enterpriseName" placeholder="请输入企业名称" />
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select v-model="filterForm.auditStatus" placeholder="请选择审核状态">
              <el-option label="全部" value="" />
              <el-option label="待审核" value="PENDING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
            </el-select>
          </el-form-item>
          <el-form-item label="企业类型">
            <el-select v-model="filterForm.enterpriseType" placeholder="请选择企业类型">
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

      <!-- 企业列表 -->
      <el-card class="enterprise-list-card">
        <el-table :data="filteredEnterprises" stripe style="width: 100%" :loading="loading">
          <el-table-column prop="enterpriseId" label="企业ID" width="100" />
          <el-table-column prop="enterpriseName" label="企业名称" min-width="150" />
          <el-table-column prop="enterpriseType" label="企业类型" width="120">
            <template #default="scope">
              <el-tag :type="getTypeTagType(scope.row.enterpriseType)">
                {{ getTypeLabel(scope.row.enterpriseType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="industry" label="所属行业" width="120" />
          <el-table-column prop="contactPerson" label="联系人" width="100" />
          <el-table-column prop="contactPhone" label="联系电话" width="150" />
          <el-table-column prop="auditStatus" label="审核状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.auditStatus)">
                {{ getStatusLabel(scope.row.auditStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="updateTime" label="更新时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button size="small" type="primary" @click="viewEnterpriseDetail(scope.row.enterpriseId)">
                查看详情
              </el-button>
              <el-button size="small" v-if="scope.row.auditStatus === 'PENDING'" @click="handleAudit(scope.row, 'APPROVED')">
                通过
              </el-button>
              <el-button size="small" type="danger" v-if="scope.row.auditStatus === 'PENDING'" @click="handleAudit(scope.row, 'REJECTED')">
                拒绝
              </el-button>
              <el-button size="small" @click="editEnterprise(scope.row.enterpriseId)">
                编辑
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
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import router from '@/router'
import NavigationHeader from '@/components/NavigationHeader.vue'
import adminApi from '@/api/modules/admin.js'

// 筛选表单
const filterForm = ref({
  enterpriseName: '',
  auditStatus: '',
  enterpriseType: ''
})

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 企业数据
const enterprises = ref([])

// 获取企业列表
const getEnterpriseList = async () => {
  try {
    loading.value = true
    const params = {
      ...filterForm.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    const response = await adminApi.getEnterpriseList(params)
    enterprises.value = response.data.list || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取企业列表失败: ' + (error.message || '未知错误'))
    enterprises.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 筛选后的企业列表
const filteredEnterprises = computed(() => {
  return enterprises.value.filter(enterprise => {
    const matchesName = !filterForm.value.enterpriseName || 
      enterprise.enterpriseName.toLowerCase().includes(filterForm.value.enterpriseName.toLowerCase())
    const matchesStatus = !filterForm.value.auditStatus || 
      enterprise.auditStatus === filterForm.value.auditStatus
    const matchesType = !filterForm.value.enterpriseType || 
      enterprise.enterpriseType === filterForm.value.enterpriseType
    return matchesName && matchesStatus && matchesType
  })
})

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
    auditStatus: '',
    enterpriseType: ''
  }
  currentPage.value = 1
  getEnterpriseList()
}

// 处理审核
const handleAudit = (enterprise, status) => {
  const newStatus = status === 'APPROVED' ? '已通过' : '已拒绝'
  ElMessage.confirm(`确定要${newStatus}该企业吗？`, '审核确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      loading.value = true
      await adminApi.auditEnterprise({
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
  router.push(`/enterprise/detail/${enterpriseId}`)
}

// 编辑企业信息
const editEnterprise = (enterpriseId) => {
  router.push(`/enterprise/form/${enterpriseId}`)
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

.enterprise-list-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>