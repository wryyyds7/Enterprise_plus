<template>
  <div class="event-detail">
    <NavigationHeader />
    <div class="page-content">
    <h2>{{ event.title }}</h2>
    
    <div class="event-info-card">
      <el-card shadow="hover">
        <div class="event-basic-info">
          <div class="info-item">
            <el-icon><Calendar /></el-icon>
            <span class="label">时间：</span>
            <span>{{ event.startTime }} - {{ event.endTime }}</span>
          </div>
          <div class="info-item">
            <el-icon><Location /></el-icon>
            <span class="label">地点：</span>
            <span>{{ event.location }}</span>
          </div>
          <div class="info-item">
            <el-icon><User /></el-icon>
            <span class="label">主办方：</span>
            <span>{{ event.organizer }}</span>
          </div>
          <div class="info-item">
            <el-icon><OfficeBuilding /></el-icon>
            <span class="label">参与企业：</span>
            <span>{{ event.companyCount }}家</span>
          </div>
          <div class="info-item">
            <el-icon><Ticket /></el-icon>
            <span class="label">报名截止：</span>
            <span>{{ event.registrationDeadline }}</span>
          </div>
        </div>
        
        <div class="event-actions">
          <el-button :type="event.registered ? 'info' : 'success'" :disabled="event.registered" @click="registerEvent">
            <el-icon><Check /></el-icon>
            {{ event.registered ? '已报名' : '立即报名' }}
          </el-button>
          <el-button type="primary" @click="shareEvent">
            <el-icon><Share /></el-icon>
            分享活动
          </el-button>
        </div>
      </el-card>
    </div>
    
    <el-tabs v-model="activeTab" class="event-tabs">
      <el-tab-pane label="活动详情" name="details">
        <div class="tab-content">
          <h3>活动简介</h3>
          <p>{{ event.description }}</p>
          
          <h3>活动亮点</h3>
          <ul class="highlights">
            <li v-for="(highlight, index) in event.highlights" :key="index">
              <el-icon><StarFilled /></el-icon> {{ highlight }}
            </li>
          </ul>
          
          <h3>活动日程</h3>
          <el-timeline>
            <el-timeline-item v-for="item in event.schedule" :key="item.time" :timestamp="item.time">
              <h4>{{ item.title }}</h4>
              <p>{{ item.description }}</p>
            </el-timeline-item>
          </el-timeline>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="参与企业" name="companies">
        <div class="tab-content">
          <div class="companies-list">
            <el-card v-for="company in event.companies" :key="company.id" shadow="hover" class="company-card">
              <div class="company-info">
                <img :src="company.logo" alt="企业logo" class="company-logo">
                <div class="company-details">
                  <h4>{{ company.name }}</h4>
                  <p class="company-industry">{{ company.industry }}</p>
                  <p class="company-desc">{{ company.description }}</p>
                </div>
              </div>
              <el-button type="primary" size="small" @click="viewCompanyDetail(company.id)">
                查看详情
              </el-button>
            </el-card>
          </div>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="报名须知" name="registration">
        <div class="tab-content">
          <h3>报名条件</h3>
          <ul class="requirements">
            <li v-for="(requirement, index) in event.registrationRequirements" :key="index">
              <el-icon><CircleCheck /></el-icon> {{ requirement }}
            </li>
          </ul>
          
          <h3>报名材料</h3>
          <ul class="materials">
            <li v-for="(material, index) in event.registrationMaterials" :key="index">
              <el-icon><Document /></el-icon> {{ material }}
            </li>
          </ul>
          
          <h3>注意事项</h3>
          <div class="notes">
            <p>{{ event.registrationNotes }}</p>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Calendar, Location, User, OfficeBuilding, Ticket, Check, Share, StarFilled, CircleCheck, Document } from '@element-plus/icons-vue'
import NavigationHeader from '@/components/NavigationHeader.vue'

const route = useRoute()
const router = useRouter()
const activeTab = ref('details')

const event = ref({
  id: route.params.eventId || 1,
  title: '2024秋季校园招聘会',
  startTime: '2024-10-20 09:00',
  endTime: '2024-10-20 17:00',
  location: '北京大学校园招聘会场馆',
  organizer: '北京大学就业指导中心',
  companyCount: 100,
  registrationDeadline: '2024-10-18 23:59',
  description: '本次招聘会汇聚了100家知名企业，涵盖互联网、金融、教育等多个行业，为2025届毕业生提供丰富的就业机会。',
  highlights: [
    '100家知名企业现场招聘',
    '提供1000+优质岗位',
    '专业面试指导',
    '职业规划咨询',
    '现场签约机会'
  ],
  schedule: [
    {
      time: '09:00-09:30',
      title: '活动开幕式',
      description: '学校领导致辞，企业代表发言'
    },
    {
      time: '09:30-12:00',
      title: '企业宣讲会',
      description: '部分企业进行现场宣讲'
    },
    {
      time: '12:00-13:30',
      title: '午休时间',
      description: '提供午餐和休息场所'
    },
    {
      time: '13:30-17:00',
      title: '现场招聘',
      description: '企业与学生现场交流、面试'
    }
  ],
  companies: [
    {
      id: 1,
      name: '腾讯科技有限公司',
      logo: 'https://www.tencent.com/img/index/logo.png',
      industry: '互联网',
      description: '腾讯是中国领先的互联网增值服务提供商'
    },
    {
      id: 2,
      name: '阿里巴巴集团',
      logo: 'https://www.alibaba.com/static/shop/frontend/common/images/logo.png',
      industry: '互联网',
      description: '阿里巴巴是全球领先的数字经济基础设施提供商'
    },
    {
      id: 3,
      name: '百度在线网络技术有限公司',
      logo: 'https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png',
      industry: '互联网',
      description: '百度是全球最大的中文搜索引擎'
    }
  ],
  registrationRequirements: [
    '2025届应届毕业生',
    '携带本人身份证、学生证',
    '准备好个人简历',
    '提前在线报名'
  ],
  registrationMaterials: [
    '个人简历（3份以上）',
    '学习成绩单',
    '相关证书复印件',
    '就业推荐表'
  ],
  registrationNotes: '请提前15分钟到达会场，有序排队入场。遵守会场秩序，注意个人财物安全。如有任何问题，请联系现场工作人员。',
  registered: false
})

const registerEvent = () => {
  event.value.registered = true
  ElMessage.success('报名成功！')
}

const shareEvent = () => {
  // 模拟分享功能
  ElMessage.info('分享功能开发中...')
}

const viewCompanyDetail = (companyId) => {
  router.push(`/home/enterprise/${companyId}`)
}

onMounted(() => {
  // 组件挂载时获取活动详情数据
})
</script>

<style scoped>
.event-detail {
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

.event-info-card {
  margin-bottom: 20px;
}

.event-basic-info {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-item .el-icon {
  margin-right: 10px;
  color: #67c23a;
}

.info-item .label {
  font-weight: bold;
  margin-right: 5px;
}

.event-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.event-tabs {
  margin-top: 20px;
}

.tab-content {
  padding: 20px 0;
}

.highlights, .requirements, .materials {
  list-style: none;
  padding: 0;
  margin: 20px 0;
}

.highlights li, .requirements li, .materials li {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.highlights li .el-icon, .requirements li .el-icon, .materials li .el-icon {
  margin-right: 10px;
  color: #67c23a;
}

.companies-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.company-card {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.company-info {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.company-logo {
  width: 60px;
  height: 60px;
  margin-right: 15px;
  border-radius: 8px;
}

.company-details {
  flex: 1;
}

.company-industry {
  color: #666;
  margin: 5px 0;
}

.company-desc {
  color: #999;
  font-size: 14px;
}
</style>