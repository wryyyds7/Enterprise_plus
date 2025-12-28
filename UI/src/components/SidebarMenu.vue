<template>
  <div class="sidebar-container">
    <!-- 侧边栏折叠按钮 -->
    <div class="collapse-button" @click="toggleCollapse">
      <el-icon>{{ isCollapsed ? ArrowRight : ArrowLeft }}</el-icon>
    </div>
    
    <!-- 侧边栏导航菜单 -->
    <el-menu
      :default-active="activePath"
      :collapse="isCollapsed"
      :router="true"
      class="sidebar-menu"
      text-color="#303133"
      active-text-color="#409EFF"
      unique-opened
      @select="handleSelect"
      @open="handleOpen"
      @close="handleClose"
    >
      <!-- 首页 -->
      <el-menu-item index="/admin/dashboard">
        <el-icon><House /></el-icon>
        <span>首页</span>
      </el-menu-item>
      
      <!-- 企业管理 -->
      <el-sub-menu index="/admin/enterprise">
        <template #title>
          <el-icon><OfficeBuilding /></el-icon>
          <span>企业管理</span>
        </template>
        <el-menu-item index="/admin/enterprise/list">企业列表</el-menu-item>
        <el-menu-item index="/admin/enterprise/create">创建企业</el-menu-item>
        <el-menu-item index="/admin/enterprise/approval">企业审批</el-menu-item>
      </el-sub-menu>
      
      <!-- 职位管理 -->
      <el-sub-menu index="/admin/position">
        <template #title>
          <el-icon><Position /></el-icon>
          <span>职位管理</span>
        </template>
        <el-menu-item index="/admin/position/list">职位列表</el-menu-item>
        <el-menu-item index="/admin/position/create">发布职位</el-menu-item>
      </el-sub-menu>
      
      <!-- 用户管理 -->
      <el-sub-menu index="/admin/user">
        <template #title>
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </template>
        <el-menu-item index="/admin/user/list">用户列表</el-menu-item>
        <el-menu-item index="/admin/user/create">创建用户</el-menu-item>
        <el-menu-item index="/admin/user/register">注册管理员</el-menu-item>
      </el-sub-menu>
      
      <!-- 活动管理 -->
      <el-sub-menu index="/admin/activity">
        <template #title>
          <el-icon><Calendar /></el-icon>
          <span>活动管理</span>
        </template>
        <el-menu-item index="/admin/activity/list">活动列表</el-menu-item>
        <el-menu-item index="/admin/activity/create">创建活动</el-menu-item>
      </el-sub-menu>
      
      <!-- 文档管理 -->
      <el-menu-item index="/admin/documentation">
        <el-icon><Document /></el-icon>
        <span>文档管理</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowRight, ArrowLeft, House, OfficeBuilding, Position, User, Calendar, Document } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 侧边栏折叠状态
const isCollapsed = ref(false)

// 当前激活的路径
const activePath = computed(() => {
  return route.path
})

// 切换折叠状态
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

// 菜单项选择事件
const handleSelect = (key, keyPath) => {
  console.log('Menu selected:', key, keyPath)
}

// 子菜单展开事件
const handleOpen = (key, keyPath) => {
  console.log('Submenu opened:', key, keyPath)
}

// 子菜单关闭事件
const handleClose = (key, keyPath) => {
  console.log('Submenu closed:', key, keyPath)
}
</script>

<style scoped>
.sidebar-container {
  position: relative;
  height: 100vh;
  background-color: #f5f7fa;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  width: 200px;
  overflow: hidden;
}

.sidebar-container:deep(.is-collapsed) {
  width: 64px;
}

.collapse-button {
  position: absolute;
  right: -10px;
  top: 20px;
  width: 20px;
  height: 36px;
  background-color: #409eff;
  color: white;
  border-radius: 0 18px 18px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.collapse-button:hover {
  background-color: #66b1ff;
  transform: translateX(2px);
}

.sidebar-menu {
  height: 100%;
  border-right: none;
  background-color: transparent;
}

:deep(.el-menu-item.is-active) {
  background-color: #ecf5ff !important;
  color: #409eff !important;
}

:deep(.el-sub-menu__title.is-active) {
  background-color: #ecf5ff !important;
  color: #409eff !important;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #f0f9ff !important;
  color: #409eff !important;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
  transition: all 0.3s ease;
}

:deep(.el-sub-menu .el-menu-item) {
  padding-left: 50px !important;
}

:deep(.el-sub-menu.is-active > .el-sub-menu__title) {
  background-color: #ecf5ff !important;
}

:deep(.el-menu--collapse .el-sub-menu .el-menu-item) {
  padding-left: 10px !important;
}

:deep(.el-menu--collapse .el-sub-menu__icon-arrow) {
  display: none;
}
</style>