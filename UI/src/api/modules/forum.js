import { request } from '../index'

export default {
  // 论坛板块相关API
  forumSection: {
    // 获取论坛板块列表
    getSectionList: (params) => request.get('/forum/section', { params }),
    // 获取论坛板块详情
    getSectionInfo: (sectionId) => request.get(`/api/forum/section/${sectionId}`),
    // 创建论坛板块
    createSection: (data) => request.post('/forum/section', data),
    // 更新论坛板块
    updateSection: (data) => request.put('/forum/section', data),
    // 删除论坛板块
    deleteSection: (sectionId) => request.delete(`/api/forum/section/${sectionId}`),
    // 批量删除论坛板块
    batchDeleteSection: (data) => request.delete('/forum/section/batch', data),
    // 查询子板块列表
    getChildSectionList: (parentSectionId) => request.get(`/api/forum/section/child/${parentSectionId}`)
  },
  
  // 论坛主题相关API
  forumTopic: {
    // 获取论坛主题列表
    getTopicList: (params) => request.get('/forum/topic', { params }),
    // 获取论坛主题详情
    getTopicInfo: (topicId) => request.get(`/api/forum/topic/${topicId}`),
    // 创建论坛主题
    createTopic: (data) => request.post('/forum/topic', data),
    // 更新论坛主题
    updateTopic: (data) => request.put('/forum/topic', data),
    // 删除论坛主题
    deleteTopic: (topicId) => request.delete(`/api/forum/topic/${topicId}`),
    // 批量删除论坛主题
    batchDeleteTopic: (data) => request.delete('/forum/topic/batch', data),
    // 根据板块ID查询主题列表
    getTopicListBySectionId: (sectionId) => request.get(`/api/forum/topic/section/${sectionId}`),
    // 更新主题浏览次数
    incrementViewCount: (topicId) => request.put(`/api/forum/topic/view/${topicId}`),
    // 更新主题回复次数
    updateReplyCount: (topicId, increment) => request.put(`/api/forum/topic/reply/${topicId}/${increment}`),
    // 更新主题点赞次数
    updateLikeCount: (topicId, increment) => request.put(`/api/forum/topic/like/${topicId}/${increment}`),
    // 更新主题收藏次数
    updateFavoriteCount: (topicId, increment) => request.put(`/api/forum/topic/favorite/${topicId}/${increment}`),
    // 更新主题最后回复时间
    updateLastReplyTime: (topicId) => request.put(`/api/forum/topic/lastReply/${topicId}`)
  },
  
  // 论坛回复相关API
  forumReply: {
    // 获取论坛回复列表
    getReplyList: (params) => request.get('/forum/reply', { params }),
    // 获取论坛回复详情
    getReplyInfo: (replyId) => request.get(`/api/forum/reply/${replyId}`),
    // 创建论坛回复
    createReply: (data) => request.post('/forum/reply', data),
    // 更新论坛回复
    updateReply: (data) => request.put('/forum/reply', data),
    // 删除论坛回复
    deleteReply: (replyId) => request.delete(`/api/forum/reply/${replyId}`),
    // 批量删除论坛回复
    batchDeleteReply: (data) => request.delete('/forum/reply/batch', data),
    // 根据主题ID查询回复列表
    getReplyListByTopicId: (topicId) => request.get(`/api/forum/reply/topic/${topicId}`),
    // 根据父回复ID查询子回复列表
    getChildReplyList: (parentReplyId) => request.get(`/api/forum/reply/child/${parentReplyId}`),
    // 更新回复点赞次数
    updateLikeCount: (replyId, increment) => request.put(`/api/forum/reply/like/${replyId}/${increment}`)
  },
  
  // 论坛收藏相关API
  forumFavorite: {
    // 获取论坛收藏记录列表
    getFavoriteList: (params) => request.get('/forum/favorite', { params }),
    // 获取论坛收藏记录详情
    getFavoriteInfo: (favoriteId) => request.get(`/api/forum/favorite/${favoriteId}`),
    // 创建论坛收藏记录
    createFavorite: (data) => request.post('/forum/favorite', data),
    // 删除论坛收藏记录
    deleteFavorite: (favoriteId) => request.delete(`/api/forum/favorite/${favoriteId}`),
    // 批量删除论坛收藏记录
    batchDeleteFavorite: (data) => request.delete('/forum/favorite/batch', data),
    // 根据主题ID和用户ID查询收藏记录
    getFavoriteByTopicAndUser: (topicId, userId) => request.get(`/api/forum/favorite/topic/${topicId}/user/${userId}`),
    // 根据主题ID统计收藏人数
    getFavoriteCountByTopicId: (topicId) => request.get(`/api/forum/favorite/count/${topicId}`),
    // 根据用户ID查询收藏主题列表
    getFavoriteTopicListByUserId: (userId) => request.get(`/api/forum/favorite/user/${userId}`),
    // 收藏主题
    favoriteTopic: (data) => request.post('/forum/favorite/topic', data),
    // 取消收藏主题
    cancelFavoriteTopic: (topicId, userId) => request.delete(`/api/forum/favorite/topic/${topicId}/user/${userId}`),
    // 查询用户是否已收藏主题
    isFavorited: (topicId, userId) => request.get(`/api/forum/favorite/isFavorited/${topicId}/${userId}`)
  }
}