<template>
  <div class="event-list-container">
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="事件名称">
          <el-input v-model="searchForm.eventName" placeholder="请输入事件名称" clearable />
        </el-form-item>
        <el-form-item label="事件类型">
          <el-select v-model="searchForm.eventType" placeholder="请选择事件类型" clearable>
            <el-option label="招聘" value="RECRUITMENT" />
            <el-option label="宣讲会" value="LECTURE" />
            <el-option label="招聘会" value="JOB_FAIR" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="事件状态">
          <el-select v-model="searchForm.status" placeholder="请选择事件状态" clearable>
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
          <span>事件列表</span>
          <el-button type="primary" @click="handleAdd">新增事件</el-button>
        </div>
      </template>

      <el-table :data="eventList" style="width: 100%" border stripe>
        <el-table-column prop="eventId" label="事件ID" width="100" />
        <el-table-column prop="eventName" label="事件名称" />
        <el-table-column prop="eventType" label="事件类型" width="150" />
        <el-table-column prop="displayPosition" label="展示位置" width="150" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="clickCount" label="点击次数" width="120" />
        <el-table-column prop="viewCount" label="浏览次数" width="120" />
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
          :total="eventList.length"
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
import eventApi from '@/api/modules/event'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  eventName: '',
  eventType: '',
  status: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

// 事件列表
const eventList = ref([])

// 获取事件列表
const fetchEventList = async () => {
  try {
    const response = await eventApi.getEventList(searchForm)
    eventList.value = response.data || []
  } catch (error) {
    ElMessage.error('获取事件列表失败：' + error.message)
    console.error('Error fetching event list:', error)
    // 使用模拟数据作为后备
    eventList.value = [
      {
        eventId: 1,
        eventName: '2025年春季校园招聘会',
        eventType: 'JOB_FAIR',
        displayPosition: 'HOME_TOP',
        startTime: '2025-03-15 09:00:00',
        endTime: '2025-03-15 17:00:00',
        clickCount: 1234,
        viewCount: 56789,
        status: '1'
      },
      {
        eventId: 2,
        eventName: '腾讯企业宣讲会',
        eventType: 'LECTURE',
        displayPosition: 'HOME_MIDDLE',
        startTime: '2025-03-10 14:30:00',
        endTime: '2025-03-10 16:30:00',
        clickCount: 890,
        viewCount: 23456,
        status: '1'
      },
      {
        eventId: 3,
        eventName: '2024年秋季招聘回顾',
        eventType: 'OTHER',
        displayPosition: 'HOME_BOTTOM',
        startTime: '2024-11-01 00:00:00',
        endTime: '2024-12-31 23:59:59',
        clickCount: 567,
        viewCount: 12345,
        status: '0'
      }
    ]
  }
}

// 查询
const handleSearch = () => {
  fetchEventList()
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  fetchEventList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
}

// 当前页码变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
}

// 新增事件
const handleAdd = () => {
  router.push('/event/create')
}

// 编辑事件
const handleEdit = (row) => {
  router.push(`/event/edit/${row.eventId}`)
}

// 删除事件
const handleDelete = async (row) => {
  try {
    const response = await eventApi.deleteEvent(row.eventId)
    if (response.success) {
      ElMessage.success('删除事件成功')
      fetchEventList()
    } else {
      ElMessage.error('删除事件失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('删除事件失败：' + error.message)
    console.error('Error deleting event:', error)
  }
}

// 查看事件详情
const handleDetail = (row) => {
  router.push(`/event/detail/${row.eventId}`)
}

// 页面挂载时获取事件列表
onMounted(() => {
  fetchEventList()
})
</script>

<style scoped>
.event-list-container {
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