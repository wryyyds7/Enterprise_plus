// 主页模块路由
export default [
  {
    path: '/home',
    name: 'HomePage',
    component: () => import('@/views/home/HomePage.vue'),
    meta: {
      title: '首页',
      requiresAuth: true
    }
  },
  {
    path: '/home/recommendation',
    name: 'EnterpriseRecommendation',
    component: () => import('@/views/home/EnterpriseRecommendation.vue'),
    meta: {
      title: '企业推荐',
      requiresAuth: true
    }
  },
  {
    path: '/home/events',
    name: 'CampusEventList',
    component: () => import('@/views/home/CampusEventList.vue'),
    meta: {
      title: '校园招聘活动',
      requiresAuth: true
    }
  },
  {
    path: '/home/enterprise/:enterpriseId',
    name: 'HomeEnterpriseDetail',
    component: () => import('@/views/home/EnterpriseDetail.vue'),
    meta: {
      title: '企业详情',
      requiresAuth: true,
      props: true
    }
  },
  {
    path: '/home/event/:eventId',
    name: 'HomeEventDetail',
    component: () => import('@/views/home/EventDetail.vue'),
    meta: {
      title: '活动详情',
      requiresAuth: true,
      props: true
    }
  }
]
