<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/ai')
}
</script>

<template>
  <div class="error-container">
    <div class="error-content">
      <div class="error-code">404</div>
      <div class="error-message">抱歉，您访问的页面不存在</div>
      <el-button type="primary" @click="goHome" class="error-button">返回首页</el-button>
    </div>
  </div>
</template>

<style scoped>
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.error-content {
  text-align: center;
  padding: 40px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.error-code {
  font-size: 80px;
  font-weight: bold;
  color: #646cff;
  margin-bottom: 20px;
}

.error-message {
  font-size: 20px;
  color: #606266;
  margin-bottom: 30px;
}

.error-button {
  font-size: 16px;
  padding: 10px 24px;
}
</style>