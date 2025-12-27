// 论坛模块路由
export default [
  {
    path: '/forum',
    name: 'ForumHome',
    component: () => import('@/views/forum/ForumHome.vue'),
    meta: {
      title: '论坛首页',
      requiresAuth: true
    }
  },
  // 论坛板块路由
  {
    path: '/forum/section/detail/:sectionId',
    name: 'ForumSectionDetail',
    component: () => import('@/views/forum/ForumSectionDetail.vue'),
    meta: {
      title: '板块详情',
      requiresAuth: true
    }
  },
  {
    path: '/forum/section/create',
    name: 'ForumSectionCreate',
    component: () => import('@/views/forum/ForumSectionForm.vue'),
    meta: {
      title: '创建板块',
      requiresAuth: true
    }
  },
  {
    path: '/forum/section/edit/:sectionId',
    name: 'ForumSectionEdit',
    component: () => import('@/views/forum/ForumSectionForm.vue'),
    meta: {
      title: '编辑板块',
      requiresAuth: true
    }
  },
  // 论坛主题路由
  {
    path: '/forum/topic/detail/:topicId',
    name: 'ForumTopicDetail',
    component: () => import('@/views/forum/ForumTopicDetail.vue'),
    meta: {
      title: '主题详情',
      requiresAuth: true
    }
  },
  {
    path: '/forum/topic/create',
    name: 'ForumTopicCreate',
    component: () => import('@/views/forum/ForumTopicForm.vue'),
    meta: {
      title: '发布主题',
      requiresAuth: true
    }
  },
  {
    path: '/forum/topic/edit/:topicId',
    name: 'ForumTopicEdit',
    component: () => import('@/views/forum/ForumTopicForm.vue'),
    meta: {
      title: '编辑主题',
      requiresAuth: true
    }
  }
]