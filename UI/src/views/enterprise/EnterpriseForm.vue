<template>
  <div class="enterprise-form-container">
    <el-card shadow="hover" class="form-card">
      <template #header>
        <div class="card-header">
          <el-button type="primary" @click="handleBack">返回列表</el-button>
          <span style="margin-left: 20px;">{{ formTitle }}</span>
        </div>
      </template>

      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="120px"
        class="enterprise-form"
      >
        <el-card shadow="hover" class="form-section">
          <template #header>
            <div class="card-header">基本信息</div>
          </template>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="企业名称" prop="enterpriseName">
                <el-input v-model="form.enterpriseName" placeholder="请输入企业名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属行业" prop="industry">
                <el-select v-model="form.industry" placeholder="请选择行业">
                  <el-option label="互联网" value="互联网" />
                  <el-option label="金融" value="金融" />
                  <el-option label="教育" value="教育" />
                  <el-option label="医疗" value="医疗" />
                  <el-option label="制造业" value="制造业" />
                  <el-option label="科技" value="科技" />
                  <el-option label="零售" value="零售" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="企业规模" prop="enterpriseScale">
                <el-select v-model="form.enterpriseScale" placeholder="请选择企业规模">
                  <el-option label="100人以下" value="small" />
                  <el-option label="100-500人" value="medium" />
                  <el-option label="500-1000人" value="large" />
                  <el-option label="1000人以上" value="huge" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所在地" prop="location">
                <el-input v-model="form.location" placeholder="请输入所在地" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="成立时间" prop="establishmentDate">
                <el-date-picker
                  v-model="form.establishmentDate"
                  type="date"
                  placeholder="请选择成立时间"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="营业执照号" prop="businessLicense">
                <el-input v-model="form.businessLicense" placeholder="请输入营业执照号" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="企业网址" prop="website">
                <el-input v-model="form.website" placeholder="请输入企业网址" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态" prop="status">
                <el-switch
                  v-model="form.status"
                  active-value="1"
                  inactive-value="0"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <el-card shadow="hover" class="form-section" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">联系方式</div>
          </template>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系人" prop="contactPerson">
                <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系邮箱" prop="contactEmail">
                <el-input v-model="form.contactEmail" placeholder="请输入联系邮箱" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <el-card shadow="hover" class="form-section" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">企业介绍</div>
          </template>

          <el-form-item label="企业简介" prop="introduction">
            <el-input
              v-model="form.introduction"
              type="textarea"
              rows="4"
              placeholder="请输入企业简介"
            />
          </el-form-item>

          <el-form-item label="企业文化" prop="culture">
            <el-input
              v-model="form.culture"
              type="textarea"
              rows="4"
              placeholder="请输入企业文化"
            />
          </el-form-item>
        </el-card>

        <el-card shadow="hover" class="form-section" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">校招信息</div>
          </template>

          <el-form-item label="校招政策" prop="recruitmentPolicy">
            <el-input
              v-model="form.recruitmentPolicy"
              type="textarea"
              rows="4"
              placeholder="请输入校招政策"
            />
          </el-form-item>

          <el-form-item label="薪酬福利">
            <el-select
              v-model="form.welfareList"
              multiple
              placeholder="请选择薪酬福利"
              collapse-tags
            >
              <el-option label="六险一金" value="六险一金" />
              <el-option label="带薪年假" value="带薪年假" />
              <el-option label="年度体检" value="年度体检" />
              <el-option label="员工食堂" value="员工食堂" />
              <el-option label="交通补贴" value="交通补贴" />
              <el-option label="住房补贴" value="住房补贴" />
              <el-option label="节日福利" value="节日福利" />
              <el-option label="团队建设" value="团队建设" />
              <el-option label="年终奖金" value="年终奖金" />
              <el-option label="股票期权" value="股票期权" />
            </el-select>
          </el-form-item>
        </el-card>

        <div class="form-actions">
          <el-button @click="handleBack">取消</el-button>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 表单引用
const formRef = ref()

// 根据路由判断是新增还是编辑
const isEdit = computed(() => {
  return route.path.includes('edit')
})

// 表单标题
const formTitle = computed(() => {
  return isEdit.value ? '编辑企业' : '新增企业'
})

// 表单数据
const form = reactive({
  enterpriseId: undefined,
  enterpriseName: '',
  industry: '',
  enterpriseScale: '',
  location: '',
  establishmentDate: '',
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  website: '',
  businessLicense: '',
  status: '1',
  introduction: '',
  culture: '',
  recruitmentPolicy: '',
  welfareList: []
})

// 表单验证规则
const rules = {
  enterpriseName: [
    { required: true, message: '请输入企业名称', trigger: 'blur' },
    { min: 2, max: 50, message: '企业名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  industry: [
    { required: true, message: '请选择所属行业', trigger: 'change' }
  ],
  enterpriseScale: [
    { required: true, message: '请选择企业规模', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入所在地', trigger: 'blur' }
  ],
  establishmentDate: [
    { required: true, message: '请选择成立时间', trigger: 'change' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  contactEmail: [
    { required: true, message: '请输入联系邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  businessLicense: [
    { required: true, message: '请输入营业执照号', trigger: 'blur' }
  ],
  introduction: [
    { required: true, message: '请输入企业简介', trigger: 'blur' },
    { min: 10, message: '企业简介长度不能少于 10 个字符', trigger: 'blur' }
  ]
}

// 返回列表
const handleBack = () => {
  router.push('/enterprise/list')
}

// 提交表单
const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 这里应该调用API提交表单
      ElMessage.success(`${isEdit.value ? '编辑' : '新增'}企业成功`)
      router.push('/enterprise/list')
    } else {
      return false
    }
  })
}
</script>

<style scoped>
.enterprise-form-container {
  padding: 20px;
}

.form-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
}

.enterprise-form {
  padding: 20px 0;
}

.form-section {
  margin-bottom: 20px;
}

.form-actions {
  margin-top: 30px;
  text-align: center;
}

.form-actions .el-button {
  margin-right: 20px;
}
</style>
