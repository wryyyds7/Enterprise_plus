// 聊天模块路由
export default [
  {
    path: '/chat',
    name: 'ChatList',
    component: () => import('@/views/chat/ChatSessionList.vue'),
    meta: {
      title: '聊天管理',
      requiresAuth: true
    }
  },
  {
    path: '/chat/session/:id',
    name: 'ChatSession',
    component: () => import('@/views/chat/ChatMessageList.vue'),
    meta: {
      title: '聊天会话',
      requiresAuth: true,
      props: true
    }
  },
  {
    path: '/chat/create',
    name: 'CreateChat',
    component: () => import('@/views/chat/CreateChat.vue'),
    meta: {
      title: '创建会话',
      requiresAuth: true
    }
  }
]