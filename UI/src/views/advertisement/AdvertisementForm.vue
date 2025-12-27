<template>
  <div class="advertisement-form-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑广告' : '创建广告' }}</span>
        </div>
      </template>
      
      <el-form :model="advertisementForm" label-position="top" class="advertisement-form" :rules="rules" ref="formRef">
        <el-form-item label="广告名称" prop="adName" required>
          <el-input v-model="advertisementForm.adName" placeholder="请输入广告名称" />
        </el-form-item>
        
        <el-form-item label="广告类型" prop="adType" required>
          <el-select v-model="advertisementForm.adType" placeholder="请选择广告类型">
            <el-option label="横幅广告" value="BANNER" />
            <el-option label="视频广告" value="VIDEO" />
            <el-option label="文字广告" value="TEXT" />
            <el-option label="图片广告" value="IMAGE" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="展示位置" prop="displayPosition" required>
          <el-select v-model="advertisementForm.displayPosition" placeholder="请选择展示位置">
            <el-option label="首页顶部" value="HOME_TOP" />
            <el-option label="首页中部" value="HOME_MIDDLE" />
            <el-option label="首页底部" value="HOME_BOTTOM" />
            <el-option label="详情页" value="DETAIL" />
            <el-option label="列表页" value="LIST" />
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime" required>
              <el-date-picker
                v-model="advertisementForm.startTime"
                type="datetime"
                placeholder="请选择开始时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime" required>
              <el-date-picker
                v-model="advertisementForm.endTime"
                type="datetime"
                placeholder="请选择结束时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="广告链接" prop="adUrl" required>
          <el-input v-model="advertisementForm.adUrl" placeholder="请输入广告链接" />
        </el-form-item>
        
        <el-form-item label="广告状态" prop="status" required>
          <el-select v-model="advertisementForm.status" placeholder="请选择广告状态">
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="广告描述">
          <el-input
            v-model="advertisementForm.description"
            type="textarea"
            :rows="5"
            placeholder="请输入广告描述"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="isSubmitting">提交</el-button>
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
import advertisementApi from '@/api/modules/advertisement'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)

// 是否为编辑模式
const isEdit = computed(() => !!route.params.adId)

// 是否正在提交
const isSubmitting = ref(false)

// 广告表单数据
const advertisementForm = reactive({
  adId: '',
  adName: '',
  adType: '',
  displayPosition: '',
  startTime: '',
  endTime: '',
  adUrl: '',
  status: '1',
  description: ''
})

// 表单验证规则
const rules = {
  adName: [
    { required: true, message: '请输入广告名称', trigger: 'blur' },
    { min: 1, max: 100, message: '广告名称长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  adType: [
    { required: true, message: '请选择广告类型', trigger: 'change' }
  ],
  displayPosition: [
    { required: true, message: '请选择展示位置', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  adUrl: [
    { required: true, message: '请输入广告链接', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择广告状态', trigger: 'change' }
  ]
}

// 页面挂载时获取广告数据（编辑模式）
onMounted(() => {
  if (isEdit.value) {
    fetchAdvertisementDetail()
  }
})

// 获取广告详情（编辑模式）
const fetchAdvertisementDetail = async () => {
  try {
    const response = await advertisementApi.getAdvertisementInfo(route.params.adId)
    if (response.success) {
      Object.assign(advertisementForm, response.data)
    } else {
      ElMessage.error('获取广告详情失败：' + response.message)
      router.push('/advertisement/list')
    }
  } catch (error) {
    ElMessage.error('获取广告详情失败：' + error.message)
    console.error('Error fetching advertisement detail:', error)
    router.push('/advertisement/list')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    isSubmitting.value = true
    
    let response
    if (isEdit.value) {
      response = await advertisementApi.updateAdvertisement(advertisementForm)
    } else {
      response = await advertisementApi.createAdvertisement(advertisementForm)
    }
    
    if (response.success) {
      ElMessage.success(isEdit.value ? '编辑广告成功' : '创建广告成功')
      router.push('/advertisement/list')
    } else {
      ElMessage.error((isEdit.value ? '编辑广告失败' : '创建广告失败') + ': ' + response.message)
    }
  } catch (error) {
    if (error.name === 'Error') {
      // 自定义错误
      ElMessage.error(isEdit.value ? '编辑广告失败' : '创建广告失败' + ': ' + error.message)
    } else {
      // 表单验证错误
      console.error('Form validation error:', error)
    }
  } finally {
    isSubmitting.value = false
  }
}

// 取消
const handleCancel = () => {
  router.push('/advertisement/list')
}
</script>

<style scoped>
.advertisement-form-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.advertisement-form {
  max-width: 600px;
  margin: 0 auto;
}
</style>