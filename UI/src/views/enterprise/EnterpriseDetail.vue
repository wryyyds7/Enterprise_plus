<template>
  <div class="enterprise-detail-container">
    <el-card shadow="hover" class="detail-card">
      <template #header>
        <div class="card-header">
          <el-button type="primary" @click="handleBack">返回列表</el-button>
          <span style="margin-left: 20px;">企业详情</span>
          <div style="margin-left: auto;">
            <el-button @click="handleEdit">编辑</el-button>
            <el-button type="danger" @click="handleDelete">删除</el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="企业ID">{{ enterpriseInfo.enterpriseId }}</el-descriptions-item>
            <el-descriptions-item label="企业名称">{{ enterpriseInfo.enterpriseName }}</el-descriptions-item>
            <el-descriptions-item label="所属行业">{{ enterpriseInfo.industry }}</el-descriptions-item>
            <el-descriptions-item label="企业规模">
              <el-tag :type="getScaleType(enterpriseInfo.enterpriseScale)">
                {{ getScaleLabel(enterpriseInfo.enterpriseScale) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="所在地">{{ enterpriseInfo.location }}</el-descriptions-item>
            <el-descriptions-item label="成立时间">{{ enterpriseInfo.establishmentDate }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ enterpriseInfo.contactPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ enterpriseInfo.contactPhone }}</el-descriptions-item>
            <el-descriptions-item label="联系邮箱">{{ enterpriseInfo.contactEmail }}</el-descriptions-item>
            <el-descriptions-item label="企业网址">{{ enterpriseInfo.website }}</el-descriptions-item>
            <el-descriptions-item label="营业执照号">{{ enterpriseInfo.businessLicense }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="enterpriseInfo.status === '1' ? 'success' : 'danger'">
                {{ enterpriseInfo.status === '1' ? '启用' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <el-tab-pane label="企业介绍" name="introduction">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>企业简介</span>
              </div>
            </template>
            <div class="introduction-content">{{ enterpriseInfo.introduction }}</div>
          </el-card>

          <el-card shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>企业文化</span>
              </div>
            </template>
            <div class="culture-content">{{ enterpriseInfo.culture }}</div>
          </el-card>

          <el-card shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>发展历程</span>
              </div>
            </template>
            <div class="history-content">
              <el-timeline>
                <el-timeline-item
                  v-for="(item, index) in enterpriseInfo.developmentHistory"
                  :key="index"
                  :timestamp="item.year"
                >
                  {{ item.content }}
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-card>
        </el-tab-pane>

        <el-tab-pane label="招聘信息" name="recruitment">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>校招政策</span>
              </div>
            </template>
            <div class="policy-content">{{ enterpriseInfo.recruitmentPolicy }}</div>
          </el-card>

          <el-card shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>薪酬福利</span>
              </div>
            </template>
            <div class="welfare-content">
              <el-tag v-for="(item, index) in enterpriseInfo.welfareList" :key="index" style="margin-right: 10px; margin-bottom: 10px;">
                {{ item }}
              </el-tag>
            </div>
          </el-card>

          <el-card shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>招聘职位</span>
                <el-button type="primary" size="small" @click="handleAddPosition">新增职位</el-button>
              </div>
            </template>
            <el-table :data="positionList" style="width: 100%" border stripe>
              <el-table-column prop="positionId" label="职位ID" width="100" />
              <el-table-column prop="positionName" label="职位名称" />
              <el-table-column prop="department" label="部门" width="150" />
              <el-table-column prop="salaryRange" label="薪资范围" width="150" />
              <el-table-column prop="workLocation" label="工作地点" width="150" />
              <el-table-column prop="education" label="学历要求" width="120" />
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="scope">
                  <el-button type="info" size="small" @click="handlePositionDetail(scope.row)">
                    详情
                  </el-button>
                  <el-button type="primary" size="small" @click="handleEditPosition(scope.row)">
                    编辑
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import enterpriseApi from '@/api/modules/enterprise'

const router = useRouter()
const route = useRoute()

// 当前激活的标签页
const activeTab = ref('basic')

// 企业信息
const enterpriseInfo = reactive({
  enterpriseId: '',
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
  status: '',
  introduction: '',
  culture: '',
  developmentHistory: [],
  recruitmentPolicy: '',
  welfareList: []
})

// 模拟企业信息数据（作为后备）
const mockEnterpriseInfo = {
  enterpriseId: 1,
  enterpriseName: '腾讯科技有限公司',
  industry: '互联网',
  enterpriseScale: 'huge',
  location: '深圳市',
  establishmentDate: '1998-11-11',
  contactPerson: '张先生',
  contactPhone: '0755-12345678',
  contactEmail: 'contact@tencent.com',
  website: 'https://www.tencent.com',
  businessLicense: '123456789012345678',
  status: '1',
  introduction: '腾讯科技有限公司成立于1998年11月11日，是中国最大的互联网综合服务提供商之一，也是中国服务用户最多的互联网企业之一。',
  culture: '腾讯的使命是"通过互联网服务提升人类生活品质"。腾讯以"正直、进取、合作、创新"为价值观，致力于成为最受尊敬的互联网企业。',
  developmentHistory: [
    { year: '1998年', content: '腾讯公司成立' },
    { year: '1999年', content: '推出即时通讯软件QQ' },
    { year: '2004年', content: '在香港联交所主板上市' },
    { year: '2011年', content: '推出微信' },
    { year: '2018年', content: '成为全球市值最高的科技公司之一' }
  ],
  recruitmentPolicy: '腾讯校园招聘面向全国高校应届毕业生，提供技术、产品、设计、市场、职能等各类职位。我们注重人才的潜力和发展能力，提供完善的培训体系和晋升通道。',
  welfareList: ['六险一金', '带薪年假', '年度体检', '员工食堂', '交通补贴', '住房补贴', '节日福利', '团队建设']
}

// 获取企业详情
const getEnterpriseDetail = async () => {
  const enterpriseId = route.params.enterpriseId
  if (!enterpriseId) {
    ElMessage.error('企业ID不能为空')
    return
  }
  
  try {
    const response = await enterpriseApi.getEnterpriseInfo(enterpriseId)
    if (response.code === 200 && response.data) {
      // 将API返回的数据赋值给enterpriseInfo
      Object.assign(enterpriseInfo, response.data)
      // 处理一些字段映射，确保数据结构一致
      if (response.data.enterpriseClassification) {
        enterpriseInfo.industry = response.data.enterpriseClassification
      }
      if (response.data.city) {
        enterpriseInfo.location = `${response.data.province || ''}${response.data.city}`
      }
      // 后端没有返回的字段使用默认值
      enterpriseInfo.introduction = enterpriseInfo.introduction || '暂无企业介绍'
      enterpriseInfo.culture = enterpriseInfo.culture || '暂无企业文化'
      enterpriseInfo.developmentHistory = enterpriseInfo.developmentHistory || []
      enterpriseInfo.recruitmentPolicy = enterpriseInfo.recruitmentPolicy || '暂无招聘政策'
      enterpriseInfo.welfareList = enterpriseInfo.welfareList || []
    } else {
      ElMessage.warning('获取企业详情失败，使用模拟数据')
      Object.assign(enterpriseInfo, mockEnterpriseInfo)
    }
  } catch (error) {
    console.error('获取企业详情失败:', error)
    ElMessage.error('获取企业详情失败，使用模拟数据')
    Object.assign(enterpriseInfo, mockEnterpriseInfo)
  }
}

// 页面加载时获取企业详情
onMounted(() => {
  getEnterpriseDetail()
})

// 模拟职位列表数据
const mockPositionList = [
  { positionId: 1, positionName: '前端开发工程师', department: '技术部', salaryRange: '20-30K', workLocation: '深圳市', education: '本科' },
  { positionId: 2, positionName: '后端开发工程师', department: '技术部', salaryRange: '20-30K', workLocation: '深圳市', education: '本科' },
  { positionId: 3, positionName: '产品经理', department: '产品部', salaryRange: '15-25K', workLocation: '深圳市', education: '本科' },
  { positionId: 4, positionName: 'UI设计师', department: '设计部', salaryRange: '15-25K', workLocation: '深圳市', education: '本科' }
]

const positionList = ref([...mockPositionList])

// 根据规模获取标签类型
const getScaleType = (scale) => {
  const typeMap = {
    small: 'primary',
    medium: 'success',
    large: 'warning',
    huge: 'danger'
  }
  return typeMap[scale] || 'info'
}

// 根据规模获取标签文本
const getScaleLabel = (scale) => {
  const labelMap = {
    small: '100人以下',
    medium: '100-500人',
    large: '500-1000人',
    huge: '1000人以上'
  }
  return labelMap[scale] || scale
}

// 返回列表
const handleBack = () => {
  router.push('/enterprise/list')
}

// 编辑企业
const handleEdit = () => {
  router.push(`/enterprise/edit/${enterpriseInfo.enterpriseId}`)
}

// 删除企业
const handleDelete = () => {
  ElMessage.success('删除功能待实现')
}

// 新增职位
const handleAddPosition = () => {
  router.push(`/enterprise/${enterpriseInfo.enterpriseId}/position/new`)
}

// 查看职位详情
const handlePositionDetail = (row) => {
  router.push(`/enterprise/${enterpriseInfo.enterpriseId}/position/${row.positionId}`)
}

// 编辑职位
const handleEditPosition = (row) => {
  router.push(`/enterprise/${enterpriseInfo.enterpriseId}/position/edit/${row.positionId}`)
}
</script>

<style scoped>
.enterprise-detail-container {
  padding: 20px;
}

.detail-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
}

.introduction-content,
.culture-content,
.policy-content {
  line-height: 1.8;
  text-indent: 2em;
}

.welfare-content {
  padding: 10px 0;
}

.history-content {
  padding: 10px 0;
}
</style>
