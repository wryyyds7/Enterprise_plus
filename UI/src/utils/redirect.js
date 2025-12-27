// 根据用户角色获取默认跳转路径
export const getRedirectPathByRole = (roles, userInfo = null) => {
  // 根据用户角色获取默认跳转路径
  if (!roles || roles.length === 0) {
    return '/home' // 默认跳转
  }
  
  if (roles.includes('ADMIN')) {
    return '/admin/dashboard'
  }
  
  if (roles.includes('ENTERPRISE')) {
    // 替换企业ID占位符，如果没有企业ID则跳转到首页
    if (userInfo && userInfo.enterpriseId) {
      return `/home/enterprise/${userInfo.enterpriseId}`
    }
    return '/home'
  }
  
  return '/home' // 默认跳转
}

export const getRedirectPathByConfig = (key) => {
  // 根据配置键获取跳转路径
  const redirectConfig = {
    'login.success': '/home', // 登录成功默认跳转主页，实际会根据角色动态调整
    'register.success': '/login',
    'logout.success': '/login'
    // 可扩展更多配置
  }
  
  return redirectConfig[key] || '/'  
}