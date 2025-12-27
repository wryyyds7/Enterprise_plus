<template>
  <div class="activity-form-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑活动' : '创建活动' }}</span>
        </div>
      </template>
      
      <el-form :model="activityForm" label-position="top" class="activity-form">
        <el-form-item label="活动名称" required>
          <el-input v-model="activityForm.activityName" placeholder="请输入活动名称" />
        </el-form-item>
        
        <el-form-item label="活动类型" required>
          <el-select v-model="activityForm.activityType" placeholder="请选择活动类型">
            <el-option label="招聘会" value="招聘会" />
            <el-option label="宣讲会" value="宣讲会" />
            <el-option label="培训" value="培训" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" required>
              <el-date-picker
                v-model="activityForm.startTime"
                type="datetime"
                placeholder="请选择开始时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" required>
              <el-date-picker
                v-model="activityForm.endTime"
                type="datetime"
                placeholder="请选择结束时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="活动地点" required>
          <el-input v-model="activityForm.location" placeholder="请输入活动地点" />
        </el-form-item>
        
        <el-form-item label="活动状态" required>
          <el-select v-model="activityForm.status" placeholder="请选择活动状态">
            <el-option label="进行中" value="1" />
            <el-option label="已结束" value="0" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="活动描述">
          <el-input
            v-model="activityForm.description"
            type="textarea"
            :rows="5"
            placeholder="请输入活动描述"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import activityApi from '@/api/modules/activity'

const router = useRouter()
const route = useRoute()

// 是否为编辑模式
const isEdit = computed(() => !!route.params.activityId)

// 活动表单数据
const activityForm = reactive({
  activityId: '',
  activityName: '',
  activityType: '',
  startTime: '',
  endTime: '',
  location: '',
  status: '1',
  description: ''
})

// 页面挂载时获取活动数据（编辑模式）
onMounted(() => {
  if (isEdit.value) {
    fetchActivityDetail()
  }
})

// 获取活动详情（编辑模式）
const fetchActivityDetail = async () => {
  try {
    const response = await activityApi.getActivityInfo(route.params.activityId)
    if (response.success) {
      Object.assign(activityForm, response.data)
    } else {
      ElMessage.error('获取活动详情失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取活动详情失败：' + error.message)
    console.error('Error fetching activity detail:', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    let response
    if (isEdit.value) {
      response = await activityApi.updateActivity(activityForm)
    } else {
      response = await activityApi.createActivity(activityForm)
    }
    
    if (response.success) {
      ElMessage.success(isEdit.value ? '编辑活动成功' : '创建活动成功')
      router.push('/activity/list')
    } else {
      ElMessage.error((isEdit.value ? '编辑活动失败' : '创建活动失败') + ': ' + response.message)
    }
  } catch (error) {
    ElMessage.error((isEdit.value ? '编辑活动失败' : '创建活动失败') + ': ' + error.message)
    console.error('Error submitting activity form:', error)
  }
}

// 取消
const handleCancel = () => {
  router.push('/activity/list')
}
</script>

<style scoped>
.activity-form-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-form {
  max-width: 600px;
  margin: 0 auto;
}
</style>