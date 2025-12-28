<template>
  <div class="event-management-container">
    <div class="page-content">
      <div class="page-header">
        <h2>事件管理</h2>
        <div class="header-actions">
          <el-button type="primary" @click="handleAddEvent">
            <el-icon><Plus /></el-icon>新增事件
          </el-button>
          <el-button @click="handleBatchDelete" :disabled="selectedEvents.length === 0">
            <el-icon><Delete /></el-icon>批量删除
          </el-button>
        </div>
      </div>

      <!-- 筛选条件 -->
      <el-card class="filter-card">
        <el-form :model="filterForm" inline>
          <el-form-item label="事件名称">
            <el-input v-model="filterForm.eventName" placeholder="请输入事件名称" clearable />
          </el-form-item>
          <el-form-item label="事件类型">
            <el-select v-model="filterForm.eventType" placeholder="请选择事件类型" clearable>
              <el-option label="全部" value="" />
              <el-option label="广告" value="advertisement" />
              <el-option label="通知" value="notification" />
              <el-option label="活动" value="activity" />
            </el-select>
          </el-form-item>
          <el-form-item label="展示位置">
            <el-select v-model="filterForm.displayPosition" placeholder="请选择展示位置" clearable>
              <el-option label="全部" value="" />
              <el-option label="首页轮播图" value="home_carousel" />
              <el-option label="侧边栏广告" value="sidebar_ad" />
              <el-option label="活动推荐" value="activity_recommend" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="启用" value="1" />
              <el-option label="禁用" value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleFilter">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 快速筛选区域 -->
        <div class="quick-filters">
          <div class="filter-group">
            <span class="filter-label">事件类型：</span>
            <el-tag
              v-for="type in eventTypes"
              :key="type.value"
              :type="filterForm.eventType === type.value ? 'primary' : ''"
              :closable="filterForm.eventType === type.value"
              @click="handleQuickFilter('eventType', type.value)"
              @close="handleQuickFilter('eventType', '')"
            >
              {{ type.label }}
            </el-tag>
          </div>
          <div class="filter-group">
            <span class="filter-label">展示位置：</span>
            <el-tag
              v-for="position in displayPositions"
              :key="position.value"
              :type="filterForm.displayPosition === position.value ? 'primary' : ''"
              :closable="filterForm.displayPosition === position.value"
              @click="handleQuickFilter('displayPosition', position.value)"
              @close="handleQuickFilter('displayPosition', '')"
            >
              {{ position.label }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <!-- 筛选结果统计 -->
      <div class="filter-result">
        <span>共 {{ total }} 条记录</span>
        <span v-if="isFilterActive" class="filter-conditions">
          <el-tag v-if="filterForm.eventName" closable @close="handleClearFilter('eventName')">
            事件名称: {{ filterForm.eventName }}
          </el-tag>
          <el-tag v-if="filterForm.eventType" closable @close="handleClearFilter('eventType')">
            事件类型: {{ getEventTypeLabel(filterForm.eventType) }}
          </el-tag>
          <el-tag v-if="filterForm.displayPosition" closable @close="handleClearFilter('displayPosition')">
            展示位置: {{ getDisplayPositionLabel(filterForm.displayPosition) }}
          </el-tag>
          <el-tag v-if="filterForm.status" closable @close="handleClearFilter('status')">
            状态: {{ filterForm.status === '1' ? '启用' : '禁用' }}
          </el-tag>
          <el-button type="text" size="small" @click="resetFilter">清除所有筛选</el-button>
        </span>
      </div>

      <!-- 事件列表 -->
      <el-card class="event-list-card">
        <el-table
          v-loading="loading"
          :data="events"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="eventId" label="事件ID" width="100" sortable />
          <el-table-column prop="eventName" label="事件名称" min-width="150" sortable />
          <el-table-column prop="eventType" label="事件类型" width="120">
            <template #default="scope">
              <el-tag :type="getEventTypeTagType(scope.row.eventType)">
                {{ getEventTypeLabel(scope.row.eventType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="displayPosition" label="展示位置" width="150">
            <template #default="scope">
              <el-tag :type="getDisplayPositionTagType(scope.row.displayPosition)">
                {{ getDisplayPositionLabel(scope.row.displayPosition) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="180" sortable />
          <el-table-column prop="endTime" label="结束时间" width="180" sortable />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
                {{ scope.row.status === '1' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" sortable />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button size="small" type="primary" @click="handleEditEvent(scope.row)">
                <el-icon><EditPen /></el-icon>编辑
              </el-button>
              <el-button size="small" type="danger" @click="handleDeleteEvent(scope.row.eventId)">
                <el-icon><Delete /></el-icon>删除
              </el-button>
              <el-switch
                v-model="scope.row.status"
                active-value="1"
                inactive-value="0"
                @change="handleStatusChange(scope.row)"
                inline-prompt
                active-text="启用"
                inactive-text="禁用"
                style="margin-left: 10px"
              />
            </template>
          </el-table-column>
        </el-table>

        <!-- 批量操作和分页 -->
        <div class="table-footer">
          <div class="batch-operations">
            <span v-if="selectedEvents.length > 0" class="selected-count">
              已选择 {{ selectedEvents.length }} 项
            </span>
          </div>
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </el-card>
    </div>
  </div>

  <!-- 事件创建/编辑弹窗 -->
  <el-dialog
    v-model="eventDialogVisible"
    :title="eventDialogTitle"
    width="600px"
    destroy-on-close
  >
    <el-form
      ref="eventFormRef"
      :model="eventForm"
      :rules="eventRules"
      label-width="100px"
      class="event-form"
    >
      <el-form-item label="事件名称" prop="eventName">
        <el-input v-model="eventForm.eventName" placeholder="请输入事件名称" />
      </el-form-item>
      <el-form-item label="事件类型" prop="eventType">
        <el-select v-model="eventForm.eventType" placeholder="请选择事件类型">
          <el-option label="广告" value="advertisement" />
          <el-option label="通知" value="notification" />
          <el-option label="活动" value="activity" />
        </el-select>
      </el-form-item>
      <el-form-item label="展示位置" prop="displayPosition">
        <el-select v-model="eventForm.displayPosition" placeholder="请选择展示位置">
          <el-option label="首页轮播图" value="home_carousel" />
          <el-option label="侧边栏广告" value="sidebar_ad" />
          <el-option label="活动推荐" value="activity_recommend" />
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
          v-model="eventForm.startTime"
          type="datetime"
          placeholder="选择开始时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="eventForm.endTime"
          type="datetime"
          placeholder="选择结束时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="事件链接">
        <el-input v-model="eventForm.eventLink" placeholder="请输入事件链接" clearable />
      </el-form-item>
      <el-form-item label="事件图片" prop="eventImage">
        <el-upload
          class="event-uploader"
          :action="uploadUrl"
          :show-file-list="false"
          :on-success="handleImageUploadSuccess"
          :before-upload="handleImageBeforeUpload"
        >
          <img v-if="eventForm.eventImage" :src="eventForm.eventImage" class="event-image-preview" />
          <el-icon v-else class="upload-icon"><Plus /></el-icon>
          <div v-else class="upload-text">点击或拖拽上传图片</div>
        </el-upload>
        <div class="upload-hint">支持JPG、PNG格式，大小限制1MB</div>
      </el-form-item>
      <el-form-item label="事件内容">
        <el-input
          v-model="eventForm.eventContent"
          type="textarea"
          :rows="4"
          placeholder="请输入事件内容"
          clearable
        />
      </el-form-item>
      <el-form-item label="排序权重">
        <el-input-number
          v-model="eventForm.sortWeight"
          :min="0"
          :max="100"
          :step="1"
          placeholder="请输入排序权重"
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-switch
          v-model="eventForm.status"
          active-value="1"
          inactive-value="0"
          active-text="启用"
          inactive-text="禁用"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="eventDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEvent">保存</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, EditPen } from '@element-plus/icons-vue'
import eventApi from '@/api/modules/event'

// 筛选表单
const filterForm = ref({
  eventName: '',
  eventType: '',
  displayPosition: '',
  status: ''
})

// 是否有筛选条件
const isFilterActive = computed(() => {
  return Object.values(filterForm.value).some(value => value !== '')
})

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 事件列表数据
const events = ref([])

// 选中的事件
const selectedEvents = ref([])

// 事件类型列表
const eventTypes = ref([
  { value: 'advertisement', label: '广告' },
  { value: 'notification', label: '通知' },
  { value: 'activity', label: '活动' }
])

// 展示位置列表
const displayPositions = ref([
  { value: 'home_carousel', label: '首页轮播图' },
  { value: 'sidebar_ad', label: '侧边栏广告' },
  { value: 'activity_recommend', label: '活动推荐' }
])

// 事件类型标签类型映射
const eventTypeTagTypeMap = {
  'advertisement': 'primary',
  'notification': 'success',
  'activity': 'warning'
}

// 展示位置标签类型映射
const displayPositionTagTypeMap = {
  'home_carousel': 'primary',
  'sidebar_ad': 'success',
  'activity_recommend': 'warning'
}

// 事件创建/编辑弹窗
const eventDialogVisible = ref(false)
const eventDialogTitle = ref('新增事件')
const eventFormRef = ref(null)
const eventForm = ref({
  eventId: null,
  eventName: '',
  eventType: '',
  displayPosition: '',
  startTime: '',
  endTime: '',
  eventLink: '',
  eventImage: '',
  eventContent: '',
  sortWeight: 0,
  status: '1'
})

// 表单验证规则
const eventRules = ref({
  eventName: [{ required: true, message: '请输入事件名称', trigger: 'blur' }],
  eventType: [{ required: true, message: '请选择事件类型', trigger: 'change' }],
  displayPosition: [{ required: true, message: '请选择展示位置', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  eventImage: [{ required: true, message: '请上传事件图片', trigger: 'change' }]
})

// 上传地址
const uploadUrl = '/upload'

// 获取事件类型标签类型
const getEventTypeTagType = (type) => {
  return eventTypeTagTypeMap[type] || 'info'
}

// 获取事件类型标签文本
const getEventTypeLabel = (type) => {
  const found = eventTypes.value.find(item => item.value === type)
  return found ? found.label : '未知'
}

// 获取展示位置标签类型
const getDisplayPositionTagType = (position) => {
  return displayPositionTagTypeMap[position] || 'info'
}

// 获取展示位置标签文本
const getDisplayPositionLabel = (position) => {
  const found = displayPositions.value.find(item => item.value === position)
  return found ? found.label : '未知'
}

// 获取事件列表
const getEventList = async () => {
  try {
    loading.value = true
    const params = {
      ...filterForm.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    const response = await eventApi.getEventList(params)
    events.value = response.data.records || response.data || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取事件列表失败: ' + (error.message || '未知错误'))
    events.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  getEventList()
})

// 处理筛选
const handleFilter = () => {
  currentPage.value = 1
  getEventList()
}

// 重置筛选
const resetFilter = () => {
  filterForm.value = {
    eventName: '',
    eventType: '',
    displayPosition: '',
    status: ''
  }
  currentPage.value = 1
  getEventList()
}

// 清除单个筛选条件
const handleClearFilter = (field) => {
  filterForm.value[field] = ''
  currentPage.value = 1
  getEventList()
}

// 快速筛选
const handleQuickFilter = (field, value) => {
  filterForm.value[field] = value
  currentPage.value = 1
  getEventList()
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedEvents.value = selection
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getEventList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  getEventList()
}

// 新增事件
const handleAddEvent = () => {
  eventDialogTitle.value = '新增事件'
  eventForm.value = {
    eventId: null,
    eventName: '',
    eventType: '',
    displayPosition: '',
    startTime: '',
    endTime: '',
    eventLink: '',
    eventImage: '',
    eventContent: '',
    sortWeight: 0,
    status: '1'
  }
  eventDialogVisible.value = true
}

// 编辑事件
const handleEditEvent = (row) => {
  eventDialogTitle.value = '编辑事件'
  eventForm.value = {
    ...row
  }
  eventDialogVisible.value = true
}

// 删除事件
const handleDeleteEvent = (eventId) => {
  ElMessageBox.confirm('确定要删除该事件吗？此操作不可恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true
      await eventApi.deleteEvent([eventId])
      ElMessage.success('删除事件成功')
      getEventList()
    } catch (error) {
      ElMessage.error('删除事件失败: ' + (error.message || '未知错误'))
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消删除操作')
  })
}

// 批量删除事件
const handleBatchDelete = () => {
  if (selectedEvents.value.length === 0) {
    ElMessage.warning('请先选择要删除的事件')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的${selectedEvents.value.length}个事件吗？此操作不可恢复。`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true
      const eventIds = selectedEvents.value.map(event => event.eventId)
      await eventApi.deleteEvent(eventIds)
      ElMessage.success(`成功删除${selectedEvents.value.length}个事件`)
      selectedEvents.value = []
      getEventList()
    } catch (error) {
      ElMessage.error('批量删除事件失败: ' + (error.message || '未知错误'))
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消批量删除操作')
  })
}

// 保存事件
const handleSaveEvent = async () => {
  if (!eventFormRef.value) return
  
  await eventFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        let response
        if (eventForm.value.eventId) {
          response = await eventApi.updateEvent(eventForm.value)
        } else {
          response = await eventApi.insertEvent(eventForm.value)
        }
        if (response && response.code === 200) {
          ElMessage.success(eventForm.value.eventId ? '事件更新成功' : '事件新增成功')
          eventDialogVisible.value = false
          getEventList()
        } else {
          ElMessage.error(eventForm.value.eventId ? '事件更新失败' : '事件新增失败')
        }
      } catch (error) {
        ElMessage.error(eventForm.value.eventId ? '事件更新失败' : '事件新增失败')
        console.error('保存事件失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 处理状态变化
const handleStatusChange = async (row) => {
  try {
    loading.value = true
    await eventApi.updateEvent({
      eventId: row.eventId,
      status: row.status
    })
    ElMessage.success('状态更新成功')
  } catch (error) {
    // 回滚状态
    row.status = row.status === '1' ? '0' : '1'
    ElMessage.error('状态更新失败')
    console.error('更新状态失败:', error)
  } finally {
    loading.value = false
  }
}

// 图片上传成功处理
const handleImageUploadSuccess = (response) => {
  if (response && response.code === 200) {
    eventForm.value.eventImage = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败')
  }
}

// 图片上传前验证
const handleImageBeforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('请上传图片文件')
    return false
  }
  const isLt1M = file.size / 1024 / 1024 < 1
  if (!isLt1M) {
    ElMessage.error('图片大小不能超过 1MB')
    return false
  }
  return true
}
</script>

<style scoped>
.event-management-container {
  padding: 0;
  margin: 0;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-content {
  padding: 20px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  padding: 15px;
}

.filter-card .el-form-item {
  margin-right: 20px;
  margin-bottom: 15px;
}

/* 快速筛选区域 */
.quick-filters {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e0e0e0;
}

.filter-group {
  margin-bottom: 10px;
}

.filter-label {
  font-size: 14px;
  color: #666;
  margin-right: 10px;
}

.filter-group .el-tag {
  margin-right: 10px;
  cursor: pointer;
  margin-bottom: 8px;
}

/* 筛选结果统计 */
.filter-result {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 12px 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #666;
}

.filter-conditions {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-conditions .el-tag {
  margin: 0;
}

.filter-conditions .el-button {
  padding: 4px 12px;
  font-size: 12px;
  color: #409eff;
}

/* 事件列表卡片 */
.event-list-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
}

/* 表格 */
.event-list-card .el-table {
  margin-bottom: 0;
  border-radius: 8px 8px 0 0;
}

.event-list-card .el-table__header-wrapper th {
  background-color: #f5f7fa;
  font-weight: bold;
  color: #333;
}

.event-list-card .el-table__body-wrapper tr:hover > td {
  background-color: #f5f7fa;
}

/* 表格底部 */
.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #fafafa;
  border-top: 1px solid #e0e0e0;
}

/* 批量操作 */
.batch-operations {
  display: flex;
  align-items: center;
  gap: 15px;
}

.selected-count {
  color: #666;
  font-size: 14px;
}

/* 分页 */
.pagination-container {
  margin: 0;
}

/* 事件表单 */
.event-form .el-form-item {
  margin-bottom: 20px;
}

/* 事件图片上传 */
.event-uploader {
  width: 200px;
  height: 150px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  background-color: #fafafa;
  position: relative;
  overflow: hidden;
}

.event-uploader:hover {
  border-color: #409eff;
  background-color: rgba(64, 158, 255, 0.05);
}

.upload-icon {
  font-size: 32px;
  color: #909399;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  color: #909399;
}

.event-image-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-hint {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-content {
    padding: 15px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: space-between;
    flex-wrap: wrap;
  }
  
  .filter-card .el-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-card .el-form-item {
    margin-right: 0;
    width: 100%;
  }
  
  .filter-card .el-form-item .el-input,
  .filter-card .el-form-item .el-select {
    width: 100%;
  }
  
  .filter-result {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .filter-conditions {
    width: 100%;
  }
  
  .table-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .batch-operations {
    width: 100%;
    justify-content: space-between;
  }
  
  .pagination-container {
    width: 100%;
    justify-content: center;
  }
}
</style>