// 活动模块路由
export default [
  {
    path: '/activity/list',
    name: 'ActivityList',
    component: () => import('@/views/activity/ActivityList.vue'),
    meta: {
      title: '活动列表',
      requiresAuth: true
    }
  },
  {
    path: '/activity/detail/:activityId',
    name: 'ActivityDetail',
    component: () => import('@/views/activity/ActivityDetail.vue'),
    meta: {
      title: '活动详情',
      requiresAuth: true,
      props: true
    }
  },
  {
    path: '/activity/create',
    name: 'CreateActivity',
    component: () => import('@/views/activity/ActivityForm.vue'),
    meta: {
      title: '创建活动',
      requiresAuth: true
    }
  },
  {
    path: '/activity/edit/:activityId',
    name: 'EditActivity',
    component: () => import('@/views/activity/ActivityForm.vue'),
    meta: {
      title: '编辑活动',
      requiresAuth: true,
      props: true
    }
  }
]