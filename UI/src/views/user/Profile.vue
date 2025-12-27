<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <div class="profile-section">
            <div class="avatar-upload">
              <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar">
                {{ userInfo.username?.charAt(0) }}
              </el-avatar>
              <el-button type="primary" size="small" class="upload-btn">
                <el-icon><Upload /></el-icon>更换头像
              </el-button>
            </div>

            <el-form :model="userInfo" label-width="120px" class="profile-form">
              <el-form-item label="用户名">
                <el-input v-model="userInfo.username" readonly />
              </el-form-item>
              <el-form-item label="真实姓名">
                <el-input v-model="userInfo.realName" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userInfo.email" type="email" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userInfo.phone" />
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="userInfo.gender">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="生日">
                <el-date-picker v-model="userInfo.birthday" type="date" placeholder="选择生日" />
              </el-form-item>
              <el-form-item label="所在学校">
                <el-input v-model="userInfo.school" />
              </el-form-item>
              <el-form-item label="专业">
                <el-input v-model="userInfo.major" />
              </el-form-item>
              <el-form-item label="学历">
                <el-select v-model="userInfo.education" placeholder="选择学历">
                  <el-option label="本科" value="本科" />
                  <el-option label="硕士" value="硕士" />
                  <el-option label="博士" value="博士" />
                </el-select>
              </el-form-item>
            </el-form>

            <div class="form-actions">
              <el-button type="primary" @click="saveUserInfo">保存修改</el-button>
              <el-button @click="resetForm">取消</el-button>
            </div>
          </div>
        </el-tab-pane>

        <!-- 求职偏好 -->
        <el-tab-pane label="求职偏好" name="preference">
          <el-form :model="jobPreference" label-width="120px" class="profile-form">
            <el-form-item label="期望行业">
              <el-select v-model="jobPreference.industry" placeholder="选择期望行业" multiple>
                <el-option label="互联网" value="互联网" />
                <el-option label="金融" value="金融" />
                <el-option label="教育" value="教育" />
                <el-option label="医疗" value="医疗" />
                <el-option label="制造业" value="制造业" />
              </el-select>
            </el-form-item>
            <el-form-item label="期望职位类型">
              <el-select v-model="jobPreference.positionType" placeholder="选择期望职位类型" multiple>
                <el-option label="技术" value="技术" />
                <el-option label="产品" value="产品" />
                <el-option label="运营" value="运营" />
                <el-option label="市场" value="市场" />
                <el-option label="销售" value="销售" />
              </el-select>
            </el-form-item>
            <el-form-item label="期望工作地点">
              <el-select v-model="jobPreference.location" placeholder="选择期望工作地点" multiple>
                <el-option label="北京" value="北京" />
                <el-option label="上海" value="上海" />
                <el-option label="广州" value="广州" />
                <el-option label="深圳" value="深圳" />
                <el-option label="杭州" value="杭州" />
              </el-select>
            </el-form-item>
            <el-form-item label="期望薪资范围">
              <el-input-number v-model="jobPreference.minSalary" :min="0" :step="1000" />
              <span class="salary-separator">-</span>
              <el-input-number v-model="jobPreference.maxSalary" :min="0" :step="1000" />
              <span class="salary-unit">元/月</span>
            </el-form-item>
          </el-form>

          <div class="form-actions">
            <el-button type="primary" @click="saveJobPreference">保存偏好</el-button>
            <el-button @click="resetPreference">取消</el-button>
          </div>
        </el-tab-pane>

        <!-- 收藏管理 -->
        <el-tab-pane label="我的收藏" name="favorites">
          <el-tabs v-model="favoriteTab">
            <el-tab-pane label="收藏的企业" name="enterprises">
              <div class="favorite-list">
                <el-card v-for="enterprise in favoriteEnterprises" :key="enterprise.id" class="favorite-item">
                  <div class="enterprise-info">
                    <el-avatar :size="60" :src="enterprise.logo">{{ enterprise.name?.charAt(0) }}</el-avatar>
                    <div class="enterprise-details">
                      <h4>{{ enterprise.name }}</h4>
                      <p class="industry">{{ enterprise.industry }}</p>
                      <p class="location">{{ enterprise.location }}</p>
                    </div>
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="unfavoriteEnterprise(enterprise.id)"
                    >
                      <el-icon><StarFilled /></el-icon>取消收藏
                    </el-button>
                  </div>
                </el-card>
              </div>
            </el-tab-pane>

            <el-tab-pane label="收藏的活动" name="events">
              <div class="favorite-list">
                <el-card v-for="event in favoriteEvents" :key="event.id" class="favorite-item">
                  <div class="event-info">
                    <div class="event-details">
                      <h4>{{ event.title }}</h4>
                      <p class="time">{{ formatDate(event.startTime) }} - {{ formatDate(event.endTime) }}</p>
                      <p class="location">{{ event.location }}</p>
                    </div>
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="unfavoriteEvent(event.id)"
                    >
                      <el-icon><StarFilled /></el-icon>取消收藏
                    </el-button>
                  </div>
                </el-card>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/store/modules/auth'
import { ElMessage } from 'element-plus'
import { Upload, StarFilled } from '@element-plus/icons-vue'
import userApi from '@/api/modules/user'
import homeApi from '@/api/modules/home'

const authStore = useAuthStore()
const activeTab = ref('basic')
const favoriteTab = ref('enterprises')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 模拟用户数据
const mockUserInfo = {
  id: 1,
  username: 'student123',
  realName: '张三',
  email: 'zhangsan@example.com',
  phone: '13800138000',
  gender: '男',
  birthday: '1999-01-01',
  school: '北京大学',
  major: '计算机科学与技术',
  avatar: '',
  createTime: '2024-01-01'
}

// 模拟求职偏好
const mockJobPreference = {
  industry: ['互联网', '金融'],
  positionType: ['技术'],
  location: ['北京', '上海'],
  minSalary: 10000,
  maxSalary: 20000
}

// 模拟收藏数据
const mockFavoriteEnterprises = [
  {
    id: 1,
    name: '阿里巴巴集团',
    logo: '',
    industry: '互联网',
    location: '杭州'
  },
  {
    id: 2,
    name: '腾讯科技',
    logo: '',
    industry: '互联网',
    location: '深圳'
  }
]

const mockFavoriteEvents = [
  {
    id: 1,
    title: '2024春季校招宣讲会',
    startTime: '2024-03-15 14:00:00',
    endTime: '2024-03-15 17:00:00',
    location: '北京大学就业中心'
  }
]

// 实际数据状态
const userInfo = reactive({ ...mockUserInfo })
const jobPreference = reactive({ ...mockJobPreference })
const favoriteEnterprises = ref([...mockFavoriteEnterprises])
const favoriteEvents = ref([...mockFavoriteEvents])

// 获取用户信息
const getUserInfo = async () => {
  try {
    if (authStore.userInfo?.id) {
      const response = await userApi.getUserInfo(authStore.userInfo.id)
      if (response.data) {
        Object.assign(userInfo, response.data)
      }
    } else {
      ElMessage.warning('用户未登录，无法获取用户信息')
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败：' + error.message)
  }
}

// 获取用户求职偏好
const getJobPreference = async () => {
  try {
    if (authStore.userInfo?.id) {
      const response = await userApi.getJobPreference(authStore.userInfo.id)
      if (response.data) {
        Object.assign(jobPreference, response.data)
      }
    } else {
      ElMessage.warning('用户未登录，无法获取求职偏好')
    }
  } catch (error) {
    ElMessage.error('获取求职偏好失败：' + error.message)
  }
}

// 获取用户收藏的企业
const getUserFavoriteEnterprises = async () => {
  try {
    if (authStore.userInfo?.id) {
      const response = await homeApi.getUserFavoriteEnterprises({ userId: authStore.userInfo.id })
      if (response.data && response.data.length > 0) {
        favoriteEnterprises.value = response.data
      }
    } else {
      ElMessage.warning('用户未登录，无法获取收藏的企业')
    }
  } catch (error) {
    ElMessage.warning('获取收藏企业列表失败，使用模拟数据：' + error.message)
  }
}

// 获取用户收藏的活动
const getUserFavoriteEvents = async () => {
  try {
    if (authStore.userInfo?.id) {
      const response = await homeApi.getUserFavoriteEvents({ userId: authStore.userInfo.id })
      if (response.data && response.data.length > 0) {
        favoriteEvents.value = response.data
      }
    } else {
      ElMessage.warning('用户未登录，无法获取收藏的活动')
    }
  } catch (error) {
    ElMessage.warning('获取收藏活动列表失败，使用模拟数据：' + error.message)
  }
}

// 保存用户信息
const saveUserInfo = async () => {
  try {
    if (authStore.userInfo?.id) {
      const data = { ...userInfo, userId: authStore.userInfo.id }
      await userApi.updateUser(data)
      ElMessage.success('个人信息更新成功')
    } else {
      ElMessage.warning('用户未登录，无法保存个人信息')
    }
  } catch (error) {
    ElMessage.error('个人信息更新失败：' + error.message)
  }
}

// 保存求职偏好
const saveJobPreference = async () => {
  try {
    if (authStore.userInfo?.id) {
      const data = { ...jobPreference, userId: authStore.userInfo.id }
      await userApi.updateJobPreference(data)
      ElMessage.success('求职偏好保存成功')
    } else {
      ElMessage.warning('用户未登录，无法保存求职偏好')
    }
  } catch (error) {
    ElMessage.error('求职偏好保存失败：' + error.message)
  }
}

// 取消收藏企业
const unfavoriteEnterprise = async (enterpriseId) => {
  try {
    await homeApi.unfavoriteEnterprise(enterpriseId)
    favoriteEnterprises.value = favoriteEnterprises.value.filter(item => item.id !== enterpriseId)
    ElMessage.success('取消收藏成功')
  } catch (error) {
    ElMessage.error('取消收藏失败：' + error.message)
  }
}

// 取消收藏活动
const unfavoriteEvent = async (eventId) => {
  try {
    await homeApi.unfavoriteEvent(eventId)
    favoriteEvents.value = favoriteEvents.value.filter(item => item.id !== eventId)
    ElMessage.success('取消收藏成功')
  } catch (error) {
    ElMessage.error('取消收藏失败：' + error.message)
  }
}

// 重置表单
const resetForm = () => {
  getUserInfo()
}

// 重置偏好
const resetPreference = () => {
  getJobPreference()
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 页面加载时获取用户信息和收藏列表
onMounted(() => {
  getUserInfo()
  getJobPreference()
  getUserFavoriteEnterprises()
  getUserFavoriteEvents()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.profile-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-section {
  padding: 20px 0;
}

.avatar-upload {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.avatar-upload .el-avatar {
  margin-right: 20px;
}

.upload-btn {
  margin-left: 20px;
}

.profile-form {
  max-width: 600px;
}

.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: flex-start;
}

.form-actions .el-button {
  margin-right: 10px;
}

.salary-separator {
  margin: 0 10px;
  line-height: 32px;
}

.salary-unit {
  margin-left: 10px;
  line-height: 32px;
}

.favorite-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.favorite-item {
  cursor: pointer;
  transition: all 0.3s;
}

.favorite-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.enterprise-info,
.event-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.enterprise-details,
.event-details {
  flex: 1;
  margin: 0 20px;
}

.enterprise-details h4,
.event-details h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
}

.industry, .location, .time {
  margin: 4px 0;
  color: #606266;
  font-size: 14px;
}
</style>
