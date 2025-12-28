<template>
  <div class="forum-topic-management-container">
    <div class="page-content">
      <div class="page-header">
        <h2>论坛主题管理</h2>
        <div class="header-actions">
          <el-button type="primary" @click="handleAddTopic">
            <el-icon><Plus /></el-icon>新增主题
          </el-button>
          <el-button @click="handleBatchDelete" :disabled="selectedTopics.length === 0">
            <el-icon><Delete /></el-icon>批量删除
          </el-button>
        </div>
      </div>

      <!-- 筛选条件 -->
      <el-card class="filter-card">
        <el-form :model="filterForm" inline>
          <el-form-item label="主题标题">
            <el-input v-model="filterForm.topicTitle" placeholder="请输入主题标题" clearable />
          </el-form-item>
          <el-form-item label="板块ID">
            <el-input-number v-model="filterForm.sectionId" placeholder="请输入板块ID" :min="1" style="width: 150px" />
          </el-form-item>
          <el-form-item label="主题状态">
            <el-select v-model="filterForm.status" placeholder="请选择主题状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="启用" value="1" />
              <el-option label="禁用" value="0" />
              <el-option label="已删除" value="2" />
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
          <el-tag v-if="filterForm.topicTitle" closable @close="handleClearFilter('topicTitle')">
            主题标题: {{ filterForm.topicTitle }}
          </el-tag>
          <el-tag v-if="filterForm.sectionId" closable @close="handleClearFilter('sectionId')">
            板块ID: {{ filterForm.sectionId }}
          </el-tag>
          <el-tag v-if="filterForm.status" closable @close="handleClearFilter('status')">
            主题状态: {{ getStatusLabel(filterForm.status) }}
          </el-tag>
          <el-button type="text" size="small" @click="resetFilter">清除所有筛选</el-button>
        </span>
      </div>

      <!-- 主题列表 -->
      <el-card class="topic-list-card">
        <el-table
          v-loading="loading"
          :data="topics"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="topicId" label="主题ID" width="100" sortable />
          <el-table-column prop="topicTitle" label="主题标题" min-width="200" sortable />
          <el-table-column prop="sectionId" label="板块ID" width="120" />
          <el-table-column prop="topicAuthor" label="主题作者" width="120" />
          <el-table-column prop="viewCount" label="浏览次数" width="120" sortable />
          <el-table-column prop="replyCount" label="回复次数" width="120" sortable />
          <el-table-column prop="likeCount" label="点赞次数" width="120" sortable />
          <el-table-column prop="favoriteCount" label="收藏次数" width="120" sortable />
          <el-table-column prop="status" label="主题状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.status)">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" sortable />
          <el-table-column prop="updateTime" label="更新时间" width="180" sortable />
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
              <el-button size="small" type="primary" @click="viewTopicDetail(scope.row.topicId)">
                <el-icon><View /></el-icon>详情
              </el-button>
              <el-button size="small" @click="handleEditTopic(scope.row.topicId)">
                <el-icon><EditPen /></el-icon>编辑
              </el-button>
              <el-button size="small" type="danger" @click="handleDeleteTopic(scope.row.topicId)">
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Delete, View, EditPen } from '@element-plus/icons-vue';
import router from '@/router';
import forumApi from '@/api/modules/forum';

// 筛选表单
const filterForm = ref({
  topicTitle: '',
  sectionId: null,
  status: ''
});

// 是否有筛选条件
const isFilterActive = computed(() => {
  return Object.values(filterForm.value).some(value => value !== '' && value !== null);
});

// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);

// 主题列表数据
const topics = ref([]);

// 选中的主题
const selectedTopics = ref([]);

// 获取状态标签文本
const getStatusLabel = (status) => {
  switch (status) {
    case '1':
      return '启用';
    case '0':
      return '禁用';
    case '2':
      return '已删除';
    default:
      return '未知';
  }
};

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case '1':
      return 'success';
    case '0':
      return 'warning';
    case '2':
      return 'danger';
    default:
      return 'info';
  }
};

// 获取主题列表
const getTopicList = async () => {
  try {
    loading.value = true;
    const params = {
      ...filterForm.value,
      page: currentPage.value,
      pageSize: pageSize.value
    };
    const response = await forumApi.forumTopic.getTopicList(params);
    topics.value = response.data.records || response.data || [];
    total.value = response.data.total || 0;
  } catch (error) {
    ElMessage.error('获取主题列表失败: ' + (error.message || '未知错误'));
    topics.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 组件挂载时获取数据
onMounted(() => {
  getTopicList();
});

// 处理筛选
const handleFilter = () => {
  currentPage.value = 1;
  getTopicList();
};

// 重置筛选
const resetFilter = () => {
  filterForm.value = {
    topicTitle: '',
    sectionId: null,
    status: ''
  };
  currentPage.value = 1;
  getTopicList();
};

// 清除单个筛选条件
const handleClearFilter = (field) => {
  filterForm.value[field] = field === 'sectionId' ? null : '';
  currentPage.value = 1;
  getTopicList();
};

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedTopics.value = selection;
};

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  getTopicList();
};

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  getTopicList();
};

// 查看主题详情
const viewTopicDetail = (topicId) => {
  router.push(`/admin/forum-topic/detail/${topicId}`);
};

// 新增主题
const handleAddTopic = () => {
  router.push('/admin/forum-topic/form');
};

// 编辑主题
const handleEditTopic = (topicId) => {
  router.push(`/admin/forum-topic/form/${topicId}`);
};

// 删除主题
const handleDeleteTopic = (topicId) => {
  ElMessageBox.confirm('确定要删除该主题吗？此操作不可恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true;
      await forumApi.forumTopic.deleteTopic(topicId);
      ElMessage.success('删除主题成功');
      getTopicList();
    } catch (error) {
      ElMessage.error('删除主题失败: ' + (error.message || '未知错误'));
    } finally {
      loading.value = false;
    }
  }).catch(() => {
    ElMessage.info('已取消删除操作');
  });
};

// 批量删除主题
const handleBatchDelete = () => {
  if (selectedTopics.value.length === 0) {
    ElMessage.warning('请先选择要删除的主题');
    return;
  }
  
  ElMessageBox.confirm(`确定要删除选中的${selectedTopics.value.length}个主题吗？此操作不可恢复。`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true;
      const topicIds = selectedTopics.value.map(topic => topic.topicId);
      await forumApi.forumTopic.batchDeleteTopic(topicIds);
      ElMessage.success(`成功删除${selectedTopics.value.length}个主题`);
      selectedTopics.value = [];
      getTopicList();
    } catch (error) {
      ElMessage.error('批量删除主题失败: ' + (error.message || '未知错误'));
    } finally {
      loading.value = false;
    }
  }).catch(() => {
    ElMessage.info('已取消批量删除操作');
  });
};
</script>

<style scoped>
.forum-topic-management-container {
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

/* 主题列表卡片 */
.topic-list-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
}

/* 表格 */
.topic-list-card .el-table {
  margin-bottom: 0;
  border-radius: 8px 8px 0 0;
}

.topic-list-card .el-table__header-wrapper th {
  background-color: #f5f7fa;
  font-weight: bold;
  color: #333;
}

.topic-list-card .el-table__body-wrapper tr:hover > td {
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
  .filter-card .el-form-item .el-select,
  .filter-card .el-form-item .el-input-number {
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