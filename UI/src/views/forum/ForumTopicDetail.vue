<template>
  <div class="forum-topic-detail-container">
    <!-- 主题详情 -->
    <el-card shadow="hover" class="topic-detail-card">
      <template #header>
        <div class="card-header">
          <h2>{{ topic.topicTitle }}</h2>
          <div class="topic-actions">
            <el-button type="primary" size="small" @click="handleEditTopic">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteTopic">删除</el-button>
          </div>
        </div>
      </template>
      
      <div class="topic-meta">
        <div class="author-info">
          <el-avatar :size="40" :src="topic.authorAvatar || defaultAvatar">{{ topic.authorName?.charAt(0) }}</el-avatar>
          <div class="author-details">
            <span class="author-name">{{ topic.authorName }}</span>
            <span class="publish-time">{{ formatDate(topic.createTime) }}</span>
          </div>
        </div>
        <div class="topic-stats">
          <el-button type="text" @click="handleLike" :icon="isLiked ? 'StarFilled' : 'Star'" :type="isLiked ? 'warning' : ''">
            {{ topic.likeCount }} 赞
          </el-button>
          <el-button type="text" @click="handleFavorite" :icon="isFavorited ? 'StarFilled' : 'Star'" :type="isFavorited ? 'success' : ''">
            {{ topic.favoriteCount }} 收藏
          </el-button>
          <el-button type="text" icon="View">
            {{ topic.viewCount }} 浏览
          </el-button>
        </div>
      </div>
      
      <div class="topic-content">
        <p>{{ topic.topicContent }}</p>
      </div>
    </el-card>
    
    <!-- 回复表单 -->
    <el-card shadow="hover" class="reply-form-card">
      <template #header>
        <h3>发表回复</h3>
      </template>
      
      <el-form ref="replyFormRef" :model="replyForm" :rules="replyRules" class="reply-form">
        <el-form-item prop="replyContent">
          <el-input
            v-model="replyForm.replyContent"
            type="textarea"
            placeholder="请输入回复内容"
            rows="4"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <div class="form-actions">
          <el-button type="primary" @click="handleSubmitReply" :loading="isSubmitting">发表回复</el-button>
        </div>
      </el-form>
    </el-card>
    
    <!-- 回复列表 -->
    <el-card shadow="hover" class="reply-list-card">
      <template #header>
        <h3>回复列表 ({{ replyList.length }})</h3>
      </template>
      
      <el-list :data="replyList" class="reply-list">
        <el-list-item v-for="(reply, index) in replyList" :key="reply.replyId" class="reply-item">
          <template #default>
            <div class="reply-content">{{ reply.replyContent }}</div>
            <div class="reply-meta">
              <span class="reply-author">{{ reply.authorName }}</span>
              <span class="reply-time">{{ formatDate(reply.createTime) }}</span>
              <div class="reply-actions">
                <el-button type="text" @click="handleLikeReply(reply)" :icon="'Star'">
                  {{ reply.likeCount }} 赞
                </el-button>
                <el-button type="text" @click="handleReplyToReply(reply)">回复</el-button>
                <el-button type="text" @click="handleEditReply(reply)">编辑</el-button>
                <el-button type="text" @click="handleDeleteReply(reply)" style="color: #f56c6c">删除</el-button>
              </div>
            </div>
            
            <!-- 子回复列表 -->
            <el-collapse v-if="reply.childReplies && reply.childReplies.length > 0">
              <el-collapse-item title="查看{{ reply.childReplies.length }}条回复" name="1">
                <el-list :data="reply.childReplies" class="child-reply-list">
                  <el-list-item v-for="childReply in reply.childReplies" :key="childReply.replyId" class="child-reply-item">
                    <template #default>
                      <div class="reply-content">{{ childReply.replyContent }}</div>
                      <div class="reply-meta">
                        <span class="reply-author">{{ childReply.authorName }}</span>
                        <span class="reply-time">{{ formatDate(childReply.createTime) }}</span>
                        <div class="reply-actions">
                          <el-button type="text" @click="handleLikeReply(childReply)" :icon="'Star'">
                            {{ childReply.likeCount }} 赞
                          </el-button>
                        </div>
                      </div>
                    </template>
                  </el-list-item>
                </el-list>
              </el-collapse-item>
            </el-collapse>
          </template>
        </el-list-item>
      </el-list>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="replyList.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElForm } from 'element-plus'
import forumApi from '@/api/modules/forum'

const route = useRoute()
const router = useRouter()
const replyFormRef = ref(ElForm)
const isSubmitting = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// 主题ID
const topicId = route.params.topicId

// 主题详情
const topic = ref({
  topicId: '',
  topicTitle: '',
  topicContent: '',
  authorName: '',
  authorAvatar: '',
  createTime: '',
  likeCount: 0,
  favoriteCount: 0,
  viewCount: 0,
  replyCount: 0
})

// 点赞和收藏状态
const isLiked = ref(false)
const isFavorited = ref(false)

// 回复表单
const replyForm = reactive({
  replyContent: '',
  parentReplyId: null
})

// 回复表单验证规则
const replyRules = {
  replyContent: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 2, max: 500, message: '回复内容长度在 2 到 500 个字符', trigger: 'blur' }
  ]
}

// 回复列表
const replyList = ref([])

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 获取主题详情
const fetchTopicDetail = async () => {
  try {
    const response = await forumApi.forumTopic.getTopicById(topicId)
    if (response.success) {
      topic.value = response.data
      // 模拟获取点赞和收藏状态
      isLiked.value = Math.random() > 0.5
      isFavorited.value = Math.random() > 0.5
    } else {
      ElMessage.error('获取主题详情失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取主题详情失败：' + error.message)
    console.error('Error fetching topic detail:', error)
    // 使用模拟数据作为后备
    topic.value = {
      topicId: topicId,
      topicTitle: '腾讯2025校招开始啦！',
      topicContent: '腾讯2025年校园招聘现已全面启动，欢迎各位同学投递简历！我们提供富有竞争力的薪酬福利、良好的职业发展空间和优秀的团队氛围。',
      authorName: '腾讯招聘',
      authorAvatar: '',
      createTime: '2025-03-10 14:30:00',
      likeCount: 89,
      favoriteCount: 45,
      viewCount: 1234,
      replyCount: 56
    }
    isLiked.value = Math.random() > 0.5
    isFavorited.value = Math.random() > 0.5
  }
}

// 获取回复列表
const fetchReplyList = async () => {
  try {
    const response = await forumApi.forumReply.getReplyListByTopicId(topicId)
    if (response.success) {
      replyList.value = response.data || []
    } else {
      ElMessage.error('获取回复列表失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取回复列表失败：' + error.message)
    console.error('Error fetching reply list:', error)
    // 使用模拟数据作为后备
    replyList.value = [
      {
        replyId: 1,
        replyContent: '请问贵公司校招的技术岗位有哪些要求？',
        authorName: '求职者小王',
        createTime: '2025-03-10 15:00:00',
        likeCount: 12,
        childReplies: [
          {
            replyId: 2,
            replyContent: '主要要求计算机相关专业，掌握至少一种编程语言，有相关项目经验优先。',
            authorName: '腾讯招聘',
            createTime: '2025-03-10 15:30:00',
            likeCount: 8
          }
        ]
      },
      {
        replyId: 3,
        replyContent: '投递简历后多久能收到面试通知？',
        authorName: '求职者小李',
        createTime: '2025-03-10 16:00:00',
        likeCount: 5,
        childReplies: []
      }
    ]
  }
}

// 点赞主题
const handleLike = async () => {
  try {
    const response = await forumApi.forumTopic.updateTopicLikeCount(topicId, isLiked.value ? -1 : 1)
    if (response.success) {
      isLiked.value = !isLiked.value
      topic.value.likeCount += isLiked.value ? 1 : -1
      ElMessage.success(isLiked.value ? '点赞成功' : '取消点赞成功')
    } else {
      ElMessage.error('操作失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
    console.error('Error liking topic:', error)
  }
}

// 收藏主题
const handleFavorite = async () => {
  try {
    if (isFavorited.value) {
      // 取消收藏
      const response = await forumApi.forumFavorite.deleteFavoriteByTopicIdAndUserId(topicId, 1) // 假设用户ID为1
      if (response.success) {
        isFavorited.value = false
        topic.value.favoriteCount--
        ElMessage.success('取消收藏成功')
      } else {
        ElMessage.error('取消收藏失败：' + response.message)
      }
    } else {
      // 收藏
      const response = await forumApi.forumFavorite.createFavorite({
        topicId: topicId,
        userId: 1 // 假设用户ID为1
      })
      if (response.success) {
        isFavorited.value = true
        topic.value.favoriteCount++
        ElMessage.success('收藏成功')
      } else {
        ElMessage.error('收藏失败：' + response.message)
      }
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
    console.error('Error favoriting topic:', error)
  }
}

// 提交回复
const handleSubmitReply = async () => {
  try {
    await replyFormRef.value.validate()
    isSubmitting.value = true
    
    const response = await forumApi.forumReply.createReply({
      topicId: topicId,
      replyContent: replyForm.replyContent,
      parentReplyId: replyForm.parentReplyId
    })
    
    if (response.success) {
      ElMessage.success('回复发表成功')
      // 重置表单
      replyForm.replyContent = ''
      replyForm.parentReplyId = null
      // 重新获取回复列表
      fetchReplyList()
      // 更新主题回复数
      topic.value.replyCount++
    } else {
      ElMessage.error('回复发表失败：' + response.message)
    }
  } catch (error) {
    if (error.message) {
      ElMessage.error('表单验证失败：' + error.message)
    } else {
      ElMessage.error('回复发表失败：' + error.message)
      console.error('Error submitting reply:', error)
    }
  } finally {
    isSubmitting.value = false
  }
}

// 编辑主题
const handleEditTopic = () => {
  router.push(`/forum/topic/edit/${topicId}`)
}

// 删除主题
const handleDeleteTopic = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该主题吗？删除后将无法恢复。', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await forumApi.forumTopic.deleteTopic(topicId)
    if (response.success) {
      ElMessage.success('删除主题成功')
      router.push('/forum')
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

// 点赞回复
const handleLikeReply = async (reply) => {
  try {
    const response = await forumApi.forumReply.updateReplyLikeCount(reply.replyId, 1)
    if (response.success) {
      reply.likeCount++
      ElMessage.success('点赞成功')
    } else {
      ElMessage.error('点赞失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('点赞失败：' + error.message)
    console.error('Error liking reply:', error)
  }
}

// 回复回复
const handleReplyToReply = (reply) => {
  replyForm.parentReplyId = reply.replyId
  // 滚动到回复表单
  setTimeout(() => {
    document.querySelector('.reply-form-card').scrollIntoView({ behavior: 'smooth' })
  }, 100)
}

// 编辑回复
const handleEditReply = () => {
  ElMessage.info('编辑回复功能开发中...')
}

// 删除回复
const handleDeleteReply = async (reply) => {
  try {
    await ElMessageBox.confirm('确定要删除该回复吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await forumApi.forumReply.deleteReply(reply.replyId)
    if (response.success) {
      ElMessage.success('删除回复成功')
      // 从列表中移除该回复
      const index = replyList.value.findIndex(item => item.replyId === reply.replyId)
      if (index !== -1) {
        replyList.value.splice(index, 1)
      }
      // 更新主题回复数
      topic.value.replyCount--
    } else {
      ElMessage.error('删除回复失败：' + response.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除回复失败：' + (error.message || '操作取消'))
      console.error('Error deleting reply:', error)
    }
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
}

// 当前页码变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
}

// 页面挂载时获取数据
onMounted(() => {
  fetchTopicDetail()
  fetchReplyList()
})
</script>

<style scoped>
.forum-topic-detail-container {
  padding: 20px;
}

.topic-detail-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.topic-actions {
  display: flex;
  gap: 10px;
}

.topic-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  margin: 20px 0;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-details {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: bold;
  color: #303133;
}

.publish-time {
  font-size: 12px;
  color: #909399;
}

.topic-stats {
  display: flex;
  gap: 20px;
}

.topic-content {
  margin: 20px 0;
  color: #606266;
  line-height: 1.8;
}

.reply-form-card {
  margin-bottom: 20px;
}

.reply-form-card h3 {
  margin: 0;
  color: #303133;
}

.reply-form {
  margin-top: 20px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.reply-list-card {
  margin-bottom: 20px;
}

.reply-list-card h3 {
  margin: 0;
  color: #303133;
}

.reply-list {
  margin-top: 20px;
}

.reply-item {
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.reply-content {
  margin-bottom: 10px;
  color: #606266;
  line-height: 1.6;
}

.reply-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 12px;
}

.reply-author {
  font-weight: bold;
  color: #303133;
  margin-right: 10px;
}

.reply-time {
  color: #909399;
  margin-right: 10px;
}

.reply-actions {
  display: flex;
  gap: 10px;
}

.child-reply-list {
  margin-top: 10px;
  padding-left: 20px;
}

.child-reply-item {
  padding: 10px 0;
  border-bottom: none;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>