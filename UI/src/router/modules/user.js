// 用户模块路由
export default [
  {
    path: '/user/list',
    name: 'UserList',
    component: () => import('@/views/user/UserList.vue'),
    meta: {
      title: '用户列表',
      requiresAuth: true,
      roles: ['ADMIN']
    }
  },
  {
    path: '/user/detail/:userId',
    name: 'UserDetail',
    component: () => import('@/views/user/UserDetail.vue'),
    meta: {
      title: '用户详情',
      requiresAuth: true
    }
  },
  {
    path: '/user/form',
    name: 'UserFormCreate',
    component: () => import('@/views/user/UserForm.vue'),
    meta: {
      title: '创建用户',
      requiresAuth: true,
      roles: ['ADMIN']
    }
  },
  {
    path: '/user/form/:userId',
    name: 'UserFormUpdate',
    component: () => import('@/views/user/UserForm.vue'),
    meta: {
      title: '编辑用户',
      requiresAuth: true,
      roles: ['ADMIN']
    }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: {
      title: '个人中心',
      requiresAuth: true
    }
  }
]