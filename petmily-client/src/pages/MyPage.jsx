import React, { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getMyPosts, getScraps, deleteScrap } from '../api/post'
import { getMyChannels, getBookmarks, removeBookmark } from '../api/channel'
import { getMyComments } from '../api/comment'
import { useAuth } from '../context/AuthContext'

const TABS = ['내 게시글', '내 댓글', '스크랩', '즐겨찾기 채널', '내 채널']

export default function MyPage() {
  const { user } = useAuth()
  const [tab, setTab] = useState(0)
  const [data, setData] = useState({})
  const [loading, setLoading] = useState(false)

  const loaders = [
    () => getMyPosts().then((d) => setData((p) => ({ ...p, posts: d || [] }))),
    () => getMyComments().then((d) => setData((p) => ({ ...p, comments: d || [] }))),
    () => getScraps().then((d) => setData((p) => ({ ...p, scraps: d || [] }))),
    () => getBookmarks().then((d) => setData((p) => ({ ...p, bookmarks: d || [] }))),
    () => getMyChannels().then((d) => setData((p) => ({ ...p, myChannels: d || [] }))),
  ]

  useEffect(() => {
    setLoading(true)
    loaders[tab]().catch(() => {}).finally(() => setLoading(false))
  }, [tab])

  const handleDeleteScrap = async (scrapId) => {
    try {
      await deleteScrap(scrapId)
      setData((p) => ({ ...p, scraps: p.scraps.filter((s) => s.scrapId !== scrapId) }))
    } catch {
      alert('스크랩 삭제에 실패했습니다.')
    }
  }

  const handleRemoveBookmark = async (channelId) => {
    try {
      await removeBookmark(channelId)
      setData((p) => ({ ...p, bookmarks: p.bookmarks.filter((b) => b.channelId !== channelId) }))
    } catch {
      alert('즐겨찾기 해제에 실패했습니다.')
    }
  }

  return (
    <div className="container">
      <h1 className="page-title">마이페이지</h1>
      <p className="user-info-text">{user?.email}</p>

      <div className="tab-list">
        {TABS.map((name, i) => (
          <button
            key={i}
            className={`tab-btn ${tab === i ? 'active' : ''}`}
            onClick={() => setTab(i)}
          >
            {name}
          </button>
        ))}
      </div>

      <div className="tab-content">
        {loading && <div className="loading">불러오는 중...</div>}

        {!loading && tab === 0 && (
          <div className="post-list">
            {(data.posts || []).length === 0 ? (
              <p className="empty-text">작성한 게시글이 없습니다.</p>
            ) : (
              (data.posts || []).map((post) => (
                <Link
                  key={post.id}
                  to={`/channel/${post.channelId}/post/${post.id}`}
                  className="post-item post-item-link"
                >
                  <h3 className="post-title">{post.title}</h3>
                  <div className="post-meta">
                    <span>{post.channelName}</span>
                    <span>{post.createDate?.slice(0, 10)}</span>
                    <span>조회 {post.hit}</span>
                  </div>
                </Link>
              ))
            )}
          </div>
        )}

        {!loading && tab === 1 && (
          <div className="comment-list">
            {(data.comments || []).length === 0 ? (
              <p className="empty-text">작성한 댓글이 없습니다.</p>
            ) : (
              (data.comments || []).map((c) => (
                <div key={c.id} className="comment-item">
                  <p className="comment-content">{c.content}</p>
                  <span className="comment-date">{c.createDate?.slice(0, 10)}</span>
                </div>
              ))
            )}
          </div>
        )}

        {!loading && tab === 2 && (
          <div className="post-list">
            {(data.scraps || []).length === 0 ? (
              <p className="empty-text">스크랩한 게시글이 없습니다.</p>
            ) : (
              (data.scraps || []).map((scrap) => (
                <div key={scrap.scrapId} className="post-item">
                  <Link
                    to={`/channel/${scrap.channelId}/post/${scrap.postId}`}
                    className="post-item-link"
                  >
                    <h3 className="post-title">{scrap.title}</h3>
                    <p className="post-meta">{scrap.createDate?.slice(0, 10)}</p>
                  </Link>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => handleDeleteScrap(scrap.scrapId)}
                  >
                    스크랩 취소
                  </button>
                </div>
              ))
            )}
          </div>
        )}

        {!loading && tab === 3 && (
          <div className="channel-grid">
            {(data.bookmarks || []).length === 0 ? (
              <p className="empty-text">즐겨찾기한 채널이 없습니다.</p>
            ) : (
              (data.bookmarks || []).map((ch) => (
                <div key={ch.channelId} className="channel-card">
                  <div className="channel-card-header">
                    <Link to={`/channel/${ch.channelId}`} className="channel-name">{ch.channelName}</Link>
                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() => handleRemoveBookmark(ch.channelId)}
                    >
                      해제
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        )}

        {!loading && tab === 4 && (
          <div className="channel-grid">
            {(data.myChannels || []).length === 0 ? (
              <p className="empty-text">개설한 채널이 없습니다.</p>
            ) : (
              (data.myChannels || []).map((ch) => (
                <Link key={ch.channelId} to={`/channel/${ch.channelId}`} className="channel-card">
                  <h3 className="channel-name">{ch.channelName}</h3>
                </Link>
              ))
            )}
          </div>
        )}
      </div>
    </div>
  )
}
