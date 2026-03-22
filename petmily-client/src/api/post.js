import api from './axios'

export const getPosts = (channelId) =>
  api.get(`/channel/${channelId}/post`).then((r) => r.data)

export const getPost = (channelId, postId) =>
  api.get(`/channel/${channelId}/post/${postId}`).then((r) => r.data)

export const writePost = (channelId, data) =>
  api.post(`/channel/${channelId}/post/write`, data).then((r) => r.data)

export const updatePost = (channelId, postId, data) =>
  api.put(`/channel/${channelId}/post/update/${postId}`, data).then((r) => r.data)

export const deletePost = (channelId, postId) =>
  api.delete(`/channel/${channelId}/post/delete/${postId}`).then((r) => r.data)

export const likePost = (channelId, postId) =>
  api.post(`/channel/${channelId}/post/${postId}/like`).then((r) => r.data)

export const reportPost = (channelId, postId, content) =>
  api.post(`/channel/${channelId}/post/${postId}/report`, { content }).then((r) => r.data)

export const searchPosts = (query) =>
  api.get('/post/search', { params: { query } }).then((r) => r.data.data)

export const getMyPosts = () => api.get('/post/mypage').then((r) => r.data.data)

export const addScrap = (postId) =>
  api.post(`/post/${postId}/scrap`).then((r) => r.data)

export const getScraps = () => api.get('/scrap').then((r) => r.data.data)

export const deleteScrap = (scrapId) =>
  api.delete(`/scrap/${scrapId}`).then((r) => r.data)
