import React, { useEffect, useState } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import { getPosts, deletePost } from '../api/post'
import { deleteChannel } from '../api/channel'
import { useAuth } from '../context/AuthContext'
import Modal from '../components/Modal'

export default function ChannelPage() {
  const { channelId } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(null)

  useEffect(() => {
    getPosts(channelId)
      .then(setData)
      .catch(() => navigate('/'))
      .finally(() => setLoading(false))
  }, [channelId])

  const handleDeleteChannel = async () => {
    try {
      await deleteChannel(channelId)
      navigate('/')
    } catch (err) {
      alert(err.response?.data?.message || '채널 삭제에 실패했습니다.')
    }
    setModal(null)
  }

  const handleDeletePost = async (postId) => {
    try {
      await deletePost(channelId, postId)
      setData((prev) => ({ ...prev, data: prev.data.filter((p) => p.id !== postId) }))
    } catch (err) {
      alert(err.response?.data?.message || '게시글 삭제에 실패했습니다.')
    }
    setModal(null)
  }

  if (loading) return <div className="loading">로딩 중...</div>
  if (!data) return null

  const posts = data.data || []
  const channelName = data.channelName || '채널'
  const channelOwner = data.nickname

  return (
    <div className="container">
      {modal && (
        <Modal
          title={modal.title}
          message={modal.message}
          onConfirm={modal.onConfirm}
          onCancel={() => setModal(null)}
        />
      )}

      <div className="page-header">
        <div>
          <Link to="/" className="breadcrumb">← 채널 목록</Link>
          <h1 className="page-title">{channelName}</h1>
          {channelOwner && <p className="channel-owner">개설자: {channelOwner}</p>}
        </div>
        <div className="page-header-actions">
          <Link to={`/channel/${channelId}/write`} className="btn btn-primary">
            + 글쓰기
          </Link>
          {user?.email && channelOwner && user.email === channelOwner && (
            <button
              className="btn btn-danger"
              onClick={() => setModal({
                title: '채널 삭제',
                message: '채널을 삭제하면 모든 게시글이 삭제됩니다. 계속하시겠습니까?',
                onConfirm: handleDeleteChannel,
              })}
            >
              채널 삭제
            </button>
          )}
        </div>
      </div>

      {posts.length === 0 ? (
        <div className="empty-state">
          <p>아직 게시글이 없습니다. 첫 글을 작성해보세요!</p>
        </div>
      ) : (
        <div className="post-list">
          {posts.map((post) => (
            <div key={post.id} className="post-item">
              <Link to={`/channel/${channelId}/post/${post.id}`} className="post-item-link">
                <h3 className="post-title">{post.title}</h3>
                <div className="post-meta">
                  <span>{post.nickname}</span>
                  <span>조회 {post.hit}</span>
                  <span>좋아요 {post.likePost}</span>
                  <span>댓글 {post.commentCount}</span>
                  <span>{post.createDate?.slice(0, 10)}</span>
                </div>
              </Link>
              {user?.email === post.email && (
                <div className="post-actions">
                  <Link
                    to={`/channel/${channelId}/post/${post.id}/edit`}
                    className="btn btn-secondary btn-sm"
                  >
                    수정
                  </Link>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => setModal({
                      title: '게시글 삭제',
                      message: '게시글을 삭제하시겠습니까?',
                      onConfirm: () => handleDeletePost(post.id),
                    })}
                  >
                    삭제
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
