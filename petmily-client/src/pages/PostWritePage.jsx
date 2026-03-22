import React, { useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { writePost } from '../api/post'

export default function PostWritePage() {
  const { channelId } = useParams()
  const navigate = useNavigate()
  const [form, setForm] = useState({ title: '', content: '', imagePath: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.title.trim() || !form.content.trim()) {
      return setError('제목과 내용을 입력해주세요.')
    }
    setLoading(true)
    setError('')
    try {
      const res = await writePost(channelId, form)
      navigate(`/channel/${channelId}/post/${res.data?.id}`)
    } catch (err) {
      setError(err.response?.data?.message || '게시글 작성에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container container-narrow">
      <Link to={`/channel/${channelId}`} className="breadcrumb">← 채널로 돌아가기</Link>
      <h1 className="page-title">글쓰기</h1>

      <form className="post-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">제목</label>
          <input
            type="text"
            name="title"
            value={form.title}
            onChange={handleChange}
            placeholder="제목을 입력하세요."
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
            placeholder="내용을 입력하세요."
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
            placeholder="이미지 URL을 입력하세요."
            className="form-input"
          />
        </div>

        {error && <p className="form-error">{error}</p>}

        <div className="form-row">
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? '등록 중...' : '게시글 등록'}
          </button>
          <button type="button" className="btn btn-secondary" onClick={() => navigate(-1)}>
            취소
          </button>
        </div>
      </form>
    </div>
  )
}
