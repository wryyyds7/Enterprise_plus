<template>
  <div class="event-management-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>校园招聘活动管理</h2>
          <el-button type="primary" @click="handleAdd">新增活动</el-button>
        </div>
      </template>

      <!-- 搜索条件 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="活动名称">
          <el-input v-model="searchForm.eventName" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="searchForm.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="活动状态">
          <el-select v-model="searchForm.status" placeholder="请选择活动状态">
            <el-option label="全部" value="" />
            <el-option label="未开始" value="0" />
            <el-option label="进行中" value="1" />
            <el-option label="已结束" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 活动列表 -->
      <div class="event-list-container">
        <el-table v-loading="loading" :data="eventList" stripe border>
          <el-table-column prop="eventId" label="活动ID" width="80" />
          <el-table-column prop="eventName" label="活动名称" min-width="180" />
          <el-table-column prop="location" label="活动地点" min-width="120" />
          <el-table-column prop="startTime" label="开始时间" width="180" />
          <el-table-column prop="endTime" label="结束时间" width="180" />
          <el-table-column prop="organizer" label="主办方" width="120" />
          <el-table-column prop="companyCount" label="参与企业数" width="100" />
          <el-table-column prop="registeredCount" label="报名人数" width="100" />
          <el-table-column prop="status" label="活动状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleView(scope.row)">查看详情</el-button>
              <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 活动详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="活动详情"
      width="700px"
    >
      <div v-if="selectedEvent" class="event-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="活动ID">{{ selectedEvent.eventId }}</el-descriptions-item>
          <el-descriptions-item label="活动名称">{{ selectedEvent.eventName }}</el-descriptions-item>
          <el-descriptions-item label="活动地点">{{ selectedEvent.location }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ selectedEvent.startTime }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ selectedEvent.endTime }}</el-descriptions-item>
          <el-descriptions-item label="主办方">{{ selectedEvent.organizer }}</el-descriptions-item>
          <el-descriptions-item label="参与企业数">{{ selectedEvent.companyCount }}</el-descriptions-item>
          <el-descriptions-item label="报名人数">{{ selectedEvent.registeredCount }}</el-descriptions-item>
          <el-descriptions-item label="活动状态">{{ getStatusLabel(selectedEvent.status) }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ selectedEvent.createTime }}</el-descriptions-item>
          <el-descriptions-item label="活动简介" :span="2">{{ selectedEvent.description }}</el-descriptions-item>
          <el-descriptions-item label="活动详情" :span="2">
            <div class="event-content" v-html="selectedEvent.content"></div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import adminApi from '@/api/modules/admin'

const router = useRouter()
const loading = ref(false)
const eventList = ref([])
const detailDialogVisible = ref(false)
const selectedEvent = ref(null)

// 搜索表单
const searchForm = reactive({
  eventName: '',
  location: '',
  status: ''
})

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 活动状态映射
const statusMap = {
  '0': '未开始',
  '1': '进行中',
  '2': '已结束'
}

// 活动状态类型
const statusTypeMap = {
  '0': 'info',
  '1': 'success',
  '2': 'warning'
}

// 获取状态标签
const getStatusLabel = (status) => {
  return statusMap[status] || '未知状态'
}

// 获取状态类型
const getStatusType = (status) => {
  return statusTypeMap[status] || 'default'
}

// 获取活动列表
const getEventList = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize
    }
    const response = await adminApi.getEventList(params)
    if (response && response.success) {
      eventList.value = response.rows || []
      pagination.total = response.total || 0
    } else {
      ElMessage.error((response && response.message) || '获取活动列表失败')
    }
  } catch (error) {
    ElMessage.error('获取活动列表失败')
    console.error('获取活动列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 搜索活动
const handleSearch = () => {
  pagination.currentPage = 1
  getEventList()
}

// 重置搜索条件
const handleReset = () => {
  Object.assign(searchForm, {
    eventName: '',
    location: '',
    status: ''
  })
  pagination.currentPage = 1
  getEventList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  getEventList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  getEventList()
}

// 新增活动
const handleAdd = () => {
  router.push('/admin/event/new')
}

// 编辑活动
const handleEdit = (row) => {
  router.push(`/admin/event/edit/${row.eventId}`)
}

// 查看活动详情
const handleView = async (row) => {
  try {
    const response = await adminApi.getEventDetail(row.eventId)
    if (response && response.success) {
      selectedEvent.value = response.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error((response && response.message) || '获取活动详情失败')
    }
  } catch (error) {
    ElMessage.error('获取活动详情失败')
    console.error('获取活动详情失败：', error)
  }
}

// 删除活动
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除活动「${row.eventName}」吗？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await adminApi.deleteEvent([row.eventId])
      if (response && response.success) {
        ElMessage.success('活动删除成功')
        getEventList()
      } else {
        ElMessage.error((response && response.message) || '活动删除失败')
      }
    } catch (error) {
      ElMessage.error('活动删除失败')
      console.error('活动删除失败：', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

onMounted(() => {
  getEventList()
})
</script>

<style scoped>
.event-management-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.event-list-container {
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.event-detail {
  margin-top: 20px;
}

.event-content {
  white-space: pre-wrap;
  word-break: break-word;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>