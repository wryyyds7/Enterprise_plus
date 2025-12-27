<template>
  <div class="forum-section-detail-container">
    <el-card shadow="hover" class="section-info-card">
      <template #header>
        <div class="card-header">
          <h2>{{ section.sectionName }}</h2>
          <div class="section-actions">
            <el-button type="primary" @click="handleAddTopic">发布主题</el-button>
            <el-button type="info" @click="handleEditSection">编辑板块</el-button>
            <el-button type="danger" @click="handleDeleteSection">删除板块</el-button>
          </div>
        </div>
      </template>
      
      <div class="section-description">
        <p>{{ section.description || '该板块暂无描述' }}</p>
      </div>
      
      <div class="section-stats">
        <el-tag type="info" effect="plain">主题数: {{ topicList.length }}</el-tag>
        <el-tag type="success" effect="plain">今日发帖: {{ todayTopicCount }}</el-tag>
      </div>
    </el-card>
    
    <!-- 主题搜索 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="主题标题">
          <el-input
            v-model="searchForm.topicTitle"
            placeholder="请输入主题标题"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 主题列表 -->
    <el-card shadow="hover" class="topic-list-card">
      <template #header>
        <div class="card-header">
          <h3>主题列表</h3>
        </div>
      </template>
      
      <el-table :data="topicList" style="width: 100%" border stripe>
        <el-table-column prop="topicId" label="主题ID" width="100" />
        <el-table-column label="主题标题" min-width="300">
          <template #default="scope">
            <div class="topic-title-container">
              <span class="topic-title" @click="handleTopicClick(scope.row)">{{ scope.row.topicTitle }}</span>
              <el-tag v-if="scope.row.isTop" type="danger" size="small" effect="dark">置顶</el-tag>
              <el-tag v-if="scope.row.isEssence" type="success" size="small" effect="dark">精华</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="createTime" label="发布时间" width="180" formatter="formatDate" />
        <el-table-column prop="viewCount" label="浏览次数" width="100" />
        <el-table-column prop="replyCount" label="回复次数" width="100" />
        <el-table-column prop="likeCount" label="点赞次数" width="100" />
        <el-table-column prop="favoriteCount" label="收藏次数" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleTopicClick(scope.row)">
              查看
            </el-button>
            <el-button type="warning" size="small" @click="handleEditTopic(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteTopic(scope.row)">
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
          :total="topicList.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import forumApi from '@/api/modules/forum'

const route = useRoute()
const router = useRouter()
const sectionId = route.params.sectionId

// 板块信息
const section = ref({
  sectionId: '',
  sectionName: '',
  description: '',
  parentSectionId: null,
  createTime: ''
})

// 搜索表单
const searchForm = reactive({
  topicTitle: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

// 主题列表
const topicList = ref([])

// 今日发帖数
const todayTopicCount = computed(() => {
  const today = new Date().toDateString()
  return topicList.value.filter(topic => {
    return new Date(topic.createTime).toDateString() === today
  }).length
})

// 格式化日期
const formatDate = (row, column, cellValue) => {
  if (!cellValue) return ''
  const date = new Date(cellValue)
  return date.toLocaleString()
}

// 获取板块详情
const fetchSectionDetail = async () => {
  try {
    const response = await forumApi.forumSection.getSectionById(sectionId)
    if (response.success) {
      section.value = response.data
    } else {
      ElMessage.error('获取板块详情失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取板块详情失败：' + error.message)
    console.error('Error fetching section detail:', error)
  }
}

// 获取主题列表
const fetchTopicList = async () => {
  try {
    const params = {
      sectionId: sectionId,
      ...searchForm
    }
    const response = await forumApi.forumTopic.getTopicListBySectionId(params)
    if (response.success) {
      topicList.value = response.data || []
    } else {
      ElMessage.error('获取主题列表失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取主题列表失败：' + error.message)
    console.error('Error fetching topic list:', error)
    // 使用模拟数据作为后备
    topicList.value = [
      {
        topicId: 1,
        topicTitle: '腾讯2025校招开始啦！',
        authorName: '腾讯招聘',
        createTime: '2025-03-10 14:30:00',
        viewCount: 1234,
        replyCount: 56,
        likeCount: 89,
        favoriteCount: 45,
        isTop: true,
        isEssence: false,
        sectionId: sectionId
      },
      {
        topicId: 2,
        topicTitle: '分享一下我的阿里面试经验',
        authorName: '面试达人',
        createTime: '2025-03-09 09:15:00',
        viewCount: 876,
        replyCount: 43,
        likeCount: 123,
        favoriteCount: 67,
        isTop: false,
        isEssence: true,
        sectionId: sectionId
      },
      {
        topicId: 3,
        topicTitle: '如何准备技术面试？',
        authorName: '菜鸟求带',
        createTime: '2025-03-08 16:45:00',
        viewCount: 543,
        replyCount: 23,
        likeCount: 45,
        favoriteCount: 23,
        isTop: false,
        isEssence: false,
        sectionId: sectionId
      }
    ]
  }
}

// 查询
const handleSearch = () => {
  fetchTopicList()
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  fetchTopicList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
}

// 当前页码变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
}

// 新增主题
const handleAddTopic = () => {
  router.push(`/forum/topic/create?sectionId=${sectionId}`)
}

// 查看主题
const handleTopicClick = (topic) => {
  router.push(`/forum/topic/detail/${topic.topicId}`)
}

// 编辑主题
const handleEditTopic = (topic) => {
  router.push(`/forum/topic/edit/${topic.topicId}`)
}

// 删除主题
const handleDeleteTopic = async (topic) => {
  try {
    await ElMessageBox.confirm('确定要删除该主题吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await forumApi.forumTopic.deleteTopic(topic.topicId)
    if (response.success) {
      ElMessage.success('删除主题成功')
      fetchTopicList()
    } else {
      ElMessage.error('删除主题失败：' + response.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除主题失败：' + (error.message || '操作取消'))
      console.error('Error deleting topic:', error)
    }
  }
}

// 编辑板块
const handleEditSection = () => {
  router.push(`/forum/section/edit/${sectionId}`)
}

// 删除板块
const handleDeleteSection = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该板块吗？删除后将无法恢复。', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await forumApi.forumSection.deleteSection(sectionId)
    if (response.success) {
      ElMessage.success('删除板块成功')
      router.push('/forum')
    } else {
      ElMessage.error('删除板块失败：' + response.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除板块失败：' + (error.message || '操作取消'))
      console.error('Error deleting section:', error)
    }
  }
}

// 页面挂载时获取数据
onMounted(() => {
  fetchSectionDetail()
  fetchTopicList()
})
</script>

<style scoped>
.forum-section-detail-container {
  padding: 20px;
}

.section-info-card {
  margin-bottom: 20px;
}

.section-info-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.section-info-card h2 {
  margin: 0;
  color: #303133;
}

.section-actions {
  display: flex;
  gap: 10px;
}

.section-description {
  margin: 20px 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  color: #606266;
}

.section-stats {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  gap: 20px;
}

.topic-list-card {
  margin-bottom: 20px;
}

.topic-title-container {
  display: flex;
  align-items: center;
}

.topic-title {
  cursor: pointer;
  color: #303133;
  margin-right: 10px;
}

.topic-title:hover {
  color: #409eff;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>