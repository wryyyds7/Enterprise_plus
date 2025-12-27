// 认证模块路由
export default [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/register/user',
    name: 'RegisterUser',
    component: () => import('@/views/login/RegisterUser.vue'),
    meta: {
      title: '用户注册',
      requiresAuth: false
    }
  },
  {
    path: '/register/enterprise',
    name: 'RegisterEnterprise',
    component: () => import('@/views/login/RegisterEnterprise.vue'),
    meta: {
      title: '企业注册',
      requiresAuth: false
    }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: {
      title: '禁止访问',
      requiresAuth: false
    }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      requiresAuth: false
    }
  }
]