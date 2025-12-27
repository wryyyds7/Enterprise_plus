// AI模块路由
export default [
  {
    path: '/ai',
    name: 'AIChat',
    component: () => import('@/views/ai/AIChat.vue'),
    meta: {
      title: 'AI对话',
      requiresAuth: true
    }
  }
]