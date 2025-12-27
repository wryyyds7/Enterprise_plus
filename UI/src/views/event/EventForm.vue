<template>
  <div class="event-form-container">
    <el-card shadow="hover" class="event-form-card">
      <template #header>
        <div class="card-header">
          <h2>{{ isEdit ? '编辑事件' : '新增事件' }}</h2>
        </div>
      </template>
      
      <el-form
        ref="eventFormRef"
        :model="eventForm"
        :rules="formRules"
        label-position="top"
        class="event-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="事件名称" prop="eventName">
              <el-input
                v-model="eventForm.eventName"
                placeholder="请输入事件名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item label="事件类型" prop="eventType">
              <el-select
                v-model="eventForm.eventType"
                placeholder="请选择事件类型"
                style="width: 100%"
              >
                <el-option label="招聘" value="RECRUITMENT" />
                <el-option label="宣讲会" value="LECTURE" />
                <el-option label="招聘会" value="JOB_FAIR" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="展示位置" prop="displayPosition">
              <el-select
                v-model="eventForm.displayPosition"
                placeholder="请选择展示位置"
                style="width: 100%"
              >
                <el-option label="首页顶部" value="HOME_TOP" />
                <el-option label="首页中部" value="HOME_MIDDLE" />
                <el-option label="首页底部" value="HOME_BOTTOM" />
                <el-option label="活动页面" value="ACTIVITY_PAGE" />
                <el-option label="论坛页面" value="FORUM_PAGE" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="状态" prop="status">
              <el-switch
                v-model="eventForm.status"
                active-value="1"
                inactive-value="0"
                active-text="进行中"
                inactive-text="已结束"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="eventForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
            
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="eventForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="事件内容" prop="content">
          <el-input
            v-model="eventForm.content"
            type="textarea"
            placeholder="请输入事件内容"
            rows="10"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        
        <div class="form-actions">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="isSubmitting">
            {{ isEdit ? '保存修改' : '创建事件' }}
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
import eventApi from '@/api/modules/event'

const route = useRoute()
const router = useRouter()
const eventFormRef = ref(ElForm)
const isSubmitting = ref(false)

// 判断是否为编辑模式
const isEdit = computed(() => {
  return !!route.params.eventId
})

// 事件表单数据
const eventForm = reactive({
  eventId: '',
  eventName: '',
  eventType: '',
  displayPosition: '',
  startTime: '',
  endTime: '',
  content: '',
  status: '1' // 默认状态为进行中
})

// 表单验证规则
const formRules = {
  eventName: [
    { required: true, message: '请输入事件名称', trigger: 'blur' },
    { min: 2, max: 100, message: '事件名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  eventType: [
    { required: true, message: '请选择事件类型', trigger: 'change' }
  ],
  displayPosition: [
    { required: true, message: '请选择展示位置', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (eventForm.startTime && value && new Date(value) < new Date(eventForm.startTime)) {
          callback(new Error('结束时间不能早于开始时间'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  content: [
    { required: true, message: '请输入事件内容', trigger: 'blur' },
    { min: 10, max: 2000, message: '事件内容长度在 10 到 2000 个字符', trigger: 'blur' }
  ]
}

// 获取事件详情（编辑模式）
const fetchEventDetail = async () => {
  try {
    const eventId = route.params.eventId
    const response = await eventApi.getEventById(eventId)
    if (response.success) {
      Object.assign(eventForm, response.data)
    } else {
      ElMessage.error('获取事件详情失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取事件详情失败：' + error.message)
    console.error('Error fetching event detail:', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await eventFormRef.value.validate()
    isSubmitting.value = true
    
    if (isEdit.value) {
      // 编辑事件
      const response = await eventApi.updateEvent(eventForm)
      if (response.success) {
        ElMessage.success('事件更新成功')
        router.push(`/event/detail/${eventForm.eventId}`)
      } else {
        ElMessage.error('事件更新失败：' + response.message)
      }
    } else {
      // 创建事件
      const response = await eventApi.createEvent(eventForm)
      if (response.success) {
        ElMessage.success('事件创建成功')
        router.push('/event/list')
      } else {
        ElMessage.error('事件创建失败：' + response.message)
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
    router.push(`/event/detail/${eventForm.eventId}`)
  } else {
    router.push('/event/list')
  }
}

// 页面挂载时，如果是编辑模式则获取事件详情
onMounted(() => {
  if (isEdit.value) {
    fetchEventDetail()
  }
})
</script>

<style scoped>
.event-form-container {
  padding: 20px;
}

.event-form-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.event-form {
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