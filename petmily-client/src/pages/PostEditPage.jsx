import React, { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { getPost, updatePost } from '../api/post'

export default function PostEditPage() {
  const { channelId, postId } = useParams()
  const navigate = useNavigate()
  const [form, setForm] = useState({ title: '', content: '', imagePath: '' })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    getPost(channelId, postId)
      .then((res) => {
        const post = res.data
        setForm({ title: post.title, content: post.content, imagePath: post.imagePath || '' })
      })
      .catch(() => navigate(`/channel/${channelId}`))
      .finally(() => setLoading(false))
  }, [channelId, postId])

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    setError('')
    try {
      await updatePost(channelId, postId, form)
      navigate(`/channel/${channelId}/post/${postId}`)
    } catch (err) {
      setError(err.response?.data?.message || '수정에 실패했습니다.')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <div className="loading">로딩 중...</div>

  return (
    <div className="container container-narrow">
      <Link to={`/channel/${channelId}/post/${postId}`} className="breadcrumb">← 게시글로 돌아가기</Link>
      <h1 className="page-title">게시글 수정</h1>

      <form className="post-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">제목</label>
          <input
            type="text"
            name="title"
            value={form.title}
            onChange={handleChange}
            className="form-input"
            maxLength={100}
            required
          />
        </div>

        <div className="form-group">
          <label className="form-label">내용</label>
          <textarea
            name="content"
            value={form.content}
            onChange={handleChange}
            className="form-textarea"
            rows={12}
            required
          />
        </div>

        <div className="form-group">
          <label className="form-label">이미지 URL (선택)</label>
          <input
            type="text"
            name="imagePath"
            value={form.imagePath}
            onChange={handleChange}
            className="form-input"
          />
        </div>

        {error && <p className="form-error">{error}</p>}

        <div className="form-row">
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? '저장 중...' : '수정 완료'}
          </button>
          <button type="button" className="btn btn-secondary" onClick={() => navigate(-1)}>
            취소
          </button>
        </div>
      </form>
    </div>
  )
}
