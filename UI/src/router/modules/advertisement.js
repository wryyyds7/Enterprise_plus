// 广告模块路由
export default [
  {
    path: '/advertisement/list',
    name: 'AdvertisementList',
    component: () => import('@/views/advertisement/AdvertisementList.vue'),
    meta: {
      title: '广告列表',
      requiresAuth: true
    }
  },
  {
    path: '/advertisement/detail/:adId',
    name: 'AdvertisementDetail',
    component: () => import('@/views/advertisement/AdvertisementDetail.vue'),
    meta: {
      title: '广告详情',
      requiresAuth: true,
      props: true
    }
  },
  {
    path: '/advertisement/create',
    name: 'CreateAdvertisement',
    component: () => import('@/views/advertisement/AdvertisementForm.vue'),
    meta: {
      title: '创建广告',
      requiresAuth: true
    }
  },
  {
    path: '/advertisement/edit/:adId',
    name: 'EditAdvertisement',
    component: () => import('@/views/advertisement/AdvertisementForm.vue'),
    meta: {
      title: '编辑广告',
      requiresAuth: true,
      props: true
    }
  }
]