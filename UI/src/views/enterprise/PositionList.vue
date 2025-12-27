<template>
  <div class="position-list-container">
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="职位名称">
          <el-input v-model="searchForm.positionName" placeholder="请输入职位名称" clearable />
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="searchForm.department" placeholder="请选择部门" clearable>
            <el-option label="技术部" value="技术部" />
            <el-option label="产品部" value="产品部" />
            <el-option label="设计部" value="设计部" />
            <el-option label="市场部" value="市场部" />
            <el-option label="销售部" value="销售部" />
            <el-option label="人力资源部" value="人力资源部" />
          </el-select>
        </el-form-item>
        <el-form-item label="薪资范围">
          <el-select v-model="searchForm.salaryRange" placeholder="请选择薪资范围" clearable>
            <el-option label="10K以下" value="0-10K" />
            <el-option label="10-20K" value="10-20K" />
            <el-option label="20-30K" value="20-30K" />
            <el-option label="30-50K" value="30-50K" />
            <el-option label="50K以上" value="50K+" />
          </el-select>
        </el-form-item>
        <el-form-item label="学历要求">
          <el-select v-model="searchForm.education" placeholder="请选择学历要求" clearable>
            <el-option label="大专" value="大专" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
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
          <span>职位列表</span>
          <el-button type="primary" @click="handleAdd">新增职位</el-button>
        </div>
      </template>

      <el-table :data="positionList" style="width: 100%" border stripe>
        <el-table-column prop="positionId" label="职位ID" width="100" />
        <el-table-column prop="positionName" label="职位名称" />
        <el-table-column prop="department" label="部门" width="120" />
        <el-table-column prop="salaryRange" label="薪资范围" width="120" />
        <el-table-column prop="workLocation" label="工作地点" width="150" />
        <el-table-column prop="education" label="学历要求" width="120" />
        <el-table-column prop="experience" label="经验要求" width="120" />
        <el-table-column prop="positionType" label="职位类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.positionType === '全职' ? 'success' : 'warning'">
              {{ scope.row.positionType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishDate" label="发布时间" width="150" />
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
          :total="positionList.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 搜索表单
const searchForm = reactive({
  positionName: '',
  department: '',
  salaryRange: '',
  education: '',
  experience: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

// 获取企业ID（从路由参数或其他地方获取）
const enterpriseId = ref(route.params.enterpriseId || 1)

// 模拟职位列表数据
const mockPositionList = [
  {
    positionId: 1,
    positionName: '前端开发工程师',
    department: '技术部',
    salaryRange: '20-30K',
    workLocation: '深圳市',
    education: '本科',
    experience: '1-3年',
    positionType: '全职',
    publishDate: '2024-01-15',
    status: '1'
  },
  {
    positionId: 2,
    positionName: '后端开发工程师',
    department: '技术部',
    salaryRange: '20-30K',
    workLocation: '深圳市',
    education: '本科',
    experience: '1-3年',
    positionType: '全职',
    publishDate: '2024-01-15',
    status: '1'
  },
  {
    positionId: 3,
    positionName: '产品经理',
    department: '产品部',
    salaryRange: '15-25K',
    workLocation: '深圳市',
    education: '本科',
    experience: '1-3年',
    positionType: '全职',
    publishDate: '2024-01-14',
    status: '1'
  },
  {
    positionId: 4,
    positionName: 'UI设计师',
    department: '设计部',
    salaryRange: '15-25K',
    workLocation: '深圳市',
    education: '本科',
    experience: '1-3年',
    positionType: '全职',
    publishDate: '2024-01-14',
    status: '1'
  },
  {
    positionId: 5,
    positionName: '市场专员',
    department: '市场部',
    salaryRange: '10-15K',
    workLocation: '深圳市',
    education: '本科',
    experience: '0-1年',
    positionType: '全职',
    publishDate: '2024-01-13',
    status: '1'
  },
  {
    positionId: 6,
    positionName: '销售经理',
    department: '销售部',
    salaryRange: '30-50K',
    workLocation: '广州市',
    education: '大专',
    experience: '3-5年',
    positionType: '全职',
    publishDate: '2024-01-13',
    status: '1'
  },
  {
    positionId: 7,
    positionName: '人力资源专员',
    department: '人力资源部',
    salaryRange: '8-12K',
    workLocation: '深圳市',
    education: '本科',
    experience: '0-1年',
    positionType: '全职',
    publishDate: '2024-01-12',
    status: '1'
  },
  {
    positionId: 8,
    positionName: '测试工程师',
    department: '技术部',
    salaryRange: '15-20K',
    workLocation: '深圳市',
    education: '本科',
    experience: '1-3年',
    positionType: '全职',
    publishDate: '2024-01-12',
    status: '0'
  }
]

const positionList = ref([...mockPositionList])

// 查询
const handleSearch = () => {
  // 这里应该调用API进行搜索
  ElMessage.success('查询功能待实现')
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
}

// 当前页码变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
}

// 新增职位
const handleAdd = () => {
  router.push(`/enterprise/${enterpriseId.value}/position/new`)
}

// 编辑职位
const handleEdit = (row) => {
  router.push(`/enterprise/${enterpriseId.value}/position/edit/${row.positionId}`)
}

// 删除职位
const handleDelete = (row) => {
  ElMessage.success('删除功能待实现')
}

// 查看职位详情
const handleDetail = (row) => {
  router.push(`/enterprise/${enterpriseId.value}/position/${row.positionId}`)
}

// 状态变更
const handleStatusChange = (row) => {
  ElMessage.success('状态变更功能待实现')
}
</script>

<style scoped>
.position-list-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
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
