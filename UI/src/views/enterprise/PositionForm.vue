<template>
  <div class="position-form-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>{{ isEdit ? '编辑职位' : '新增职位' }}</h2>
          <el-button type="primary" @click="handleBack">返回</el-button>
        </div>
      </template>

      <el-form ref="positionFormRef" :model="positionForm" :rules="rules" label-width="120px" class="position-form">
        <el-form-item label="企业ID" prop="enterpriseId">
          <el-input v-model="positionForm.enterpriseId" placeholder="请输入企业ID" />
        </el-form-item>

        <el-form-item label="职位名称" prop="positionName">
          <el-input v-model="positionForm.positionName" placeholder="请输入职位名称" />
        </el-form-item>

        <el-form-item label="职位类别" prop="positionType">
          <el-select v-model="positionForm.positionType" placeholder="请选择职位类别">
            <el-option label="技术岗" value="技术岗" />
            <el-option label="管理岗" value="管理岗" />
            <el-option label="销售岗" value="销售岗" />
            <el-option label="运营岗" value="运营岗" />
            <el-option label="市场岗" value="市场岗" />
          </el-select>
        </el-form-item>

        <el-form-item label="工作地点" prop="workLocation">
          <el-input v-model="positionForm.workLocation" placeholder="请输入工作地点" />
        </el-form-item>

        <el-form-item label="薪资范围" prop="salaryRange">
          <el-input v-model="positionForm.salaryRange" placeholder="请输入薪资范围（如：10-15K）" />
        </el-form-item>

        <el-form-item label="学历要求" prop="education">
          <el-select v-model="positionForm.education" placeholder="请选择学历要求">
            <el-option label="大专" value="大专" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
          </el-select>
        </el-form-item>

        <el-form-item label="工作经验" prop="workExperience">
          <el-select v-model="positionForm.workExperience" placeholder="请选择工作经验要求">
            <el-option label="不限" value="不限" />
            <el-option label="1年以内" value="1年以内" />
            <el-option label="1-3年" value="1-3年" />
            <el-option label="3-5年" value="3-5年" />
            <el-option label="5年以上" value="5年以上" />
          </el-select>
        </el-form-item>

        <el-form-item label="职位描述" prop="positionDescription">
          <el-input 
            v-model="positionForm.positionDescription" 
            type="textarea" 
            rows="4" 
            placeholder="请输入职位描述" 
          />
        </el-form-item>

        <el-form-item label="任职要求" prop="requirements">
          <el-input 
            v-model="positionForm.requirements" 
            type="textarea" 
            rows="4" 
            placeholder="请输入任职要求" 
          />
        </el-form-item>

        <el-form-item label="发布状态" prop="status">
          <el-switch v-model="positionForm.status" active-value="1" inactive-value="0" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import enterpriseApi from '@/api/modules/enterprise'

const router = useRouter()
const route = useRoute()
const positionFormRef = ref(null)
const loading = ref(false)

// 判断是否为编辑模式
const isEdit = computed(() => !!route.params.positionId)

// 职位表单数据
const positionForm = reactive({
  positionId: undefined,
  enterpriseId: '',
  positionName: '',
  positionType: '',
  workLocation: '',
  salaryRange: '',
  education: '',
  workExperience: '',
  positionDescription: '',
  requirements: '',
  status: '1' // 默认发布状态
})

// 表单验证规则
const rules = {
  enterpriseId: [
    { required: true, message: '请输入企业ID', trigger: 'blur' }
  ],
  positionName: [
    { required: true, message: '请输入职位名称', trigger: 'blur' },
    { min: 2, max: 50, message: '职位名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  positionType: [
    { required: true, message: '请选择职位类别', trigger: 'change' }
  ],
  workLocation: [
    { required: true, message: '请输入工作地点', trigger: 'blur' }
  ],
  salaryRange: [
    { required: true, message: '请输入薪资范围', trigger: 'blur' }
  ],
  education: [
    { required: true, message: '请选择学历要求', trigger: 'change' }
  ],
  workExperience: [
    { required: true, message: '请选择工作经验要求', trigger: 'change' }
  ],
  positionDescription: [
    { required: true, message: '请输入职位描述', trigger: 'blur' },
    { min: 10, message: '职位描述长度不能少于10个字符', trigger: 'blur' }
  ],
  requirements: [
    { required: true, message: '请输入任职要求', trigger: 'blur' },
    { min: 10, message: '任职要求长度不能少于10个字符', trigger: 'blur' }
  ]
}

// 获取职位详情（编辑模式下）
const getPositionDetail = async () => {
  if (!isEdit.value) return

  try {
    loading.value = true
    const response = await enterpriseApi.getPositionInfo(route.params.positionId)
    if (response && response.data) {
      Object.assign(positionForm, response.data)
    }
  } catch (error) {
    ElMessage.error('获取职位详情失败：' + (error.message || '未知错误'))
    console.error('获取职位详情失败：', error)
    router.push('/enterprise/position/list')
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!positionFormRef.value) return

  await positionFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        let response
        if (isEdit.value) {
          // 编辑模式：调用更新接口
          response = await enterpriseApi.updatePosition(positionForm)
        } else {
          // 新增模式：调用新增接口
          response = await enterpriseApi.createPosition(positionForm)
        }

        if (response && response.success) {
          ElMessage.success(isEdit.value ? '职位更新成功' : '职位新增成功')
          router.push('/enterprise/position/list')
        } else {
          ElMessage.error((response && response.message) || (isEdit.value ? '职位更新失败' : '职位新增失败'))
        }
      } catch (error) {
        ElMessage.error(isEdit.value ? '职位更新失败' : '职位新增失败')
        console.error('职位提交失败：', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 重置表单
const handleReset = () => {
  if (positionFormRef.value) {
    positionFormRef.value.resetFields()
  }
  // 如果是编辑模式，重新加载数据
  if (isEdit.value) {
    getPositionDetail()
  }
}

// 返回上一页
const handleBack = () => {
  router.push('/enterprise/position/list')
}

onMounted(() => {
  // 编辑模式下获取职位详情
  if (isEdit.value) {
    getPositionDetail()
  }
})
</script>

<style scoped>
.position-form-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.position-form {
  margin-top: 20px;
  max-width: 800px;
}
</style>