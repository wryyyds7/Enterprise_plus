<template>
  <div class="forum-reply-management-container">
    <div class="page-content">
      <div class="page-header">
        <h2>论坛回复管理</h2>
        <div class="header-actions">
          <el-button @click="handleBatchDelete" :disabled="selectedReplies.length === 0">
            <el-icon><Delete /></el-icon>批量删除
          </el-button>
        </div>
      </div>

      <!-- 筛选条件 -->
      <el-card class="filter-card">
        <el-form :model="filterForm" inline>
          <el-form-item label="主题ID">
            <el-input-number v-model="filterForm.topicId" placeholder="请输入主题ID" :min="1" style="width: 150px" />
          </el-form-item>
          <el-form-item label="回复内容">
            <el-input v-model="filterForm.replyContent" placeholder="请输入回复内容" clearable />
          </el-form-item>
          <el-form-item label="回复作者">
            <el-input v-model="filterForm.replyAuthor" placeholder="请输入回复作者" clearable />
          </el-form-item>
          <el-form-item label="回复状态">
            <el-select v-model="filterForm.status" placeholder="请选择回复状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="正常" value="1" />
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
          <el-tag v-if="filterForm.topicId" closable @close="handleClearFilter('topicId')">
            主题ID: {{ filterForm.topicId }}
          </el-tag>
          <el-tag v-if="filterForm.replyContent" closable @close="handleClearFilter('replyContent')">
            回复内容: {{ filterForm.replyContent }}
          </el-tag>
          <el-tag v-if="filterForm.replyAuthor" closable @close="handleClearFilter('replyAuthor')">
            回复作者: {{ filterForm.replyAuthor }}
          </el-tag>
          <el-tag v-if="filterForm.status" closable @close="handleClearFilter('status')">
            回复状态: {{ getStatusLabel(filterForm.status) }}
          </el-tag>
          <el-button type="text" size="small" @click="resetFilter">清除所有筛选</el-button>
        </span>
      </div>

      <!-- 回复列表 -->
      <el-card class="reply-list-card">
        <el-table
          v-loading="loading"
          :data="replies"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="replyId" label="回复ID" width="100" sortable />
          <el-table-column prop="topicId" label="主题ID" width="120" />
          <el-table-column prop="replyContent" label="回复内容" min-width="200" />
          <el-table-column prop="replyAuthor" label="回复作者" width="120" />
          <el-table-column prop="parentReplyId" label="父回复ID" width="120" />
          <el-table-column prop="likeCount" label="点赞次数" width="120" sortable />
          <el-table-column prop="status" label="回复状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.status)">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" sortable />
          <el-table-column prop="updateTime" label="更新时间" width="180" sortable />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <el-button size="small" type="danger" @click="handleDeleteReply(scope.row.replyId)">
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
import { Delete, EditPen, View } from '@element-plus/icons-vue';
import router from '@/router';
import forumApi from '@/api/modules/forum';

// 筛选表单
const filterForm = ref({
  topicId: null,
  replyContent: '',
  replyAuthor: '',
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

// 回复列表数据
const replies = ref([]);

// 选中的回复
const selectedReplies = ref([]);

// 获取状态标签文本
const getStatusLabel = (status) => {
  switch (status) {
    case '1':
      return '正常';
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
    case '2':
      return 'danger';
    default:
      return 'info';
  }
};

// 获取回复列表
const getReplyList = async () => {
  try {
    loading.value = true;
    const params = {
      ...filterForm.value,
      page: currentPage.value,
      pageSize: pageSize.value
    };
    const response = await forumApi.forumReply.getReplyList(params);
    replies.value = response.data.records || response.data || [];
    total.value = response.data.total || 0;
  } catch (error) {
    ElMessage.error('获取回复列表失败: ' + (error.message || '未知错误'));
    replies.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 组件挂载时获取数据
onMounted(() => {
  getReplyList();
});

// 处理筛选
const handleFilter = () => {
  currentPage.value = 1;
  getReplyList();
};

// 重置筛选
const resetFilter = () => {
  filterForm.value = {
    topicId: null,
    replyContent: '',
    replyAuthor: '',
    status: ''
  };
  currentPage.value = 1;
  getReplyList();
};

// 清除单个筛选条件
const handleClearFilter = (field) => {
  filterForm.value[field] = field === 'topicId' ? null : '';
  currentPage.value = 1;
  getReplyList();
};

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedReplies.value = selection;
};

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  getReplyList();
};

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  getReplyList();
};

// 删除回复
const handleDeleteReply = (replyId) => {
  ElMessageBox.confirm('确定要删除该回复吗？此操作不可恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true;
      await forumApi.forumReply.deleteReply(replyId);
      ElMessage.success('删除回复成功');
      getReplyList();
    } catch (error) {
      ElMessage.error('删除回复失败: ' + (error.message || '未知错误'));
    } finally {
      loading.value = false;
    }
  }).catch(() => {
    ElMessage.info('已取消删除操作');
  });
};

// 批量删除回复
const handleBatchDelete = () => {
  if (selectedReplies.value.length === 0) {
    ElMessage.warning('请先选择要删除的回复');
    return;
  }
  
  ElMessageBox.confirm(`确定要删除选中的${selectedReplies.value.length}条回复吗？此操作不可恢复。`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      loading.value = true;
      const replyIds = selectedReplies.value.map(reply => reply.replyId);
      await forumApi.forumReply.batchDeleteReply(replyIds);
      ElMessage.success(`成功删除${selectedReplies.value.length}条回复`);
      selectedReplies.value = [];
      getReplyList();
    } catch (error) {
      ElMessage.error('批量删除回复失败: ' + (error.message || '未知错误'));
    } finally {
      loading.value = false;
    }
  }).catch(() => {
    ElMessage.info('已取消批量删除操作');
  });
};
</script>

<style scoped>
.forum-reply-management-container {
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

/* 回复列表卡片 */
.reply-list-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
}

/* 表格 */
.reply-list-card .el-table {
  margin-bottom: 0;
  border-radius: 8px 8px 0 0;
}

.reply-list-card .el-table__header-wrapper th {
  background-color: #f5f7fa;
  font-weight: bold;
  color: #333;
}

.reply-list-card .el-table__body-wrapper tr:hover > td {
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