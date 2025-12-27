<template>
  <div class="position-detail-container">
    <el-card shadow="hover" class="detail-card">
      <template #header>
        <div class="card-header">
          <el-button type="primary" @click="handleBack">返回列表</el-button>
          <span style="margin-left: 20px;">职位详情</span>
          <div style="margin-left: auto;">
            <el-button @click="handleEdit">编辑</el-button>
            <el-button type="danger" @click="handleDelete">删除</el-button>
          </div>
        </div>
      </template>

      <el-card shadow="hover" class="info-card">
        <template #header>
          <div class="card-header">
            <h2>{{ positionInfo.positionName }}</h2>
            <span class="department">{{ positionInfo.department }}</span>
          </div>
        </template>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="薪资范围">{{ positionInfo.salaryRange }}</el-descriptions-item>
          <el-descriptions-item label="工作地点">{{ positionInfo.workLocation }}</el-descriptions-item>
          <el-descriptions-item label="学历要求">{{ positionInfo.education }}</el-descriptions-item>
          <el-descriptions-item label="经验要求">{{ positionInfo.experience }}</el-descriptions-item>
          <el-descriptions-item label="职位类型">
            <el-tag :type="positionInfo.positionType === '全职' ? 'success' : 'warning'">
              {{ positionInfo.positionType }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ positionInfo.publishDate }}</el-descriptions-item>
          <el-descriptions-item label="招聘人数">{{ positionInfo.recruitNumber }}人</el-descriptions-item>
          <el-descriptions-item label="截止时间">{{ positionInfo.deadline }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="positionInfo.status === '1' ? 'success' : 'danger'">
              {{ positionInfo.status === '1' ? '招聘中' : '已结束' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card shadow="hover" class="content-card" style="margin-top: 20px;">
        <template #header>
          <div class="card-header">职位描述</div>
        </template>
        <div class="description-content">
          {{ positionInfo.description }}
        </div>
      </el-card>

      <el-card shadow="hover" class="content-card" style="margin-top: 20px;">
        <template #header>
          <div class="card-header">职位职责</div>
        </template>
        <div class="responsibility-content">
          <el-timeline>
            <el-timeline-item v-for="(item, index) in positionInfo.responsibility" :key="index">
              {{ item }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </el-card>

      <el-card shadow="hover" class="content-card" style="margin-top: 20px;">
        <template #header>
          <div class="card-header">任职要求</div>
        </template>
        <div class="requirement-content">
          <el-timeline>
            <el-timeline-item v-for="(item, index) in positionInfo.requirement" :key="index">
              {{ item }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </el-card>

      <el-card shadow="hover" class="content-card" style="margin-top: 20px;">
        <template #header>
          <div class="card-header">公司福利</div>
        </template>
        <div class="welfare-content">
          <el-tag v-for="(item, index) in positionInfo.welfare" :key="index" style="margin-right: 10px; margin-bottom: 10px;">
            {{ item }}
          </el-tag>
        </div>
      </el-card>

      <el-card shadow="hover" class="content-card" style="margin-top: 20px;">
        <template #header>
          <div class="card-header">联系方式</div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="联系人">{{ positionInfo.contactPerson }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ positionInfo.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="联系邮箱">{{ positionInfo.contactEmail }}</el-descriptions-item>
          <el-descriptions-item label="公司名称">{{ positionInfo.companyName }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 获取职位ID和企业ID（从路由参数获取）
const positionId = ref(route.params.positionId || 1)
const enterpriseId = ref(route.params.enterpriseId || 1)

// 模拟职位详情数据
const mockPositionInfo = {
  positionId: 1,
  positionName: '前端开发工程师',
  department: '技术部',
  salaryRange: '20-30K',
  workLocation: '深圳市南山区',
  education: '本科',
  experience: '1-3年',
  positionType: '全职',
  publishDate: '2024-01-15',
  recruitNumber: 10,
  deadline: '2024-06-30',
  status: '1',
  description: '我们正在寻找一位有经验的前端开发工程师，加入我们的技术团队，负责公司核心产品的前端开发工作。你将参与产品的需求分析、设计和开发，与后端工程师紧密合作，确保产品的高质量交付。',
  responsibility: [
    '负责公司产品的前端开发工作，包括页面布局、交互设计和功能实现',
    '与产品经理和设计师合作，理解产品需求，将设计稿转化为高质量的前端代码',
    '与后端工程师协作，完成前后端数据交互和接口对接',
    '优化前端性能，提高页面加载速度和用户体验',
    '维护和升级现有前端代码，解决技术问题',
    '学习和研究新技术，不断提高前端开发能力'
  ],
  requirement: [
    '本科及以上学历，计算机相关专业',
    '1-3年前端开发经验，熟练掌握HTML、CSS和JavaScript',
    '熟悉Vue.js或React等前端框架，有实际项目开发经验',
    '了解前端工程化，熟悉Webpack、Vite等构建工具',
    '熟悉HTTP协议，了解RESTful API设计',
    '良好的沟通能力和团队合作精神，责任心强',
    '有移动端开发经验或TypeScript使用经验者优先'
  ],
  welfare: ['六险一金', '带薪年假', '年度体检', '员工食堂', '交通补贴', '住房补贴', '节日福利', '团队建设'],
  contactPerson: '李先生',
  contactPhone: '0755-12345678',
  contactEmail: 'hr@company.com',
  companyName: '腾讯科技有限公司'
}

const positionInfo = reactive({ ...mockPositionInfo })

// 返回列表
const handleBack = () => {
  router.push(`/enterprise/${enterpriseId.value}/positions`)
}

// 编辑职位
const handleEdit = () => {
  router.push(`/enterprise/${enterpriseId.value}/position/edit/${positionId.value}`)
}

// 删除职位
const handleDelete = () => {
  ElMessage.success('删除功能待实现')
}
</script>

<style scoped>
.position-detail-container {
  padding: 20px;
}

.detail-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.department {
  color: #606266;
  font-size: 16px;
}

.info-card {
  margin-bottom: 20px;
}

.content-card {
  margin-bottom: 20px;
}

.description-content {
  line-height: 1.8;
  text-indent: 2em;
}

.responsibility-content,
.requirement-content {
  padding: 10px 0;
}

.responsibility-content .el-timeline-item,
.requirement-content .el-timeline-item {
  padding-bottom: 10px;
}

.welfare-content {
  padding: 10px 0;
}
</style>
