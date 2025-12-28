// 管理员模块路由
export default [
  {
    path: '/admin',
    component: () => import('@/layouts/BaseLayout.vue'),
    meta: {
      requiresAuth: true,
      roles: ['ADMIN']
    },
    children: [
      { 
        path: 'dashboard', 
        name: 'AdminDashboard', 
        component: () => import('@/views/admin/Dashboard.vue'), 
        meta: { 
          title: '管理仪表盘', 
          requiresAuth: true, 
          roles: ['ADMIN'] 
        } 
      },  
      { 
        path: 'enterprises', 
        name: 'EnterpriseManagement', 
        component: () => import('@/views/admin/EnterpriseManagement.vue'), 
        meta: { 
          title: '企业管理', 
          requiresAuth: true, 
          roles: ['ADMIN'] 
        } 
      },  
      { 
        path: 'users', 
        name: 'UserManagement', 
        component: () => import('@/views/admin/UserManagement.vue'), 
        meta: { 
          title: '用户管理', 
          requiresAuth: true, 
          roles: ['ADMIN'] 
        } 
      },  
      { 
        path: 'register', 
        name: 'AdminRegister', 
        component: () => import('@/views/admin/RegisterAdmin.vue'), 
        meta: { 
          title: '新增管理员', 
          requiresAuth: true, 
          roles: ['ADMIN'] 
        } 
      },
      { 
        path: 'content-management', 
        name: 'ContentManagement', 
        component: () => import('@/views/admin/ContentManagement.vue'), 
        meta: { 
          title: '活动管理', 
          requiresAuth: true, 
          roles: ['ADMIN'] 
        } 
      },
      { 
        path: 'event-management', 
        name: 'EventManagement', 
        component: () => import('@/views/admin/EventManagement.vue'), 
        meta: { 
          title: '事件管理', 
          requiresAuth: true, 
          roles: ['ADMIN'] 
        } 
      },
      { 
    path: 'statistics', 
    name: 'Statistics', 
    component: () => import('@/views/admin/Statistics.vue'), 
    meta: { 
      title: '统计页面', 
      requiresAuth: true, 
      roles: ['ADMIN'] 
    } 
  },
  { 
    path: 'positions', 
    name: 'PositionManagement', 
    component: () => import('@/views/admin/PositionManagement.vue'), 
    meta: { 
      title: '职位管理', 
      requiresAuth: true, 
      roles: ['ADMIN'] 
    } 
  },
  { 
    path: 'forum-sections', 
    name: 'ForumSectionManagement', 
    component: () => import('@/views/admin/ForumSectionManagement.vue'), 
    meta: { 
      title: '论坛板块管理', 
      requiresAuth: true, 
      roles: ['ADMIN'] 
    } 
  },
  { 
    path: 'forum-topics', 
    name: 'ForumTopicManagement', 
    component: () => import('@/views/admin/ForumTopicManagement.vue'), 
    meta: { 
      title: '论坛主题管理', 
      requiresAuth: true, 
      roles: ['ADMIN'] 
    } 
  },
  { 
    path: 'forum-replies', 
    name: 'ForumReplyManagement', 
    component: () => import('@/views/admin/ForumReplyManagement.vue'), 
    meta: { 
      title: '论坛回复管理', 
      requiresAuth: true, 
      roles: ['ADMIN'] 
    } 
  }
    ]
  }
]
