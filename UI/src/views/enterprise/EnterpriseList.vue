<template>
  <div class="enterprise-list-container">
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="企业名称">
          <el-input v-model="searchForm.enterpriseName" placeholder="请输入企业名称" clearable />
        </el-form-item>
        <el-form-item label="所属行业">
          <el-select v-model="searchForm.industry" placeholder="请选择行业" clearable>
            <el-option label="互联网" value="互联网" />
            <el-option label="金融" value="金融" />
            <el-option label="教育" value="教育" />
            <el-option label="医疗" value="医疗" />
            <el-option label="制造业" value="制造业" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业规模">
          <el-select v-model="searchForm.enterpriseScale" placeholder="请选择企业规模" clearable>
            <el-option label="100人以下" value="small" />
            <el-option label="100-500人" value="medium" />
            <el-option label="500-1000人" value="large" />
            <el-option label="1000人以上" value="huge" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover" class="list-card">
      <template #header>
        <div class="card-header">
          <span>企业列表</span>
          <el-button type="primary" @click="handleAdd">新增企业</el-button>
        </div>
      </template>

      <el-table :data="enterpriseList" style="width: 100%" border stripe>
        <el-table-column prop="enterpriseId" label="企业ID" width="100" />
        <el-table-column prop="enterpriseName" label="企业名称" />
        <el-table-column prop="industry" label="所属行业" width="120" />
        <el-table-column prop="enterpriseScale" label="企业规模" width="120">
          <template #default="scope">
            <el-tag :type="getScaleType(scope.row.enterpriseScale)">
              {{ getScaleLabel(scope.row.enterpriseScale) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="所在地" width="150" />
        <el-table-column prop="establishmentDate" label="成立时间" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="1"
              inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">
              删除
            </el-button>
            <el-button type="info" size="small" @click="handleDetail(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
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
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import searchApi from '@/api/modules/search'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  enterpriseName: '',
  industry: '',
  enterpriseScale: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 模拟企业列表数据
const mockEnterpriseList = [
  {
    enterpriseId: 1,
    enterpriseName: '腾讯科技有限公司',
    industry: '互联网',
    enterpriseScale: 'huge',
    location: '深圳市',
    establishmentDate: '1998-11-11',
    status: '1'
  },
  {
    enterpriseId: 2,
    enterpriseName: '阿里巴巴集团',
    industry: '互联网',
    enterpriseScale: 'huge',
    location: '杭州市',
    establishmentDate: '1999-09-10',
    status: '1'
  },
  {
    enterpriseId: 3,
    enterpriseName: '字节跳动科技有限公司',
    industry: '互联网',
    enterpriseScale: 'huge',
    location: '北京市',
    establishmentDate: '2012-03-09',
    status: '1'
  },
  {
    enterpriseId: 4,
    enterpriseName: '招商银行股份有限公司',
    industry: '金融',
    enterpriseScale: 'huge',
    location: '深圳市',
    establishmentDate: '1987-04-08',
    status: '1'
  },
  {
    enterpriseId: 5,
    enterpriseName: '华为技术有限公司',
    industry: '科技',
    enterpriseScale: 'huge',
    location: '深圳市',
    establishmentDate: '1987-09-15',
    status: '1'
  }
]

const enterpriseList = ref([...mockEnterpriseList])

// 根据规模获取标签类型
const getScaleType = (scale) => {
  const typeMap = {
    small: 'primary',
    medium: 'success',
    large: 'warning',
    huge: 'danger'
  }
  return typeMap[scale] || 'info'
}

// 根据规模获取标签文本
const getScaleLabel = (scale) => {
  const labelMap = {
    small: '100人以下',
    medium: '100-500人',
    large: '500-1000人',
    huge: '1000人以上'
  }
  return labelMap[scale] || scale
}

// 查询
const handleSearch = async () => {
  try {
    const response = await searchApi.searchEnterprise({
      keyword: searchForm.enterpriseName,
      industry: searchForm.industry,
      enterpriseScale: searchForm.enterpriseScale
    })
    
    if (response.success) {
      enterpriseList.value = response.data || []
      pagination.total = response.total || enterpriseList.value.length
      ElMessage.success('搜索成功')
    } else {
      ElMessage.warning('搜索结果为空')
      enterpriseList.value = []
    }
  } catch (error) {
    ElMessage.error('搜索失败：' + error.message)
    console.error('Search error:', error)
    // 使用模拟数据作为后备
    enterpriseList.value = [...mockEnterpriseList].filter(enterprise => {
      const matchesName = !searchForm.enterpriseName || enterprise.enterpriseName.includes(searchForm.enterpriseName)
      const matchesIndustry = !searchForm.industry || enterprise.industry === searchForm.industry
      const matchesScale = !searchForm.enterpriseScale || enterprise.enterpriseScale === searchForm.enterpriseScale
      return matchesName && matchesIndustry && matchesScale
    })
  }
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  handleSearch()
}

// 当前页码变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  handleSearch()
}

// 新增企业
const handleAdd = () => {
  router.push('/enterprise/new')
}

// 编辑企业
const handleEdit = (row) => {
  router.push(`/enterprise/edit/${row.enterpriseId}`)
}

// 删除企业
const handleDelete = (row) => {
  ElMessage.success('删除功能待实现')
}

// 查看企业详情
const handleDetail = (row) => {
  router.push(`/enterprise/detail/${row.enterpriseId}`)
}

// 状态变更
const handleStatusChange = (row) => {
  ElMessage.success('状态变更功能待实现')
}
</script>

<style scoped>
.enterprise-list-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
