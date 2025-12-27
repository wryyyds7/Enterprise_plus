<template>
  <div class="enterprise-detail">
    <NavigationHeader />
    <div class="page-content">
    <div class="enterprise-header">
      <div class="enterprise-basic-info">
        <img :src="enterprise.logo" alt="企业logo" class="enterprise-logo">
        <div class="enterprise-name-section">
          <h2>{{ enterprise.name }}</h2>
          <div class="enterprise-tags">
            <el-tag v-for="tag in enterprise.tags" :key="tag" type="success" size="small">
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>
      <div class="enterprise-actions">
        <el-button :type="enterprise.isFavorited ? 'warning' : 'primary'" @click="toggleFavorite">
          <el-icon><StarFilled /></el-icon>
          {{ enterprise.isFavorited ? '已收藏' : '收藏企业' }}
        </el-button>
      </div>
    </div>
    
    <el-tabs v-model="activeTab" class="enterprise-tabs">
      <el-tab-pane label="企业介绍" name="introduction">
        <div class="tab-content">
          <h3>企业简介</h3>
          <p>{{ enterprise.introduction }}</p>
          
          <h3>发展历程</h3>
          <div class="timeline">
            <el-timeline>
              <el-timeline-item v-for="item in enterprise.history" :key="item.year" :timestamp="item.year">
                {{ item.event }}
              </el-timeline-item>
            </el-timeline>
          </div>
          
          <h3>企业文化</h3>
          <div class="culture">
            <el-card v-for="item in enterprise.culture" :key="item.title" shadow="hover" class="culture-card">
              <h4>{{ item.title }}</h4>
              <p>{{ item.description }}</p>
            </el-card>
          </div>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="校招信息" name="campus-recruitment">
        <div class="tab-content">
          <h3>校招政策</h3>
          <p>{{ enterprise.campusPolicy }}</p>
          
          <h3>薪酬福利</h3>
          <ul class="benefits-list">
            <li v-for="benefit in enterprise.benefits" :key="benefit">
              <el-icon><Check /></el-icon> {{ benefit }}
            </li>
          </ul>
          
          <h3>晋升路径</h3>
          <p>{{ enterprise.promotionPath }}</p>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="招聘数据" name="recruitment-data">
        <div class="tab-content">
          <h3>历年招聘情况</h3>
          <div class="recruitment-stats">
            <el-card v-for="stat in enterprise.recruitmentStats" :key="stat.year" shadow="hover" class="stat-card">
              <h4>{{ stat.year }}年</h4>
              <p>招聘人数: {{ stat.numberOfHires }}</p>
              <p>录用率: {{ stat.acceptanceRate }}</p>
              <p>主要岗位: {{ stat.mainPositions }}</p>
            </el-card>
          </div>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="学生评价" name="student-comments">
        <div class="tab-content">
          <div class="comments-list">
            <el-card v-for="comment in enterprise.comments" :key="comment.id" shadow="hover" class="comment-card">
              <div class="comment-header">
                <span class="comment-student">{{ comment.studentName }}</span>
                <span class="comment-time">{{ comment.time }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-rating">
                <el-rate v-model="comment.rating" disabled />
              </div>
            </el-card>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { StarFilled, Check } from '@element-plus/icons-vue'
import NavigationHeader from '@/components/NavigationHeader.vue'

const route = useRoute()
const activeTab = ref('introduction')

const enterprise = ref({
  id: route.params.enterpriseId || 1,
  name: '腾讯科技有限公司',
  logo: 'https://www.tencent.com/img/index/logo.png',
  tags: ['互联网', '上市公司', '500强', '校园招聘合作单位'],
  introduction: '腾讯是中国领先的互联网增值服务提供商，为用户提供社交、游戏、金融、云计算等多元化服务。',
  history: [
    { year: '1998', event: '腾讯公司成立' },
    { year: '1999', event: '推出QQ即时通讯软件' },
    { year: '2004', event: '在香港联交所上市' },
    { year: '2011', event: '推出微信' },
    { year: '2020', event: '成为全球市值最高的互联网公司之一' }
  ],
  culture: [
    { title: '用户为本', description: '一切以用户价值为依归' },
    { title: '科技向善', description: '利用科技解决社会问题，创造美好未来' },
    { title: '创新驱动', description: '持续创新，引领行业发展' },
    { title: '协作共赢', description: '开放协作，与合作伙伴共同成长' }
  ],
  campusPolicy: '腾讯2025校园招聘面向全国高校应届毕业生，提供技术、产品、设计、市场、职能等多个方向的岗位。我们重视人才培养，为新员工提供完善的培训体系和职业发展路径。',
  benefits: [
    '具有竞争力的薪资待遇',
    '六险一金及补充商业保险',
    '弹性工作时间',
    '带薪年假和节假日福利',
    '员工食堂和住房补贴',
    '定期体检和健康管理',
    '丰富的团队建设活动'
  ],
  promotionPath: '腾讯建立了完善的晋升体系，新员工入职后可通过专业通道或管理通道发展。专业通道注重技术能力提升，管理通道注重团队管理能力培养。每年有两次晋升机会，根据员工的工作表现和能力进行评估。',
  recruitmentStats: [
    { year: '2024', numberOfHires: 1000, acceptanceRate: '5%', mainPositions: '技术、产品、设计' },
    { year: '2023', numberOfHires: 800, acceptanceRate: '4%', mainPositions: '技术、市场、职能' },
    { year: '2022', numberOfHires: 600, acceptanceRate: '3%', mainPositions: '技术、产品' }
  ],
  comments: [
    { id: 1, studentName: '张三', time: '2024-09-15', content: '腾讯的校招流程非常专业，面试官很nice，给了我很多宝贵的建议。', rating: 5 },
    { id: 2, studentName: '李四', time: '2024-09-10', content: '腾讯的工作环境很好，同事们都很优秀，是一个能学到很多东西的地方。', rating: 4 },
    { id: 3, studentName: '王五', time: '2024-08-25', content: '腾讯的薪酬福利很有竞争力，培训体系也很完善。', rating: 5 }
  ],
  isFavorited: false
})

const toggleFavorite = () => {
  enterprise.value.isFavorited = !enterprise.value.isFavorited
  ElMessage.success(enterprise.value.isFavorited ? '收藏成功！' : '取消收藏成功！')
}

onMounted(() => {
  // 组件挂载时获取企业详情数据
})
</script>

<style scoped>
.enterprise-detail {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  flex: 1;
}

.enterprise-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.enterprise-basic-info {
  display: flex;
  align-items: center;
}

.enterprise-logo {
  width: 80px;
  height: 80px;
  margin-right: 20px;
  border-radius: 8px;
}

.enterprise-name-section {
  display: flex;
  flex-direction: column;
}

.enterprise-tags {
  margin-top: 10px;
}

.enterprise-tabs {
  margin-top: 20px;
}

.tab-content {
  padding: 20px 0;
}

.timeline {
  margin: 20px 0;
}

.culture {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin: 20px 0;
}

.benefits-list {
  list-style: none;
  padding: 0;
  margin: 20px 0;
}

.benefits-list li {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.benefits-list li .el-icon {
  margin-right: 10px;
  color: #67c23a;
}

.recruitment-stats {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin: 20px 0;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-rating {
  margin-top: 10px;
}
</style>