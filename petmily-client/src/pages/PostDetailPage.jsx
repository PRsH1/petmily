import React, { useEffect, useState } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import { getPost, likePost, reportPost, addScrap, deletePost } from '../api/post'
import { getComments, addComment, updateComment, deleteComment, likeComment } from '../api/comment'
import { useAuth } from '../context/AuthContext'
import Modal from '../components/Modal'

export default function PostDetailPage() {
  const { channelId, postId } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()

  const [postData, setPostData] = useState(null)
  const [comments, setComments] = useState([])
  const [newComment, setNewComment] = useState('')
  const [editingComment, setEditingComment] = useState(null)
  const [editContent, setEditContent] = useState('')
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(null)
  const [reportContent, setReportContent] = useState('')
  const [showReport, setShowReport] = useState(false)

  const loadPost = async () => {
    try {
      const res = await getPost(channelId, postId)
      setPostData(res)
    } catch {
      navigate(`/channel/${channelId}`)
    }
  }

  const loadComments = async () => {
    try {
      const res = await getComments(postId)
      setComments(res.data || [])
    } catch {
      setComments([])
    }
  }

  useEffect(() => {
    Promise.all([loadPost(), loadComments()]).finally(() => setLoading(false))
  }, [channelId, postId])

  const post = postData?.data

  const handleLike = async () => {
    try {
      const res = await likePost(channelId, postId)
      alert(res.message || '좋아요를 눌렀습니다.')
      loadPost()
    } catch {
      alert('좋아요 처리에 실패했습니다.')
    }
  }

  const handleScrap = async () => {
    try {
      await addScrap(postId)
      alert('스크랩되었습니다.')
    } catch (err) {
      alert(err.response?.data?.message || '스크랩에 실패했습니다.')
    }
  }

  const handleReport = async (e) => {
    e.preventDefault()
    if (!reportContent.trim()) return
    try {
      const res = await reportPost(channelId, postId, reportContent)
      alert(res.message || '신고가 접수되었습니다.')
      setShowReport(false)
      setReportContent('')
      if (res.message?.includes('자동 삭제')) navigate(`/channel/${channelId}`)
    } catch (err) {
      alert(err.response?.data?.message || '신고에 실패했습니다.')
    }
  }

  const handleDeletePost = async () => {
    try {
      await deletePost(channelId, postId)
      navigate(`/channel/${channelId}`)
    } catch (err) {
      alert(err.response?.data?.message || '삭제에 실패했습니다.')
    }
    setModal(null)
  }

  const handleAddComment = async (e) => {
    e.preventDefault()
    if (!newComment.trim()) return
    try {
      await addComment(postId, newComment)
      setNewComment('')
      loadComments()
    } catch {
      alert('댓글 등록에 실패했습니다.')
    }
  }

  const handleUpdateComment = async (commentId) => {
    if (!editContent.trim()) return
    try {
      await updateComment(commentId, editContent)
      setEditingComment(null)
      loadComments()
    } catch {
      alert('댓글 수정에 실패했습니다.')
    }
  }

  const handleDeleteComment = async (commentId) => {
    try {
      await deleteComment(commentId)
      loadComments()
    } catch {
      alert('댓글 삭제에 실패했습니다.')
    }
    setModal(null)
  }

  const handleLikeComment = async (commentId) => {
    try {
      await likeComment(commentId)
      loadComments()
    } catch {
      alert('좋아요 처리에 실패했습니다.')
    }
  }

  if (loading) return <div className="loading">로딩 중...</div>
  if (!post) return null

  return (
    <div className="container container-narrow">
      {modal && (
        <Modal
          title={modal.title}
          message={modal.message}
          onConfirm={modal.onConfirm}
          onCancel={() => setModal(null)}
        />
      )}

      <Link to={`/channel/${channelId}`} className="breadcrumb">← 채널로 돌아가기</Link>

      <article className="post-detail">
        <div className="post-detail-header">
          <h1 className="post-detail-title">{post.title}</h1>
          <div className="post-detail-meta">
            <span className="post-author">{post.nickname}</span>
            <span>{post.createDate?.slice(0, 10)}</span>
            <span>조회 {post.hit}</span>
            <span>좋아요 {post.likePost}</span>
          </div>
        </div>

        {post.imagePath && (
          <img src={post.imagePath} alt="첨부 이미지" className="post-image" />
        )}

        <div className="post-detail-content">{post.content}</div>

        <div className="post-detail-actions">
          <button className="btn btn-like" onClick={handleLike}>❤️ 좋아요</button>
          <button className="btn btn-secondary" onClick={handleScrap}>🔖 스크랩</button>
          <button className="btn btn-secondary" onClick={() => setShowReport(!showReport)}>
            🚨 신고
          </button>
          {user?.email === post.email && (
            <>
              <Link to={`/channel/${channelId}/post/${postId}/edit`} className="btn btn-secondary">
                수정
              </Link>
              <button
                className="btn btn-danger"
                onClick={() => setModal({
                  title: '게시글 삭제',
                  message: '게시글을 삭제하시겠습니까?',
                  onConfirm: handleDeletePost,
                })}
              >
                삭제
              </button>
            </>
          )}
        </div>

        {showReport && (
          <form className="report-form" onSubmit={handleReport}>
            <textarea
              placeholder="신고 사유를 입력하세요."
              value={reportContent}
              onChange={(e) => setReportContent(e.target.value)}
              className="form-textarea"
              rows={3}
              required
            />
            <div className="form-row">
              <button type="submit" className="btn btn-danger btn-sm">신고 제출</button>
              <button type="button" className="btn btn-secondary btn-sm" onClick={() => setShowReport(false)}>
                취소
              </button>
            </div>
          </form>
        )}
      </article>

      <section className="comment-section">
        <h2 className="section-title">댓글 {comments.length}개</h2>

        <form className="comment-form" onSubmit={handleAddComment}>
          <textarea
            placeholder="댓글을 입력하세요."
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            className="form-textarea"
            rows={3}
            required
          />
          <button type="submit" className="btn btn-primary">댓글 등록</button>
        </form>

        <div className="comment-list">
          {comments.map((comment) => (
            <div key={comment.id} className="comment-item">
              {editingComment === comment.id ? (
                <div className="comment-edit">
                  <textarea
                    value={editContent}
                    onChange={(e) => setEditContent(e.target.value)}
                    className="form-textarea"
                    rows={2}
                  />
                  <div className="form-row">
                    <button className="btn btn-primary btn-sm" onClick={() => handleUpdateComment(comment.id)}>
                      저장
                    </button>
                    <button className="btn btn-secondary btn-sm" onClick={() => setEditingComment(null)}>
                      취소
                    </button>
                  </div>
                </div>
              ) : (
                <>
                  <div className="comment-header">
                    <span className="comment-author">{comment.nickname}</span>
                    <span className="comment-date">{comment.createDate?.slice(0, 10)}</span>
                  </div>
                  <p className="comment-content">{comment.content}</p>
                  <div className="comment-actions">
                    <button className="btn-text" onClick={() => handleLikeComment(comment.id)}>
                      ❤️ {comment.commentLike || 0}
                    </button>
                    {user?.email === comment.email && (
                      <>
                        <button
                          className="btn-text"
                          onClick={() => { setEditingComment(comment.id); setEditContent(comment.content) }}
                        >
                          수정
                        </button>
                        <button
                          className="btn-text btn-text-danger"
                          onClick={() => setModal({
                            title: '댓글 삭제',
                            message: '댓글을 삭제하시겠습니까?',
                            onConfirm: () => handleDeleteComment(comment.id),
                          })}
                        >
                          삭제
                        </button>
                      </>
                    )}
                  </div>
                </>
              )}
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}
