import React, { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getChannels, createChannel, deleteChannel, addBookmark, removeBookmark, getBookmarks } from '../api/channel'
import { useAuth } from '../context/AuthContext'

const CATEGORIES = [
  { id: 1, name: '전체' },
  { id: 2, name: '강아지' },
  { id: 3, name: '고양이' },
  { id: 4, name: '기타' },
]

export default function HomePage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [channels, setChannels] = useState([])
  const [bookmarked, setBookmarked] = useState(new Set())
  const [loading, setLoading] = useState(true)
  const [showCreate, setShowCreate] = useState(false)
  const [newChannel, setNewChannel] = useState({ channelName: '', categoryId: 2 })
  const [creating, setCreating] = useState(false)
  const [filter, setFilter] = useState('')

  const loadData = async () => {
    try {
      const [ch, bk] = await Promise.all([getChannels(), getBookmarks()])
      setChannels(ch || [])
      setBookmarked(new Set((bk || []).map((b) => b.channelId)))
    } catch {
      setChannels([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { loadData() }, [])

  const handleCreate = async (e) => {
    e.preventDefault()
    if (!newChannel.channelName.trim()) return
    setCreating(true)
    try {
      await createChannel(newChannel)
      setNewChannel({ channelName: '', categoryId: 2 })
      setShowCreate(false)
      loadData()
    } catch (err) {
      alert(err.response?.data?.message || '채널 생성에 실패했습니다.')
    } finally {
      setCreating(false)
    }
  }

  const handleBookmark = async (e, channelId) => {
    e.preventDefault()
    e.stopPropagation()
    try {
      if (bookmarked.has(channelId)) {
        await removeBookmark(channelId)
        setBookmarked((prev) => { const n = new Set(prev); n.delete(channelId); return n })
      } else {
        await addBookmark(channelId)
        setBookmarked((prev) => new Set([...prev, channelId]))
      }
    } catch (err) {
      alert(err.response?.data?.message || '즐겨찾기 처리에 실패했습니다.')
    }
  }

  const filtered = channels.filter((c) =>
    !filter || c.channelName?.toLowerCase().includes(filter.toLowerCase())
  )

  if (loading) return <div className="loading">채널 목록 불러오는 중...</div>

  return (
    <div className="container">
      <div className="page-header">
        <h1 className="page-title">채널 목록</h1>
        <div className="page-header-actions">
          <input
            type="text"
            placeholder="채널명 필터..."
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
            className="form-input filter-input"
          />
          <button className="btn btn-primary" onClick={() => setShowCreate(!showCreate)}>
            + 채널 만들기
          </button>
        </div>
      </div>

      {showCreate && (
        <form className="create-channel-form" onSubmit={handleCreate}>
          <input
            type="text"
            placeholder="채널 이름"
            value={newChannel.channelName}
            onChange={(e) => setNewChannel({ ...newChannel, channelName: e.target.value })}
            className="form-input"
            required
          />
          <select
            value={newChannel.categoryId}
            onChange={(e) => setNewChannel({ ...newChannel, categoryId: Number(e.target.value) })}
            className="form-select"
          >
            {CATEGORIES.filter((c) => c.id !== 1).map((cat) => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
          <button type="submit" className="btn btn-primary" disabled={creating}>
            {creating ? '생성 중...' : '생성'}
          </button>
          <button type="button" className="btn btn-secondary" onClick={() => setShowCreate(false)}>
            취소
          </button>
        </form>
      )}

      {filtered.length === 0 ? (
        <div className="empty-state">
          <p>채널이 없습니다. 첫 번째 채널을 만들어보세요!</p>
        </div>
      ) : (
        <div className="channel-grid">
          {filtered.map((channel) => (
            <Link key={channel.channelId} to={`/channel/${channel.channelId}`} className="channel-card">
              <div className="channel-card-header">
                <h3 className="channel-name">{channel.channelName}</h3>
                <button
                  className={`bookmark-btn ${bookmarked.has(channel.channelId) ? 'bookmarked' : ''}`}
                  onClick={(e) => handleBookmark(e, channel.channelId)}
                  title={bookmarked.has(channel.channelId) ? '즐겨찾기 해제' : '즐겨찾기 추가'}
                >
                  {bookmarked.has(channel.channelId) ? '★' : '☆'}
                </button>
              </div>
              <p className="channel-meta">
                {channel.categoryName && <span className="tag">{channel.categoryName}</span>}
                <span>개설자: {channel.nickname || '-'}</span>
              </p>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
