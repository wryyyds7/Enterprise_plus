<template>
  <div class="forum-section-form-container">
    <el-card shadow="hover" class="section-form-card">
      <template #header>
        <div class="card-header">
          <h2>{{ isEdit ? '编辑板块' : '创建板块' }}</h2>
        </div>
      </template>
      
      <el-form
        ref="sectionFormRef"
        :model="sectionForm"
        :rules="formRules"
        label-position="top"
        class="section-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="板块名称" prop="sectionName">
              <el-input
                v-model="sectionForm.sectionName"
                placeholder="请输入板块名称"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item label="父板块" prop="parentSectionId">
              <el-select
                v-model="sectionForm.parentSectionId"
                placeholder="请选择父板块（可选）"
                style="width: 100%"
              >
                <el-option label="无父板块" value="" />
                <el-option
                  v-for="section in parentSectionList"
                  :key="section.sectionId"
                  :label="section.sectionName"
                  :value="section.sectionId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="板块描述" prop="description">
              <el-input
                v-model="sectionForm.description"
                type="textarea"
                placeholder="请输入板块描述"
                rows="4"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <div class="form-actions">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="isSubmitting">
            {{ isEdit ? '保存修改' : '创建板块' }}
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElForm } from 'element-plus'
import forumApi from '@/api/modules/forum'

const route = useRoute()
const router = useRouter()
const sectionFormRef = ref(ElForm)
const isSubmitting = ref(false)

// 判断是否为编辑模式
const isEdit = computed(() => {
  return !!route.params.sectionId
})

// 板块表单数据
const sectionForm = reactive({
  sectionId: '',
  sectionName: '',
  description: '',
  parentSectionId: ''
})

// 父板块列表
const parentSectionList = ref([])

// 表单验证规则
const formRules = {
  sectionName: [
    { required: true, message: '请输入板块名称', trigger: 'blur' },
    { min: 2, max: 50, message: '板块名称长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 获取父板块列表
const fetchParentSectionList = async () => {
  try {
    const response = await forumApi.forumSection.getSectionList()
    if (response.success) {
      // 过滤掉当前板块（编辑模式下）
      parentSectionList.value = response.data.filter(section => {
        return !isEdit.value || section.sectionId !== parseInt(route.params.sectionId)
      })
    } else {
      ElMessage.error('获取父板块列表失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取父板块列表失败：' + error.message)
    console.error('Error fetching parent section list:', error)
    // 使用模拟数据作为后备
    parentSectionList.value = [
      { sectionId: 1, sectionName: '招聘信息' },
      { sectionId: 2, sectionName: '求职经验' },
      { sectionId: 3, sectionName: '面试技巧' }
    ]
  }
}

// 获取板块详情（编辑模式）
const fetchSectionDetail = async () => {
  try {
    const sectionId = route.params.sectionId
    const response = await forumApi.forumSection.getSectionById(sectionId)
    if (response.success) {
      Object.assign(sectionForm, response.data)
    } else {
      ElMessage.error('获取板块详情失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取板块详情失败：' + error.message)
    console.error('Error fetching section detail:', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await sectionFormRef.value.validate()
    isSubmitting.value = true
    
    if (isEdit.value) {
      // 编辑板块
      const response = await forumApi.forumSection.updateSection(sectionForm)
      if (response.success) {
        ElMessage.success('板块更新成功')
        router.push(`/forum/section/detail/${sectionForm.sectionId}`)
      } else {
        ElMessage.error('板块更新失败：' + response.message)
      }
    } else {
      // 创建板块
      const response = await forumApi.forumSection.createSection(sectionForm)
      if (response.success) {
        ElMessage.success('板块创建成功')
        router.push('/forum')
      } else {
        ElMessage.error('板块创建失败：' + response.message)
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
    router.push(`/forum/section/detail/${sectionForm.sectionId}`)
  } else {
    router.push('/forum')
  }
}

// 页面挂载时获取数据
onMounted(() => {
  fetchParentSectionList()
  if (isEdit.value) {
    fetchSectionDetail()
  }
})
</script>

<style scoped>
.forum-section-form-container {
  padding: 20px;
}

.section-form-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.section-form {
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