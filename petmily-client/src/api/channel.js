import api from './axios'

export const getChannels = () => api.get('/channel').then((r) => r.data.data)

export const getChannelById = (channelId) =>
  api.get(`/channel/${channelId}/post`).then((r) => r.data)

export const createChannel = (data) =>
  api.post('/channel', data).then((r) => r.data)

export const updateChannel = (channelId, data) =>
  api.put(`/channel/update/${channelId}`, data).then((r) => r.data)

export const deleteChannel = (channelId) =>
  api.delete(`/channel/delete/${channelId}`).then((r) => r.data)

export const searchChannels = (query) =>
  api.get('/channel/search', { params: { query } }).then((r) => r.data.data)

export const getMyChannels = () => api.get('/channel/mypage').then((r) => r.data.data)

export const addBookmark = (channelId) =>
  api.post(`/channel/${channelId}/bookmark`).then((r) => r.data)

export const removeBookmark = (channelId) =>
  api.delete(`/channel/${channelId}/bookmark`).then((r) => r.data)

export const getBookmarks = () => api.get('/bookmark').then((r) => r.data.data)
