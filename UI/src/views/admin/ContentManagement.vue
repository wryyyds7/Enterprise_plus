<template>
  <div class="content-management">
    <div class="page-header">
      <h2>活动管理</h2>
      <p>管理平台上的活动信息</p>
    </div>

    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <div class="header-actions">
        <el-button type="primary" @click="handleAddActivity">
          <el-icon><Plus /></el-icon>新增活动
        </el-button>
        <el-button @click="handleBatchDelete" :disabled="selectedActivities.length === 0">
          <el-icon><Delete /></el-icon>批量删除
        </el-button>
      </div>
      <el-form :model="filterForm" inline>
        <el-form-item label="活动名称">
          <el-input v-model="filterForm.activityName" placeholder="请输入活动名称" clearable />
        </el-form-item>
        <el-form-item label="活动时间">
          <el-date-picker
            v-model="filterForm.activityTime"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
          />
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
    </el-card>

    <!-- 筛选结果统计 -->
    <div class="filter-result">
      <span>共 {{ total }} 条记录</span>
      <span v-if="isFilterActive" class="filter-conditions">
        <el-tag v-if="filterForm.activityName" closable @close="handleClearFilter('activityName')">
          活动名称: {{ filterForm.activityName }}
        </el-tag>
        <el-tag v-if="filterForm.status" closable @close="handleClearFilter('status')">
          状态: {{ filterForm.status === '1' ? '启用' : '禁用' }}
        </el-tag>
        <el-button type="text" size="small" @click="resetFilter">清除所有筛选</el-button>
      </span>
    </div>

    <!-- 活动列表 -->
    <el-card class="activity-list-card">
      <el-table
        v-loading="loading"
        :data="activities"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="activityId" label="活动ID" width="100" sortable />
        <el-table-column prop="activityName" label="活动名称" min-width="200" sortable />
        <el-table-column prop="activityType" label="活动类型" width="120">
          <template #default="scope">
            <el-tag :type="getActivityTypeTagType(scope.row.activityType)">
              {{ getActivityTypeLabel(scope.row.activityType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="200" sortable />
        <el-table-column prop="endTime" label="结束时间" width="200" sortable />
        <el-table-column prop="location" label="活动地点" min-width="150" />
        <el-table-column prop="maxParticipants" label="最大参与人数" width="120" />
        <el-table-column prop="currentParticipants" label="当前参与人数" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
              {{ scope.row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="200" sortable />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleEditActivity(scope.row.activityId)">
              <el-icon><EditPen /></el-icon>编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDeleteActivity(scope.row.activityId)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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
    </el-card>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Delete, EditPen } from '@element-plus/icons-vue';
import router from '@/router';
import activityApi from '@/api/modules/activity';

// 筛选表单
const filterForm = ref({
  activityName: '',
  activityTime: [],
  status: ''
});

// 是否有筛选条件
const isFilterActive = computed(() => {
  return filterForm.value.activityName !== '' || filterForm.value.status !== '' || filterForm.value.activityTime.length > 0;
});

// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);

// 活动列表数据
const activities = ref([]);

// 选中的活动
const selectedActivities = ref([]);

// 活动类型标签映射
const activityTypeMap = {
  '1': { label: '宣讲会', type: 'primary' },
  '2': { label: '双选会', type: 'success' },
  '3': { label: '线上活动', type: 'warning' },
  '4': { label: '线下活动', type: 'info' }
};

// 获取活动类型标签类型
const getActivityTypeTagType = (type) => {
  return activityTypeMap[type]?.type || 'info';
};

// 获取活动类型标签文本
const getActivityTypeLabel = (type) => {
  return activityTypeMap[type]?.label || '未知';
};

// 获取活动列表
const getActivityList = async () => {
  try {
    loading.value = true;
    const params = {
      ...filterForm.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    };
    // 处理时间范围
    if (filterForm.value.activityTime && filterForm.value.activityTime.length === 2) {
      params.startTime = filterForm.value.activityTime[0];
      params.endTime = filterForm.value.activityTime[1];
    }
    const response = await activityApi.getActivityList(params);
    activities.value = response.data.records || response.data || [];
    total.value = response.data.total || 0;
  } catch (error) {
    ElMessage.error('获取活动列表失败: ' + (error.message || '未知错误'));
    activities.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 组件挂载时获取数据
onMounted(() => {
  getActivityList();
});

// 处理筛选
const handleFilter = () => {
  currentPage.value = 1;
  getActivityList();
};

// 重置筛选
const resetFilter = () => {
  filterForm.value = {
    activityName: '',
    activityTime: [],
    status: ''
  };
  currentPage.value = 1;
  getActivityList();
};

// 清除单个筛选条件
const handleClearFilter = (field) => {
  if (field === 'activityTime') {
    filterForm.value.activityTime = [];
  } else {
    filterForm.value[field] = '';
  }
  currentPage.value = 1;
  getActivityList();
};

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedActivities.value = selection;
};

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  getActivityList();
};

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  getActivityList();
};

// 新增活动
const handleAddActivity = () => {
  router.push('/admin/activity/form');
};

// 编辑活动
const handleEditActivity = (activityId) => {
  router.push(`/admin/activity/form/${activityId}`);
};

// 删除活动
const handleDeleteActivity = (activityId) => {
  ElMessageBox.confirm('确定要删除该活动吗？此操作不可恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true;
      await activityApi.deleteActivity([activityId]);
      ElMessage.success('删除活动成功');
      getActivityList();
    } catch (error) {
      ElMessage.error('删除活动失败: ' + (error.message || '未知错误'));
    } finally {
      loading.value = false;
    }
  }).catch(() => {
    ElMessage.info('已取消删除操作');
  });
};

// 批量删除活动
const handleBatchDelete = () => {
  if (selectedActivities.value.length === 0) {
    ElMessage.warning('请先选择要删除的活动');
    return;
  }
  
  ElMessageBox.confirm(`确定要删除选中的${selectedActivities.value.length}个活动吗？此操作不可恢复。`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true;
      const activityIds = selectedActivities.value.map(activity => activity.activityId);
      await activityApi.deleteActivity(activityIds);
      ElMessage.success(`成功删除${selectedActivities.value.length}个活动`);
      selectedActivities.value = [];
      getActivityList();
    } catch (error) {
      ElMessage.error('批量删除活动失败: ' + (error.message || '未知错误'));
    } finally {
      loading.value = false;
    }
  }).catch(() => {
    ElMessage.info('已取消批量删除操作');
  });
};
</script>

<style scoped>
.content-management {
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

.page-header p {
  margin: 5px 0 0 0;
  color: #666;
  font-size: 14px;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.header-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.filter-card .el-form-item {
  margin-right: 20px;
  margin-bottom: 15px;
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

/* 活动列表卡片 */
.activity-list-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
}

/* 表格 */
.activity-list-card .el-table {
  margin-bottom: 20px;
}

.activity-list-card .el-table__header-wrapper th {
  background-color: #f5f7fa;
  font-weight: bold;
  color: #333;
}

.activity-list-card .el-table__body-wrapper tr:hover > td {
  background-color: #f5f7fa;
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
  margin-top: 20px;
  text-align: right;
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
  .filter-card .el-form-item .el-select,
  .filter-card .el-form-item .el-date-picker {
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
}
</style>