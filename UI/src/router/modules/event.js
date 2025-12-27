// 事件模块路由
export default [
  {
    path: '/event/list',
    name: 'EventList',
    component: () => import('@/views/event/EventList.vue'),
    meta: {
      title: '事件列表',
      requiresAuth: true
    }
  },
  {
    path: '/event/detail/:eventId',
    name: 'EventDetail',
    component: () => import('@/views/event/EventDetail.vue'),
    meta: {
      title: '事件详情',
      requiresAuth: true
    }
  },
  {
    path: '/event/create',
    name: 'EventCreate',
    component: () => import('@/views/event/EventForm.vue'),
    meta: {
      title: '创建事件',
      requiresAuth: true
    }
  },
  {
    path: '/event/edit/:eventId',
    name: 'EventEdit',
    component: () => import('@/views/event/EventForm.vue'),
    meta: {
      title: '编辑事件',
      requiresAuth: true
    }
  }
]