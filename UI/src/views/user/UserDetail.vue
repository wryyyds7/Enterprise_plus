<template>
  <div class="user-detail-container">
    <el-card class="user-detail-card">
      <template #header>
        <div class="card-header">
          <span>用户详情</span>
          <el-button type="primary" size="small" @click="goBack">
            <el-icon><ArrowLeft /></el-icon>返回列表
          </el-button>
        </div>
      </template>

      <div class="detail-content">
        <!-- 基本信息 -->
        <el-card class="info-card">
          <template #header>
            <div class="card-sub-header">
              <span>基本信息</span>
            </div>
          </template>

          <div class="info-section">
            <div class="avatar-section">
              <el-avatar :size="120" :src="userInfo.avatar || defaultAvatar">
                {{ userInfo.username?.charAt(0) }}
              </el-avatar>
            </div>

            <div class="info-grid">
              <div class="info-item">
                <span class="label">用户ID：</span>
                <span class="value">{{ userInfo.id }}</span>
              </div>
              <div class="info-item">
                <span class="label">用户名：</span>
                <span class="value">{{ userInfo.username }}</span>
              </div>
              <div class="info-item">
                <span class="label">真实姓名：</span>
                <span class="value">{{ userInfo.realName }}</span>
              </div>
              <div class="info-item">
                <span class="label">邮箱：</span>
                <span class="value">{{ userInfo.email }}</span>
              </div>
              <div class="info-item">
                <span class="label">手机号：</span>
                <span class="value">{{ userInfo.phone }}</span>
              </div>
              <div class="info-item">
                <span class="label">性别：</span>
                <span class="value">{{ userInfo.gender === 'male' ? '男' : '女' }}</span>
              </div>
              <div class="info-item">
                <span class="label">角色：</span>
                <el-tag :type="userInfo.role === 'admin' ? 'danger' : 'success'" class="role-tag">
                  {{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}
                </el-tag>
              </div>
              <div class="info-item">
                <span class="label">状态：</span>
                <el-tag :type="userInfo.status === '1' ? 'success' : 'warning'" class="status-tag">
                  {{ userInfo.status === '1' ? '启用' : '禁用' }}
                </el-tag>
              </div>
              <div class="info-item">
                <span class="label">创建时间：</span>
                <span class="value">{{ userInfo.createTime }}</span>
              </div>
              <div class="info-item">
                <span class="label">最后登录：</span>
                <span class="value">{{ userInfo.lastLoginTime || '从未登录' }}</span>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 个人资料 -->
        <el-card class="info-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-sub-header">
              <span>个人资料</span>
            </div>
          </template>

          <div class="info-grid">
            <div class="info-item">
              <span class="label">学校：</span>
              <span class="value">{{ userInfo.school || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">专业：</span>
              <span class="value">{{ userInfo.major || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">学历：</span>
              <span class="value">{{ userInfo.education || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">生日：</span>
              <span class="value">{{ userInfo.birthday || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">籍贯：</span>
              <span class="value">{{ userInfo.hometown || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">毕业时间：</span>
              <span class="value">{{ userInfo.graduationTime || '未设置' }}</span>
            </div>
          </div>
        </el-card>

        <!-- 求职偏好 -->
        <el-card class="info-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-sub-header">
              <span>求职偏好</span>
            </div>
          </template>

          <div class="info-grid">
            <div class="info-item">
              <span class="label">期望行业：</span>
              <span class="value">{{ userInfo.preferences?.industry?.join(', ') || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">期望职位：</span>
              <span class="value">{{ userInfo.preferences?.positionType?.join(', ') || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">期望地点：</span>
              <span class="value">{{ userInfo.preferences?.location?.join(', ') || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">期望薪资：</span>
              <span class="value">
                {{ userInfo.preferences?.minSalary ? `${userInfo.preferences.minSalary}-${userInfo.preferences.maxSalary}元/月` : '未设置' }}
              </span>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import userApi from '@/api/modules/user'

const router = useRouter()
const route = useRoute()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 模拟用户详情数据
const mockUserInfo = {
  id: 1,
  username: 'student123',
  realName: '张三',
  email: 'zhangsan@example.com',
  phone: '13800138000',
  gender: 'male',
  avatar: '',
  role: 'user',
  status: '1',
  school: '北京大学',
  major: '计算机科学与技术',
  education: '本科',
  birthday: '1999-01-01',
  hometown: '北京市',
  graduationTime: '2024-06-30',
  createTime: '2024-01-01 00:00:00',
  lastLoginTime: '2024-01-15 14:30:00',
  preferences: {
    industry: ['互联网', '金融'],
    positionType: ['技术'],
    location: ['北京', '上海'],
    minSalary: 10000,
    maxSalary: 20000
  }
}

const userInfo = reactive({ ...mockUserInfo })

// 页面加载时获取用户详情
onMounted(() => {
  const userId = route.params.id
  if (userId) {
    fetchUserDetail(userId)
  }
})

// 获取用户详情
const fetchUserDetail = async (userId) => {
  try {
    // TODO: 调用真实API获取用户详情
    // const response = await userApi.searchUser({ userId })
    // Object.assign(userInfo, response.data)
  } catch (error) {
    ElMessage.error('获取用户详情失败：' + error.message)
  }
}

// 返回列表
const goBack = () => {
  router.push('/user/list')
}
</script>

<style scoped>
.user-detail-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.user-detail-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-sub-header {
  font-weight: bold;
}

.detail-content {
  padding: 20px 0;
}

.info-card {
  margin-bottom: 20px;
}

.info-section {
  display: flex;
  align-items: flex-start;
}

.avatar-section {
  margin-right: 40px;
}

.info-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.label {
  font-weight: bold;
  color: #606266;
  margin-bottom: 5px;
}

.value {
  color: #303133;
  font-size: 14px;
}

.role-tag,
.status-tag {
  margin-top: 5px;
}
</style>
