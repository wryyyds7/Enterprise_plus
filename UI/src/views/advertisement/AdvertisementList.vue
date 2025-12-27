<template>
  <div class="advertisement-list-container">
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="广告名称">
          <el-input v-model="searchForm.adName" placeholder="请输入广告名称" clearable />
        </el-form-item>
        <el-form-item label="展示位置">
          <el-select v-model="searchForm.displayPosition" placeholder="请选择展示位置" clearable>
            <el-option label="首页顶部" value="HOME_TOP" />
            <el-option label="首页中部" value="HOME_MIDDLE" />
            <el-option label="首页底部" value="HOME_BOTTOM" />
            <el-option label="详情页" value="DETAIL" />
            <el-option label="列表页" value="LIST" />
          </el-select>
        </el-form-item>
        <el-form-item label="广告状态">
          <el-select v-model="searchForm.status" placeholder="请选择广告状态" clearable>
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
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
          <span>广告列表</span>
          <el-button type="primary" @click="handleAdd">新增广告</el-button>
        </div>
      </template>

      <el-table :data="advertisementList" style="width: 100%" border stripe>
        <el-table-column prop="adId" label="广告ID" width="100" />
        <el-table-column prop="adName" label="广告名称" />
        <el-table-column prop="displayPosition" label="展示位置" width="150" />
        <el-table-column prop="adType" label="广告类型" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="clickCount" label="点击次数" width="120" />
        <el-table-column prop="viewCount" label="浏览次数" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">
              {{ scope.row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="info" size="small" @click="handleDetail(scope.row)">
              详情
            </el-button>
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">
              删除
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
          :total="advertisementList.length"
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
import advertisementApi from '@/api/modules/advertisement'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  adName: '',
  displayPosition: '',
  status: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

// 广告列表
const advertisementList = ref([])

// 获取广告列表
const fetchAdvertisementList = async () => {
  try {
    const response = await advertisementApi.getAdvertisementList(searchForm)
    advertisementList.value = response.data || []
  } catch (error) {
    ElMessage.error('获取广告列表失败：' + error.message)
    console.error('Error fetching advertisement list:', error)
    // 使用模拟数据作为后备
    advertisementList.value = [
      {
        adId: 1,
        adName: '2025春季招聘会',
        displayPosition: 'TOP',
        adType: 'BANNER',
        startTime: '2025-03-01 00:00:00',
        endTime: '2025-03-31 23:59:59',
        clickCount: 1234,
        viewCount: 56789,
        status: '1'
      },
      {
        adId: 2,
        adName: '腾讯企业宣讲会',
        displayPosition: 'MIDDLE',
        adType: 'VIDEO',
        startTime: '2025-03-05 00:00:00',
        endTime: '2025-03-15 23:59:59',
        clickCount: 890,
        viewCount: 23456,
        status: '1'
      },
      {
        adId: 3,
        adName: '2024秋季招聘回顾',
        displayPosition: 'BOTTOM',
        adType: 'TEXT',
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
  fetchAdvertisementList()
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  fetchAdvertisementList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
}

// 当前页码变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
}

// 新增广告
const handleAdd = () => {
  router.push('/advertisement/create')
}

// 编辑广告
const handleEdit = (row) => {
  router.push(`/advertisement/edit/${row.adId}`)
}

// 删除广告
const handleDelete = async (row) => {
  try {
    const response = await advertisementApi.deleteAdvertisement(row.adId)
    if (response.success) {
      ElMessage.success('删除广告成功')
      fetchAdvertisementList()
    } else {
      ElMessage.error('删除广告失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('删除广告失败：' + error.message)
    console.error('Error deleting advertisement:', error)
  }
}

// 查看广告详情
const handleDetail = (row) => {
  router.push(`/advertisement/detail/${row.adId}`)
}

// 页面挂载时获取广告列表
onMounted(() => {
  fetchAdvertisementList()
})
</script>

<style scoped>
.advertisement-list-container {
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