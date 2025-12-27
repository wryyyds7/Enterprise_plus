<template>
  <div class="activity-list-container">
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="活动名称">
          <el-input v-model="searchForm.activityName" placeholder="请输入活动名称" clearable />
        </el-form-item>
        <el-form-item label="活动状态">
          <el-select v-model="searchForm.status" placeholder="请选择活动状态" clearable>
            <el-option label="进行中" value="1" />
            <el-option label="已结束" value="0" />
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
          <span>活动列表</span>
          <el-button type="primary" @click="handleAdd">新增活动</el-button>
        </div>
      </template>

      <el-table :data="activityList" style="width: 100%" border stripe>
        <el-table-column prop="activityId" label="活动ID" width="100" />
        <el-table-column prop="activityName" label="活动名称" />
        <el-table-column prop="activityType" label="活动类型" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="location" label="活动地点" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">
              {{ scope.row.status === '1' ? '进行中' : '已结束' }}
            </el-tag>
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
          :total="activityList.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import activityApi from '@/api/modules/activity'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  activityName: '',
  status: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

// 活动列表
const activityList = ref([])

// 获取活动列表
const fetchActivityList = async () => {
  try {
    const response = await activityApi.getActivityList(searchForm)
    activityList.value = response.data || []
  } catch (error) {
    ElMessage.error('获取活动列表失败：' + error.message)
    console.error('Error fetching activity list:', error)
    // 使用模拟数据作为后备
    activityList.value = [
      {
        activityId: 1,
        activityName: '2025年春季校园招聘会',
        activityType: '招聘会',
        startTime: '2025-03-15 09:00:00',
        endTime: '2025-03-15 17:00:00',
        location: '图书馆前广场',
        status: '1'
      },
      {
        activityId: 2,
        activityName: '企业宣讲会 - 腾讯',
        activityType: '宣讲会',
        startTime: '2025-03-10 14:30:00',
        endTime: '2025-03-10 16:30:00',
        location: '教学楼A301',
        status: '1'
      },
      {
        activityId: 3,
        activityName: '2024年秋季校园招聘会',
        activityType: '招聘会',
        startTime: '2024-10-20 09:00:00',
        endTime: '2024-10-20 17:00:00',
        location: '体育馆',
        status: '0'
      }
    ]
  }
}

// 查询
const handleSearch = () => {
  fetchActivityList()
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  fetchActivityList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
}

// 当前页码变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
}

// 新增活动
const handleAdd = () => {
  router.push('/activity/create')
}

// 编辑活动
const handleEdit = (row) => {
  router.push(`/activity/edit/${row.activityId}`)
}

// 删除活动
const handleDelete = async (row) => {
  try {
    const response = await activityApi.deleteActivity(row.activityId)
    if (response.success) {
      ElMessage.success('删除活动成功')
      fetchActivityList()
    } else {
      ElMessage.error('删除活动失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('删除活动失败：' + error.message)
    console.error('Error deleting activity:', error)
  }
}

// 查看活动详情
const handleDetail = (row) => {
  router.push(`/activity/detail/${row.activityId}`)
}

// 页面挂载时获取活动列表
onMounted(() => {
  fetchActivityList()
})
</script>

<style scoped>
.activity-list-container {
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