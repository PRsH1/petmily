import api from './axios'

export const getComments = (postId) =>
  api.get(`/post/${postId}/comment`).then((r) => r.data)

export const addComment = (postId, content) =>
  api.post(`/post/${postId}/comment/add`, { content }).then((r) => r.data)

export const updateComment = (commentId, content) =>
  api.put(`/comment/${commentId}/update`, { content }).then((r) => r.data)

export const deleteComment = (commentId) =>
  api.delete(`/comment/${commentId}/delete`).then((r) => r.data)

export const likeComment = (commentId) =>
  api.post(`/comment/${commentId}/like`).then((r) => r.data)

export const getMyComments = () =>
  api.get('/comment/mycomment').then((r) => r.data.data)
