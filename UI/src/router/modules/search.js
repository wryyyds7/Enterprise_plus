// 搜索模块路由
export default [
  {
    path: '/search/result',
    name: 'SearchResult',
    component: () => import('@/views/search/SearchResult.vue'),
    meta: {
      title: '搜索结果',
      requiresAuth: true,
      props: (route) => ({ keyword: route.query.keyword })
    }
  }
]