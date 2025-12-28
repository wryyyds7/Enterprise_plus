<template>
  <div class="forum-section-management-container">
    <div class="page-content">
      <div class="page-header">
        <h2>论坛板块管理</h2>
        <div class="header-actions">
          <el-button type="primary" @click="handleAddSection">
            <el-icon><Plus /></el-icon>新增板块
          </el-button>
          <el-button @click="handleBatchDelete" :disabled="selectedSections.length === 0">
            <el-icon><Delete /></el-icon>批量删除
          </el-button>
        </div>
      </div>

      <!-- 筛选条件 -->
      <el-card class="filter-card">
        <el-form :model="filterForm" inline>
          <el-form-item label="板块名称">
            <el-input v-model="filterForm.sectionName" placeholder="请输入板块名称" clearable />
          </el-form-item>
          <el-form-item label="板块状态">
            <el-select v-model="filterForm.status" placeholder="请选择板块状态" clearable>
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
          <el-tag v-if="filterForm.sectionName" closable @close="handleClearFilter('sectionName')">
            板块名称: {{ filterForm.sectionName }}
          </el-tag>
          <el-tag v-if="filterForm.status" closable @close="handleClearFilter('status')">
            板块状态: {{ filterForm.status === '1' ? '启用' : '禁用' }}
          </el-tag>
          <el-button type="text" size="small" @click="resetFilter">清除所有筛选</el-button>
        </span>
      </div>

      <!-- 板块列表 -->
      <el-card class="section-list-card">
        <el-table
          v-loading="loading"
          :data="sections"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="sectionId" label="板块ID" width="100" sortable />
          <el-table-column prop="sectionName" label="板块名称" min-width="150" sortable />
          <el-table-column prop="sectionDescription" label="板块描述" min-width="200" />
          <el-table-column prop="parentSectionId" label="父板块ID" width="120" />
          <el-table-column prop="sectionOrder" label="排序权重" width="120" sortable />
          <el-table-column prop="topicCount" label="主题数量" width="120" />
          <el-table-column prop="status" label="板块状态" width="120">
            <template #default="scope">
              <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
                {{ scope.row.status === '1' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" sortable />
          <el-table-column prop="updateTime" label="更新时间" width="180" sortable />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button size="small" type="primary" @click="handleEditSection(scope.row.sectionId)">
                <el-icon><EditPen /></el-icon>编辑
              </el-button>
              <el-button size="small" type="danger" @click="handleDeleteSection(scope.row.sectionId)">
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Delete, EditPen } from '@element-plus/icons-vue';
import router from '@/router';
import forumApi from '@/api/modules/forum';

// 筛选表单
const filterForm = ref({
  sectionName: '',
  status: ''
});

// 是否有筛选条件
const isFilterActive = computed(() => {
  return Object.values(filterForm.value).some(value => value !== '');
});

// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);

// 板块列表数据
const sections = ref([]);

// 选中的板块
const selectedSections = ref([]);

// 获取板块列表
const getSectionList = async () => {
  try {
    loading.value = true;
    const params = {
      ...filterForm.value,
      page: currentPage.value,
      pageSize: pageSize.value
    };
    const response = await forumApi.forumSection.getSectionList(params);
    sections.value = response.data.records || response.data || [];
    total.value = response.data.total || 0;
  } catch (error) {
    ElMessage.error('获取板块列表失败: ' + (error.message || '未知错误'));
    sections.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 组件挂载时获取数据
onMounted(() => {
  getSectionList();
});

// 处理筛选
const handleFilter = () => {
  currentPage.value = 1;
  getSectionList();
};

// 重置筛选
const resetFilter = () => {
  filterForm.value = {
    sectionName: '',
    status: ''
  };
  currentPage.value = 1;
  getSectionList();
};

// 清除单个筛选条件
const handleClearFilter = (field) => {
  filterForm.value[field] = '';
  currentPage.value = 1;
  getSectionList();
};

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedSections.value = selection;
};

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  getSectionList();
};

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  getSectionList();
};

// 新增板块
const handleAddSection = () => {
  router.push('/admin/forum-section/form');
};

// 编辑板块
const handleEditSection = (sectionId) => {
  router.push(`/admin/forum-section/form/${sectionId}`);
};

// 删除板块
const handleDeleteSection = (sectionId) => {
  ElMessageBox.confirm('确定要删除该板块吗？此操作不可恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true;
      await forumApi.forumSection.deleteSection(sectionId);
      ElMessage.success('删除板块成功');
      getSectionList();
    } catch (error) {
      ElMessage.error('删除板块失败: ' + (error.message || '未知错误'));
    } finally {
      loading.value = false;
    }
  }).catch(() => {
    ElMessage.info('已取消删除操作');
  });
};

// 批量删除板块
const handleBatchDelete = () => {
  if (selectedSections.value.length === 0) {
    ElMessage.warning('请先选择要删除的板块');
    return;
  }
  
  ElMessageBox.confirm(`确定要删除选中的${selectedSections.value.length}个板块吗？此操作不可恢复。`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true;
      const sectionIds = selectedSections.value.map(section => section.sectionId);
      await forumApi.forumSection.batchDeleteSection(sectionIds);
      ElMessage.success(`成功删除${selectedSections.value.length}个板块`);
      selectedSections.value = [];
      getSectionList();
    } catch (error) {
      ElMessage.error('批量删除板块失败: ' + (error.message || '未知错误'));
    } finally {
      loading.value = false;
    }
  }).catch(() => {
    ElMessage.info('已取消批量删除操作');
  });
};

// 处理状态变化
const handleStatusChange = async (row) => {
  try {
    loading.value = true;
    await forumApi.forumSection.updateSection({
      sectionId: row.sectionId,
      status: row.status
    });
    ElMessage.success('状态更新成功');
  } catch (error) {
    // 回滚状态
    row.status = row.status === '1' ? '0' : '1';
    ElMessage.error('状态更新失败');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.forum-section-management-container {
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
  border-radius: 8px;
  overflow: hidden;
}

.filter-card .el-form {
  padding: 10px;
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

/* 板块列表卡片 */
.section-list-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
}

/* 表格 */
.section-list-card .el-table {
  margin-bottom: 0;
  border-radius: 8px 8px 0 0;
}

.section-list-card .el-table__header-wrapper th {
  background-color: #f5f7fa;
  font-weight: bold;
  color: #333;
}

.section-list-card .el-table__body-wrapper tr:hover > td {
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
}
</style>