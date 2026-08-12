<template>
  <div class="forum-home-container">
    <el-card shadow="hover" class="forum-header-card">
      <h1 class="forum-title">企业招聘论坛</h1>
      <p class="forum-subtitle">分享招聘信息，交流求职经验</p>
    </el-card>

    <el-row :gutter="20">
      <!-- 论坛板块列表 -->
      <el-col :span="8">
        <el-card shadow="hover" class="section-list-card">
          <template #header>
            <div class="card-header">
              <span>论坛板块</span>
              <el-button type="primary" size="small" @click="handleAddSection">新增板块</el-button>
            </div>
          </template>
          
          <el-menu :default-active="activeSection" class="el-menu-vertical-demo" @select="handleSectionSelect">
            <el-menu-item
              v-for="section in sectionList"
              :key="section.sectionId"
              :index="section.sectionId.toString()"
            >
              <template #title>
                <div class="section-item">
                  <span class="section-name">{{ section.sectionName }}</span>
                  <el-badge :value="section.topicCount" class="section-topic-count" />
                </div>
              </template>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 热门主题列表 -->
      <el-col :span="16">
        <el-card shadow="hover" class="topic-list-card">
          <template #header>
            <div class="card-header">
              <span>热门主题</span>
              <el-button type="primary" size="small" @click="handleAddTopic">发布主题</el-button>
            </div>
          </template>
          
          <el-list
            :data="topicList"
            v-for="section in topicListBySection"
            :key="section.sectionId"
            class="topic-list"
          >
            <el-list-item
              v-for="topic in section.topics"
              :key="topic.topicId"
              class="topic-item"
              @click="handleTopicClick(topic)"
            >
              <template #title>
                <div class="topic-title-container">
                  <span class="topic-title">{{ topic.topicTitle }}</span>
                  <el-tag v-if="topic.isTop" type="danger" size="small" effect="dark">置顶</el-tag>
                  <el-tag v-if="topic.isEssence" type="success" size="small" effect="dark">精华</el-tag>
                </div>
              </template>
              <template #description>
                <div class="topic-meta">
                  <span class="topic-author">{{ topic.authorName }}</span>
                  <span class="topic-time">{{ formatDate(topic.createTime) }}</span>
                  <div class="topic-stats">
                    <el-icon class="topic-stat-icon"><View /></el-icon>
                    <span class="topic-stat">{{ topic.viewCount }}</span>
                    <el-icon class="topic-stat-icon"><ChatDotRound /></el-icon>
                    <span class="topic-stat">{{ topic.replyCount }}</span>
                    <el-icon class="topic-stat-icon"><Star /></el-icon>
                    <span class="topic-stat">{{ topic.likeCount }}</span>
                  </div>
                </div>
              </template>
            </el-list-item>
          </el-list>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { View, ChatDotRound, Star } from '@element-plus/icons-vue'
import forumApi from '@/api/modules/forum'

const router = useRouter()

// 激活的板块
const activeSection = ref('')

// 板块列表
const sectionList = ref([])

// 主题列表
const topicList = ref([])

// 按板块分组的主题列表
const topicListBySection = computed(() => {
  // 这里简化处理，实际应该按板块分组
  return [
    {
      sectionId: activeSection.value || '1',
      topics: topicList.value
    }
  ]
})

// 获取板块列表
const fetchSectionList = async () => {
  try {
    const response = await forumApi.forumSection.getSectionList()
    sectionList.value = response.data || []
    if (sectionList.value.length > 0) {
      activeSection.value = sectionList.value[0].sectionId.toString()
    }
  } catch (error) {
    ElMessage.error('获取板块列表失败：' + error.message)
    console.error('Error fetching section list:', error)
    // 使用模拟数据作为后备
    sectionList.value = [
      {
        sectionId: 1,
        sectionName: '招聘信息',
        topicCount: 123
      },
      {
        sectionId: 2,
        sectionName: '求职经验',
        topicCount: 89
      },
      {
        sectionId: 3,
        sectionName: '面试技巧',
        topicCount: 67
      },
      {
        sectionId: 4,
        sectionName: '职场话题',
        topicCount: 45
      }
    ]
    activeSection.value = '1'
  }
}

// 获取主题列表
const fetchTopicList = async (sectionId) => {
  try {
    const params = sectionId ? { sectionId } : {}
    const response = await forumApi.forumTopic.getTopicList(params)
    topicList.value = response.data || []
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
        isTop: true,
        isEssence: false,
        sectionId: 1
      },
      {
        topicId: 2,
        topicTitle: '分享一下我的阿里面试经验',
        authorName: '面试达人',
        createTime: '2025-03-09 09:15:00',
        viewCount: 876,
        replyCount: 43,
        likeCount: 123,
        isTop: false,
        isEssence: true,
        sectionId: 2
      },
      {
        topicId: 3,
        topicTitle: '如何准备技术面试？',
        authorName: '菜鸟求带',
        createTime: '2025-03-08 16:45:00',
        viewCount: 543,
        replyCount: 23,
        likeCount: 45,
        isTop: false,
        isEssence: false,
        sectionId: 3
      }
    ]
  }
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 板块选择
const handleSectionSelect = (sectionId) => {
  activeSection.value = sectionId
  // 根据板块ID获取对应的主题列表
  fetchTopicList(sectionId)
}

// 新增板块
const handleAddSection = () => {
  router.push('/forum/section/create')
}

// 新增主题
const handleAddTopic = () => {
  router.push('/forum/topic/create')
}

// 点击主题
const handleTopicClick = (topic) => {
  router.push(`/forum/topic/detail/${topic.topicId}`)
}

// 页面挂载时获取数据
onMounted(() => {
  fetchSectionList()
  fetchTopicList()
})
</script>

<style scoped>
.forum-home-container {
  padding: 20px;
}

.forum-header-card {
  margin-bottom: 20px;
  text-align: center;
}

.forum-title {
  font-size: 28px;
  margin-bottom: 10px;
  color: #303133;
}

.forum-subtitle {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.section-list-card {
  height: calc(100vh - 200px);
}

.section-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-name {
  font-size: 14px;
  font-weight: 500;
}

.section-topic-count {
  margin-left: 10px;
}

.topic-list-card {
  height: calc(100vh - 200px);
  overflow-y: auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.topic-list {
  margin-bottom: 20px;
}

.topic-item {
  cursor: pointer;
  transition: all 0.3s;
}

.topic-item:hover {
  background-color: #f5f7fa;
}

.topic-title-container {
  display: flex;
  align-items: center;
}

.topic-title {
  font-size: 16px;
  font-weight: 500;
  margin-right: 10px;
}

.topic-meta {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.topic-author {
  margin-right: 20px;
}

.topic-time {
  margin-right: 20px;
}

.topic-stats {
  display: flex;
  align-items: center;
  margin-left: auto;
}

.topic-stat-icon {
  margin-right: 2px;
  font-size: 14px;
}

.topic-stat {
  margin-right: 15px;
}
</style>