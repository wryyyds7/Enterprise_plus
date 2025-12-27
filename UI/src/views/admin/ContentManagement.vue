<template>
  <div class="content-management">
    <div class="page-header">
      <h2>内容管理</h2>
      <p>管理网站公告和轮播图内容</p>
    </div>

    <el-tabs v-model="activeTab" class="content-tabs">
      <!-- 公告管理 -->
      <el-tab-pane label="公告管理" name="announcement">
        <div class="tab-content">
          <el-card shadow="never">
            <template #header>
              <div class="card-header">
                <span>公告列表</span>
                <el-button type="primary" @click="openAnnouncementModal('add')">
                  <el-icon><Plus /></el-icon>
                  新增公告
                </el-button>
              </div>
            </template>

            <div class="search-form">
              <el-form :model="announcementSearchForm" inline>
                <el-form-item label="公告标题">
                  <el-input
                    v-model="announcementSearchForm.title"
                    placeholder="请输入公告标题"
                    clearable
                    style="width: 200px"
                  />
                </el-form-item>
                <el-form-item label="状态">
                  <el-select
                    v-model="announcementSearchForm.status"
                    placeholder="请选择状态"
                    clearable
                    style="width: 120px"
                  >
                    <el-option label="启用" value="1" />
                    <el-option label="禁用" value="0" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="getAnnouncementList">查询</el-button>
                  <el-button @click="resetAnnouncementSearch">重置</el-button>
                </el-form-item>
              </el-form>
            </div>

            <el-table
              v-loading="announcementLoading"
              :data="announcementList"
              style="width: 100%"
              border
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="title" label="公告标题" min-width="200" />
              <el-table-column prop="content" label="公告内容" min-width="300" show-overflow-tooltip />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                    {{ scope.row.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="180" />
              <el-table-column prop="updateTime" label="更新时间" width="180" />
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="scope">
                  <el-button size="small" type="primary" @click="openAnnouncementModal('edit', scope.row)">
                    编辑
                  </el-button>
                  <el-button size="small" type="danger" @click="deleteAnnouncement(scope.row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination-container">
              <el-pagination
                v-model:current-page="announcementPagination.currentPage"
                v-model:page-size="announcementPagination.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="announcementPagination.total"
                @size-change="getAnnouncementList"
                @current-change="getAnnouncementList"
              />
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 轮播图管理 -->
      <el-tab-pane label="轮播图管理" name="banner">
        <div class="tab-content">
          <el-card shadow="never">
            <template #header>
              <div class="card-header">
                <span>轮播图列表</span>
                <el-button type="primary" @click="openBannerModal('add')">
                  <el-icon><Plus /></el-icon>
                  新增轮播图
                </el-button>
              </div>
            </template>

            <div class="search-form">
              <el-form :model="bannerSearchForm" inline>
                <el-form-item label="轮播图标题">
                  <el-input
                    v-model="bannerSearchForm.title"
                    placeholder="请输入轮播图标题"
                    clearable
                    style="width: 200px"
                  />
                </el-form-item>
                <el-form-item label="状态">
                  <el-select
                    v-model="bannerSearchForm.status"
                    placeholder="请选择状态"
                    clearable
                    style="width: 120px"
                  >
                    <el-option label="启用" value="1" />
                    <el-option label="禁用" value="0" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="getBannerList">查询</el-button>
                  <el-button @click="resetBannerSearch">重置</el-button>
                </el-form-item>
              </el-form>
            </div>

            <el-table
              v-loading="bannerLoading"
              :data="bannerList"
              style="width: 100%"
              border
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="title" label="轮播图标题" min-width="200" />
              <el-table-column prop="imageUrl" label="轮播图图片" width="200">
                <template #default="scope">
                  <el-image
                    :src="scope.row.imageUrl"
                    :preview-src-list="[scope.row.imageUrl]"
                    style="width: 100px; height: 50px; object-fit: cover"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="linkUrl" label="跳转链接" min-width="200" show-overflow-tooltip />
              <el-table-column prop="sortOrder" label="排序" width="100" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                    {{ scope.row.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="180" />
              <el-table-column prop="updateTime" label="更新时间" width="180" />
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="scope">
                  <el-button size="small" type="primary" @click="openBannerModal('edit', scope.row)">
                    编辑
                  </el-button>
                  <el-button size="small" type="danger" @click="deleteBanner(scope.row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination-container">
              <el-pagination
                v-model:current-page="bannerPagination.currentPage"
                v-model:page-size="bannerPagination.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="bannerPagination.total"
                @size-change="getBannerList"
                @current-change="getBannerList"
              />
            </div>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 公告编辑弹窗 -->
    <el-dialog
      v-model="announcementDialogVisible"
      :title="announcementDialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="announcementFormRef"
        :model="announcementForm"
        :rules="announcementRules"
        label-width="80px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="announcementForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="announcementForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="announcementForm.status"
            active-value="1"
            inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="announcementDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveAnnouncement">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 轮播图编辑弹窗 -->
    <el-dialog
      v-model="bannerDialogVisible"
      :title="bannerDialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="bannerFormRef"
        :model="bannerForm"
        :rules="bannerRules"
        label-width="80px"
      >
        <el-form-item label="轮播图标题" prop="title">
          <el-input v-model="bannerForm.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="轮播图图片" prop="imageUrl">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleBannerUploadSuccess"
            :before-upload="handleBannerBeforeUpload"
          >
            <img v-if="bannerForm.imageUrl" :src="bannerForm.imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="跳转链接" prop="linkUrl">
          <el-input v-model="bannerForm.linkUrl" placeholder="请输入跳转链接" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="bannerForm.sortOrder" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="bannerForm.status"
            active-value="1"
            inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="bannerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveBanner">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import adminApi from '@/api/modules/admin';

// 标签页激活状态
const activeTab = ref('announcement');

// 公告管理
const announcementLoading = ref(false);
const announcementList = ref([]);
const announcementSearchForm = reactive({ title: '', status: '' });
const announcementPagination = reactive({ currentPage: 1, pageSize: 10, total: 0 });
const announcementDialogVisible = ref(false);
const announcementDialogTitle = ref('');
const announcementForm = reactive({ id: null, title: '', content: '', status: '1' });
const announcementFormRef = ref(null);
const announcementRules = reactive({
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }, { min: 2, max: 50, message: '标题长度在 2 到 50 个字符', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
});

// 轮播图管理
const bannerLoading = ref(false);
const bannerList = ref([]);
const bannerSearchForm = reactive({ title: '', status: '' });
const bannerPagination = reactive({ currentPage: 1, pageSize: 10, total: 0 });
const bannerDialogVisible = ref(false);
const bannerDialogTitle = ref('');
const bannerForm = reactive({ id: null, title: '', imageUrl: '', linkUrl: '', sortOrder: 0, status: '1' });
const bannerFormRef = ref(null);
const bannerRules = reactive({
  title: [{ required: true, message: '请输入轮播图标题', trigger: 'blur' }, { min: 2, max: 50, message: '标题长度在 2 到 50 个字符', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请上传轮播图图片', trigger: 'blur' }]
});

// 上传地址
const uploadUrl = '/upload';

// 公告相关方法
const getAnnouncementList = async () => {
  announcementLoading.value = true;
  try {
    // 这里应该调用真实API，暂时使用mock数据
    // const response = await adminApi.getAnnouncementList({
    //   ...announcementSearchForm,
    //   pageNum: announcementPagination.currentPage,
    //   pageSize: announcementPagination.pageSize
    // });
    // announcementList.value = response.data.records;
    // announcementPagination.total = response.data.total;
    
    // Mock数据
    announcementList.value = [
      { id: 1, title: '欢迎使用校招企业推荐平台', content: '本平台致力于为大学生提供个性化的企业推荐服务...', status: 1, createTime: '2025-12-01 10:00:00', updateTime: '2025-12-01 10:00:00' },
      { id: 2, title: '平台更新公告', content: '平台已完成最新一轮更新，新增了多项功能...', status: 1, createTime: '2025-12-05 15:30:00', updateTime: '2025-12-05 15:30:00' }
    ];
    announcementPagination.total = 2;
    
  } catch (error) {
    ElMessage.error('获取公告列表失败');
    console.error('获取公告列表失败:', error);
  } finally {
    announcementLoading.value = false;
  }
};

const resetAnnouncementSearch = () => {
  Object.assign(announcementSearchForm, { title: '', status: '' });
  announcementPagination.currentPage = 1;
  getAnnouncementList();
};

const openAnnouncementModal = (type, row = null) => {
  announcementDialogTitle.value = type === 'add' ? '新增公告' : '编辑公告';
  if (type === 'add') {
    Object.assign(announcementForm, { id: null, title: '', content: '', status: '1' });
  } else {
    Object.assign(announcementForm, { ...row });
  }
  announcementDialogVisible.value = true;
};

const saveAnnouncement = async () => {
  await announcementFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里应该调用真实API，暂时使用mock数据
        // let response;
        // if (announcementForm.id) {
        //   response = await adminApi.updateAnnouncement(announcementForm);
        // } else {
        //   response = await adminApi.addAnnouncement(announcementForm);
        // }
        
        // Mock数据处理
        ElMessage.success(announcementForm.id ? '公告更新成功' : '公告新增成功');
        announcementDialogVisible.value = false;
        getAnnouncementList();
      } catch (error) {
        ElMessage.error(announcementForm.id ? '公告更新失败' : '公告新增失败');
        console.error(announcementForm.id ? '公告更新失败:' : '公告新增失败:', error);
      }
    }
  });
};

const deleteAnnouncement = async (id) => {
  await ElMessageBox.confirm('确定要删除这条公告吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 这里应该调用真实API，暂时使用mock数据
      // await adminApi.deleteAnnouncement([id]);
      
      // Mock数据处理
      ElMessage.success('公告删除成功');
      getAnnouncementList();
    } catch (error) {
      ElMessage.error('公告删除失败');
      console.error('公告删除失败:', error);
    }
  }).catch(() => {
    // 用户取消删除
  });
};

// 轮播图相关方法
const getBannerList = async () => {
  bannerLoading.value = true;
  try {
    // 这里应该调用真实API，暂时使用mock数据
    // const response = await adminApi.getBannerList({
    //   ...bannerSearchForm,
    //   pageNum: bannerPagination.currentPage,
    //   pageSize: bannerPagination.pageSize
    // });
    // bannerList.value = response.data.records;
    // bannerPagination.total = response.data.total;
    
    // Mock数据
    bannerList.value = [
      { id: 1, title: '秋季校招专场', imageUrl: 'https://picsum.photos/id/1/100/50', linkUrl: '/home', sortOrder: 1, status: 1, createTime: '2025-12-01 10:00:00', updateTime: '2025-12-01 10:00:00' },
      { id: 2, title: '热门企业推荐', imageUrl: 'https://picsum.photos/id/2/100/50', linkUrl: '/home/recommendation', sortOrder: 2, status: 1, createTime: '2025-12-05 15:30:00', updateTime: '2025-12-05 15:30:00' }
    ];
    bannerPagination.total = 2;
    
  } catch (error) {
    ElMessage.error('获取轮播图列表失败');
    console.error('获取轮播图列表失败:', error);
  } finally {
    bannerLoading.value = false;
  }
};

const resetBannerSearch = () => {
  Object.assign(bannerSearchForm, { title: '', status: '' });
  bannerPagination.currentPage = 1;
  getBannerList();
};

const openBannerModal = (type, row = null) => {
  bannerDialogTitle.value = type === 'add' ? '新增轮播图' : '编辑轮播图';
  if (type === 'add') {
    Object.assign(bannerForm, { id: null, title: '', imageUrl: '', linkUrl: '', sortOrder: 0, status: '1' });
  } else {
    Object.assign(bannerForm, { ...row });
  }
  bannerDialogVisible.value = true;
};

const handleBannerUploadSuccess = (response, file) => {
  if (response.success) {
    bannerForm.imageUrl = response.data.fileUrl;
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error('图片上传失败');
  }
};

const handleBannerBeforeUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  if (!isImage) {
    ElMessage.error('请上传图片文件');
    return false;
  }
  const isLt5M = file.size / 1024 / 1024 < 5;
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB');
    return false;
  }
  return true;
};

const saveBanner = async () => {
  await bannerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里应该调用真实API，暂时使用mock数据
        // let response;
        // if (bannerForm.id) {
        //   response = await adminApi.updateBanner(bannerForm);
        // } else {
        //   response = await adminApi.addBanner(bannerForm);
        // }
        
        // Mock数据处理
        ElMessage.success(bannerForm.id ? '轮播图更新成功' : '轮播图新增成功');
        bannerDialogVisible.value = false;
        getBannerList();
      } catch (error) {
        ElMessage.error(bannerForm.id ? '轮播图更新失败' : '轮播图新增失败');
        console.error(bannerForm.id ? '轮播图更新失败:' : '轮播图新增失败:', error);
      }
    }
  });
};

const deleteBanner = async (id) => {
  await ElMessageBox.confirm('确定要删除这条轮播图吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 这里应该调用真实API，暂时使用mock数据
      // await adminApi.deleteBanner([id]);
      
      // Mock数据处理
      ElMessage.success('轮播图删除成功');
      getBannerList();
    } catch (error) {
      ElMessage.error('轮播图删除失败');
      console.error('轮播图删除失败:', error);
    }
  }).catch(() => {
    // 用户取消删除
  });
};

// 初始化
onMounted(() => {
  getAnnouncementList();
  getBannerList();
});
</script>

<style scoped>
.content-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 5px 0;
  font-size: 20px;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.content-tabs {
  margin-bottom: 20px;
}

.tab-content {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>