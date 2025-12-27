<template>
  <div class="forum-topic-form-container">
    <el-card shadow="hover" class="topic-form-card">
      <template #header>
        <div class="card-header">
          <h2>{{ isEdit ? '编辑主题' : '创建主题' }}</h2>
        </div>
      </template>
      
      <el-form
        ref="topicFormRef"
        :model="topicForm"
        :rules="formRules"
        label-position="top"
        class="topic-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主题标题" prop="topicTitle">
              <el-input
                v-model="topicForm.topicTitle"
                placeholder="请输入主题标题"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item label="所属板块" prop="sectionId">
              <el-select
                v-model="topicForm.sectionId"
                placeholder="请选择所属板块"
                style="width: 100%"
              >
                <el-option
                  v-for="section in sectionList"
                  :key="section.sectionId"
                  :label="section.sectionName"
                  :value="section.sectionId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="主题标签" prop="tags">
              <el-select
                v-model="topicForm.tags"
                placeholder="请选择主题标签（可选）"
                style="width: 100%"
                multiple
              >
                <el-option label="技术" value="技术" />
                <el-option label="经验分享" value="经验分享" />
                <el-option label="招聘信息" value="招聘信息" />
                <el-option label="面试技巧" value="面试技巧" />
                <el-option label="职场交流" value="职场交流" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="主题内容" prop="topicContent">
          <el-input
            v-model="topicForm.topicContent"
            type="textarea"
            placeholder="请输入主题内容"
            rows="12"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        
        <div class="form-actions">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="isSubmitting">
            {{ isEdit ? '保存修改' : '发布主题' }}
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElForm } from 'element-plus'
import forumApi from '@/api/modules/forum'

const route = useRoute()
const router = useRouter()
const topicFormRef = ref(ElForm)
const isSubmitting = ref(false)

// 判断是否为编辑模式
const isEdit = computed(() => {
  return !!route.params.topicId
})

// 主题表单数据
const topicForm = reactive({
  topicId: '',
  topicTitle: '',
  topicContent: '',
  sectionId: '',
  tags: []
})

// 板块列表
const sectionList = ref([])

// 表单验证规则
const formRules = {
  topicTitle: [
    { required: true, message: '请输入主题标题', trigger: 'blur' },
    { min: 5, max: 100, message: '主题标题长度在 5 到 100 个字符', trigger: 'blur' }
  ],
  topicContent: [
    { required: true, message: '请输入主题内容', trigger: 'blur' },
    { min: 10, max: 2000, message: '主题内容长度在 10 到 2000 个字符', trigger: 'blur' }
  ],
  sectionId: [
    { required: true, message: '请选择所属板块', trigger: 'change' }
  ]
}

// 获取板块列表
const fetchSectionList = async () => {
  try {
    const response = await forumApi.forumSection.getSectionList()
    if (response.success) {
      sectionList.value = response.data || []
    } else {
      ElMessage.error('获取板块列表失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取板块列表失败：' + error.message)
    console.error('Error fetching section list:', error)
    // 使用模拟数据作为后备
    sectionList.value = [
      { sectionId: 1, sectionName: '招聘信息' },
      { sectionId: 2, sectionName: '求职经验' },
      { sectionId: 3, sectionName: '面试技巧' },
      { sectionId: 4, sectionName: '职场交流' }
    ]
  }
}

// 获取主题详情（编辑模式）
const fetchTopicDetail = async () => {
  try {
    const topicId = route.params.topicId
    const response = await forumApi.forumTopic.getTopicById(topicId)
    if (response.success) {
      Object.assign(topicForm, response.data)
      // 处理标签字段
      if (topicForm.tags && typeof topicForm.tags === 'string') {
        topicForm.tags = topicForm.tags.split(',')
      }
    } else {
      ElMessage.error('获取主题详情失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取主题详情失败：' + error.message)
    console.error('Error fetching topic detail:', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await topicFormRef.value.validate()
    isSubmitting.value = true
    
    // 处理标签字段
    const formData = { ...topicForm }
    if (Array.isArray(formData.tags)) {
      formData.tags = formData.tags.join(',')
    }
    
    if (isEdit.value) {
      // 编辑主题
      const response = await forumApi.forumTopic.updateTopic(formData)
      if (response.success) {
        ElMessage.success('主题更新成功')
        router.push(`/forum/topic/detail/${formData.topicId}`)
      } else {
        ElMessage.error('主题更新失败：' + response.message)
      }
    } else {
      // 创建主题
      const response = await forumApi.forumTopic.createTopic(formData)
      if (response.success) {
        ElMessage.success('主题创建成功')
        router.push(`/forum/topic/detail/${response.data.topicId}`)
      } else {
        ElMessage.error('主题创建失败：' + response.message)
      }
    }
  } catch (error) {
    if (error.message) {
      ElMessage.error('表单验证失败：' + error.message)
    } else {
      ElMessage.error('提交失败：' + error.message)
      console.error('Error submitting form:', error)
    }
  } finally {
    isSubmitting.value = false
  }
}

// 取消
const handleCancel = () => {
  if (isEdit.value) {
    router.push(`/forum/topic/detail/${topicForm.topicId}`)
  } else if (topicForm.sectionId) {
    router.push(`/forum/section/detail/${topicForm.sectionId}`)
  } else {
    router.push('/forum')
  }
}

// 监听路由参数中的 sectionId
watch(() => route.query.sectionId, (newSectionId) => {
  if (newSectionId) {
    topicForm.sectionId = newSectionId
  }
}, { immediate: true })

// 页面挂载时获取数据
onMounted(() => {
  fetchSectionList()
  if (isEdit.value) {
    fetchTopicDetail()
  }
})
</script>

<style scoped>
.forum-topic-form-container {
  padding: 20px;
}

.topic-form-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.topic-form {
  margin-top: 20px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
</style>